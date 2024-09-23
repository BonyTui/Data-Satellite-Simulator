package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.entities.*;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private List<Device> deviceList = new ArrayList<>();
    private List<StandardSatellite> satelliteList = new ArrayList<>();

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
        // TODO: Task 2a)
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
        // TODO: Task 2 b)
        return new ArrayList<>();
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
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
