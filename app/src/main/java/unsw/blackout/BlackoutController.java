package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.blackout.FileTransferException.*;
import unsw.entities.*;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private List<Device> deviceList = new ArrayList<>();
    private List<Satellite> satelliteList = new ArrayList<>();
    private List<FileTransfer> fileTransferList = new ArrayList<FileTransfer>();

    private Entity findEntity(String id) {
        for (Device device : deviceList) {
            if (device.getId().equals(id)) {
                return device;
            }
        }

        for (Satellite satellite : satelliteList) {
            if (satellite.getId().equals(id)) {
                return satellite;
            }
        }

        return null;
    }

    private Device findDevice(String deviceId) {
        for (Device device : deviceList) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    private Satellite findSatellite(String satelliteId) {
        for (Satellite satellite : satelliteList) {
            if (satellite.getId().equals(satelliteId)) {
                return satellite;
            }
        }
        return null;
    }

    public void createDevice(String deviceId, String type, Angle position) {
        Device device = null;
        switch (type) {
        case "HandheldDevice":
            device = new HandheldDevice(deviceId, type, position);
            break;
        case "LaptopDevice":
            device = new LaptopDevice(deviceId, type, position);
            break;
        case "DesktopDevice":
            device = new DesktopDevice(deviceId, type, position);
            break;
        default:
            break;
        }
        deviceList.add(device);
    }

    public void removeDevice(String deviceId) {
        Device device = findDevice(deviceId);
        deviceList.remove(device);
        device = null;
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        Satellite satellite = null;
        switch (type) {
        case "StandardSatellite":
            satellite = new StandardSatellite(satelliteId, type, height, position);
            break;
        case "RelaySatellite":
            satellite = new RelaySatellite(satelliteId, type, height, position);
            break;
        case "TeleportingSatellite":
            satellite = new TeleportingSatellite(satelliteId, type, height, position);
            break;
        case "ElephantSatellite":
            satellite = new ElephantSatellite(satelliteId, type, height, position);
            break;
        default:
            break;
        }
        satelliteList.add(satellite);
    }

    public void removeSatellite(String satelliteId) {
        Satellite satellite = findSatellite(satelliteId);
        satelliteList.remove(satellite);
        satellite = null;
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<>();

        for (Device device : deviceList) {
            deviceIds.add(device.getId());
        }

        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<>();

        for (Satellite satellite : satelliteList) {
            satelliteIds.add(satellite.getId());
        }

        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = findDevice(deviceId);
        FileInfoResponse info = new FileInfoResponse(filename, content, content.length(), true);
        device.getFiles().put(filename, info);
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = findEntity(id);

        if (entity == null) {
            return null;
        } else {
            EntityInfoResponse info = new EntityInfoResponse(entity.getId(), entity.getPosition(), entity.getHeight(),
                    entity.getType(), entity.getFiles());
            return info;
        }
    }

    public void transferFile() {
        List<FileTransfer> completeTransfer = new ArrayList<FileTransfer>();
        for (FileTransfer fileTransfer : fileTransferList) {
            Entity sourceEntity = fileTransfer.getSourceEntity();
            Entity destinationEntity = fileTransfer.getDestinationEntity();
            if (!communicable(sourceEntity.getId(), destinationEntity.getId())) {
                System.out.println("Out of range");
                continue;
            }

            String fileName = fileTransfer.getFileName();

            int numByteTransferred = 0;
            if (sourceEntity instanceof Device) {
                // Device to Satellite
                numByteTransferred = fileTransfer.getByteTransferred() + destinationEntity.getDownloadSpeed();
            } else if (sourceEntity instanceof Satellite) {
                // Satellite to Device/Satellite
                numByteTransferred = fileTransfer.getByteTransferred()
                        + Math.min(sourceEntity.getUploadSpeed(), destinationEntity.getDownloadSpeed());
            }
            System.out.println(numByteTransferred);

            String sourceData = fileTransfer.getFileContent();
            String transferredData = sourceData.substring(0, numByteTransferred);
            boolean isFileComplete = false;

            fileTransfer.setByteTransferred(numByteTransferred);

            // Check if file is completely transferred
            if (transferredData.length() == sourceData.length()) {
                isFileComplete = true;
                completeTransfer.add(fileTransfer);
            }

            FileInfoResponse updatedFile = new FileInfoResponse(fileName, transferredData, sourceData.length(),
                    isFileComplete);
            fileTransfer.getDestinationFileStorage().put(fileName, updatedFile);

        }
        fileTransferList.removeAll(completeTransfer);
    }

    public void simulate() {
        for (Satellite satellite : satelliteList) {
            satellite.move();
        }

        transferFile();
    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<Entity> entityList = new ArrayList<>();
        entityList.addAll(satelliteList);
        entityList.addAll(deviceList);

        List<String> communicableEntities = new ArrayList<>();
        Entity sourceEntity = findEntity(id);

        for (Entity destinationEntity : entityList) {
            double distance = MathsHelper.getDistance(sourceEntity.getHeight(), sourceEntity.getPosition(),
                    destinationEntity.getHeight(), destinationEntity.getPosition());
            if (distance <= sourceEntity.getMaxRange()
                    && MathsHelper.isVisible(sourceEntity.getHeight(), sourceEntity.getPosition(),
                            destinationEntity.getHeight(), destinationEntity.getPosition())
                    && !sourceEntity.getId().equals(destinationEntity.getId())
                    && sourceEntity.getSupportedTypes().contains(destinationEntity.getType())) {
                communicableEntities.add(destinationEntity.getId());
            }
        }

        return communicableEntities;
    }

    private boolean communicable(String sourceId, String destinationId) {
        List<String> communicableEntities = communicableEntitiesInRange(sourceId);
        Entity sourceEntity = findEntity(sourceId);
        Entity destinationEntity = findEntity(destinationId);
        if (communicableEntities.contains(destinationId)
                && sourceEntity.getSupportedTypes().contains(destinationEntity.getType())) {
            return true;
        }
        return false;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity sourceEntity = findEntity(fromId);
        Entity destinationEntity = findEntity(toId);
        FileInfoResponse sourceFile = sourceEntity.getFiles().get(fileName);

        if (sourceFile == null || !sourceFile.isFileComplete()) {
            // Can't find file to send from source
            throw new VirtualFileNotFoundException(fileName);
        } else if (destinationEntity.getFiles().get(fileName) != null) {
            // File already exist at destination
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        // else if (sourceEntity.getUploadSpeed()) {

        // }

        // Begin the transfer
        FileInfoResponse destinationFile = new FileInfoResponse(fileName, "", sourceFile.getFileSize(), false);
        destinationEntity.getFiles().put(fileName, destinationFile);

        // Store file transfer to keep track of each world state
        FileTransfer fileTransfer = new FileTransfer(sourceEntity, destinationEntity, sourceFile.getFilename(),
                sourceFile.getData(), destinationEntity.getFiles(), 0);
        fileTransferList.add(fileTransfer);
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
}
