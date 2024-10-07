package unsw.blackout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unsw.blackout.FileTransferException.VirtualFileAlreadyExistsException;
import unsw.blackout.FileTransferException.VirtualFileNoBandwidthException;
import unsw.blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import unsw.blackout.FileTransferException.VirtualFileNotFoundException;
import unsw.entities.DesktopDevice;
import unsw.entities.Device;
import unsw.entities.ElephantSatellite;
import unsw.entities.Entity;
import unsw.entities.HandheldDevice;
import unsw.entities.LaptopDevice;
import unsw.entities.RelaySatellite;
import unsw.entities.Satellite;
import unsw.entities.StandardSatellite;
import unsw.entities.TeleportingSatellite;
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
    private static List<FileTransfer> fileTransferList = new ArrayList<FileTransfer>();

    public static List<FileTransfer> getFileTransferList() {
        return fileTransferList;
    }

    private Entity findEntity(String id) {
        Entity entity = findDevice(id);
        if (entity != null) {
            return entity;
        } else {
            return findSatellite(id);
        }
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

    private void transferFile() {
        // Violates Law of Demeter in order to send files, Blackoutcontroller needs to access File Transfer
        // in order to access the Source and Destination
        List<FileTransfer> completeTransfer = new ArrayList<FileTransfer>();
        for (FileTransfer fileTransfer : fileTransferList) {
            Entity sourceEntity = fileTransfer.getSourceEntity();
            Entity destinationEntity = fileTransfer.getDestinationEntity();
            String fileName = fileTransfer.getFileName();
            if (!communicable(sourceEntity.getId(), destinationEntity.getId())) {
                if (!(destinationEntity instanceof ElephantSatellite)) {
                    // If not ElephantSatellite, file is transient and will resume downloading when back in range. Else, delete.
                    destinationEntity.getFiles().entrySet().removeIf(entry -> !entry.getValue().isFileComplete()
                            && entry.getValue().getFilename().equals(fileName));
                }
                fileTransfer = null;
                continue;
            }

            // Distribute bytes fairly
            int numFilesUploading = fileTransferList.stream()
                    .filter(file -> file.getSourceEntity().equals(sourceEntity)).toList().size();
            int numFilesDownloading = fileTransferList.stream()
                    .filter(file -> file.getDestinationEntity().equals(destinationEntity)).toList().size();

            if (destinationEntity instanceof ElephantSatellite) {
                int numTransientFiles = fileTransferList.stream()
                        .filter(file -> !communicable(sourceEntity.getId(), destinationEntity.getId())).toList().size();
                numFilesDownloading -= numTransientFiles;
            }
            int uploadBandwidthDistribution = sourceEntity.getUploadSpeed() / (numFilesUploading);
            int downloadBandwidthDistribution = destinationEntity.getDownloadSpeed() / (numFilesDownloading);
            int numByteTransferred = 0;
            if (sourceEntity instanceof Device) {
                // Device to Satellite
                numByteTransferred = fileTransfer.getByteTransferred() + downloadBandwidthDistribution;
            } else if (sourceEntity instanceof Satellite) {
                // Satellite to Device/Satellite
                numByteTransferred = fileTransfer.getByteTransferred()
                        + Math.min(uploadBandwidthDistribution, downloadBandwidthDistribution);
            }

            String sourceData = fileTransfer.getFileContent();

            numByteTransferred = Math.min(numByteTransferred, sourceData.length());
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
        return communicableEntitiesInRange(id, findEntity(id), new HashSet<>());
    }

    private List<String> communicableEntitiesInRange(String id, Entity originalSourceEntity,
            Set<String> visitedEntities) {
        List<Entity> entityList = new ArrayList<>();
        entityList.addAll(satelliteList);
        entityList.addAll(deviceList);

        List<Entity> communicableRelays = new ArrayList<>();
        List<String> communicableEntities = new ArrayList<>();
        Entity sourceEntity = findEntity(id);
        if (sourceEntity == null) {
            return communicableEntities;
        }

        // Mark the source entity as visited
        visitedEntities.add(id);

        for (Entity destinationEntity : entityList) {
            // Normal case
            double distance = MathsHelper.getDistance(sourceEntity.getHeight(), sourceEntity.getPosition(),
                    destinationEntity.getHeight(), destinationEntity.getPosition());
            if (distance <= sourceEntity.getMaxRange()
                    && MathsHelper.isVisible(sourceEntity.getHeight(), sourceEntity.getPosition(),
                            destinationEntity.getHeight(), destinationEntity.getPosition())
                    && !sourceEntity.getId().equals(destinationEntity.getId())
                    && originalSourceEntity.getSupportedTypes().contains(destinationEntity.getType())
                    && !visitedEntities.contains(destinationEntity.getId())) {

                communicableEntities.add(destinationEntity.getId());

                if (destinationEntity instanceof RelaySatellite) {
                    communicableRelays.add(destinationEntity);
                }
            }
        }

        // Check for mutual connections via relay
        for (Entity relaySatellite : communicableRelays) {
            List<String> mutualConnections = communicableEntitiesInRange(relaySatellite.getId(), originalSourceEntity,
                    visitedEntities);
            communicableEntities.addAll(mutualConnections);
        }

        // Remove duplicates
        Set<String> communicableEntitiesSet = new HashSet<>(communicableEntities);
        communicableEntities.clear();
        communicableEntities.addAll(communicableEntitiesSet);
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

        // Can't find file to send from source
        if (sourceFile == null || !sourceFile.isFileComplete()) {
            throw new VirtualFileNotFoundException(fileName);
        }

        // Check bandwidth
        int downloadBandwidth = destinationEntity.getDownloadSpeed();
        int numFilesDownloading = fileTransferList.stream()
                .filter(file -> file.getDestinationEntity().equals(destinationEntity)).toList().size();

        // Download bandwidth limit
        if (numFilesDownloading + 1 > downloadBandwidth) {
            throw new VirtualFileNoBandwidthException(toId);
        }

        int uploadBandwidth = sourceEntity.getUploadSpeed();
        int numFilesUploading = fileTransferList.stream().filter(file -> file.getSourceEntity().equals(sourceEntity))
                .toList().size();

        // Upload bandwidth limit
        if (numFilesUploading + 1 > uploadBandwidth) {
            throw new VirtualFileNoBandwidthException(fromId);
        }

        // File already exist at destination
        if (destinationEntity.getFiles().get(fileName) != null) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }

        // Check storage
        int fileLimit = destinationEntity.getFileStorageLimit();
        int fileUsage = destinationEntity.getFiles().size();
        int newFileUsage = fileUsage + 1;
        if (newFileUsage > fileLimit) {
            // File limit reached
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        int byteLimit = destinationEntity.getByteStorageLimit();
        int byteUsage = destinationEntity.getFiles().values().stream().map(file -> file.getFileSize()).reduce(0,
                (file1, file2) -> (file1 + file2));
        int newByteUsage = byteUsage + sourceFile.getFileSize();
        if (newByteUsage > byteLimit) {
            // File limit reached
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        // Begin the transfer
        FileInfoResponse destinationFile = new FileInfoResponse(fileName, "", sourceFile.getFileSize(), false);
        destinationEntity.getFiles().put(fileName, destinationFile);

        // Store file transfer to keep track of each world state
        FileTransfer fileTransfer = new FileTransfer(sourceEntity, destinationEntity, sourceFile.getFilename(),
                sourceFile.getData(), destinationEntity.getFiles(), sourceEntity.getFiles(), 0);
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
