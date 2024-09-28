package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.vfs.Vfs.File;

import unsw.blackout.FileTransferException.*;
import unsw.entities.Device;
import unsw.entities.Entity;
import unsw.entities.StandardSatellite;
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
    private List<StandardSatellite> satelliteList = new ArrayList<>();
    private List<FileTransfer> fileTransferList = new ArrayList<FileTransfer>();

    private Entity findEntity(String id) {
        for (Device device : deviceList) {
            if (device.getId() == id) {
                return device;
            }
        }

        for (StandardSatellite satellite : satelliteList) {
            if (satellite.getId() == id) {
                return satellite;
            }
        }

        return null;
    }

    private Device findDevice(String deviceId) {
        for (Device device : deviceList) {
            if (device.getId() == deviceId) {
                return device;
            }
        }
        return null;
    }

    private StandardSatellite findSatellite(String satelliteId) {
        for (StandardSatellite satellite : satelliteList) {
            if (satellite.getId() == satelliteId) {
                return satellite;
            }
        }
        return null;
    }

    public void createDevice(String deviceId, String type, Angle position) {
        Device device = new Device(deviceId, type, position);
        deviceList.add(device);
    }

    public void removeDevice(String deviceId) {
        Device device = findDevice(deviceId);
        deviceList.remove(device);
        device = null;
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        StandardSatellite satellite = new StandardSatellite(satelliteId, type, height, position);
        satelliteList.add(satellite);
    }

    public void removeSatellite(String satelliteId) {
        StandardSatellite satellite = findSatellite(satelliteId);
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

        for (StandardSatellite satellite : satelliteList) {
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

    public void simulate() {
        for (StandardSatellite satellite : satelliteList) {
            satellite.move();
        }

        List<FileTransfer> completeTransfer = new ArrayList<FileTransfer>();
        for (FileTransfer fileTransfer : fileTransferList) {
            String fileName = fileTransfer.getFileName();
            int numByteTransferred = fileTransfer.getByteTransferred() + 1;
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

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    private List<String> communicableSatellitesInRangeOfDevice(Entity entity) {
        List<String> communicableSatellites = new ArrayList<>();
        for (StandardSatellite satellite : satelliteList) {
            double distance = MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(),
                    entity.getPosition());
            if (((distance <= entity.getMaxRange()) || (distance <= satellite.getMaxRange()))
                    && MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), entity.getPosition())
                    && satellite.getId() != entity.getId()) {
                communicableSatellites.add(satellite.getId());
            }
        }
        return communicableSatellites;
    }

    private List<String> communicableSatellitesInRangeOfSatellite(Entity entity) {
        List<String> communicableSatellites = new ArrayList<>();
        for (StandardSatellite satellite : satelliteList) {
            double distance = MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(),
                    entity.getHeight(), entity.getPosition());
            if (((distance <= entity.getMaxRange()) || (distance <= satellite.getMaxRange())) && MathsHelper
                    .isVisible(satellite.getHeight(), satellite.getPosition(), entity.getHeight(), entity.getPosition())
                    && satellite.getId() != entity.getId()) {
                communicableSatellites.add(satellite.getId());
            }
        }
        return communicableSatellites;
    }

    private List<String> communicableDeviceInRange(Entity entity) {
        List<String> communicableDevices = new ArrayList<>();
        for (Device device : deviceList) {
            double distance = MathsHelper.getDistance(entity.getHeight(), entity.getPosition(), device.getPosition());
            if (((distance <= entity.getMaxRange()) || (distance <= device.getMaxRange()))
                    && MathsHelper.isVisible(entity.getHeight(), entity.getPosition(), device.getPosition())
                    && device.getId() != entity.getId()) {
                communicableDevices.add(device.getId());
            }
        }
        return communicableDevices;
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String> communicableEntities = new ArrayList<>();
        Entity entity = findEntity(id);

        if (entity.getClass() == Device.class) {
            for (String satelliteId : communicableSatellitesInRangeOfDevice(entity)) {
                communicableEntities.add(satelliteId);
            }
        } else if (entity.getClass() == StandardSatellite.class) {
            for (String satelliteId : communicableSatellitesInRangeOfSatellite(entity)) {
                communicableEntities.add(satelliteId);
            }
            for (String deviceId : communicableDeviceInRange(entity)) {
                communicableEntities.add(deviceId);
            }
        }
        return communicableEntities;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity sourceEntity = findEntity(fromId);
        Entity destinationEntity = findEntity(toId);
        FileInfoResponse sourceFile = sourceEntity.getFiles().get(fileName);

        if (sourceFile == null) {
            // Can't find file to send from source
            throw new VirtualFileNotFoundException(fileName);
        } else if (destinationEntity.getFiles().get(fileName) != null) {
            // File already exist at destination
            throw new VirtualFileAlreadyExistsException(fileName);
        }

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
