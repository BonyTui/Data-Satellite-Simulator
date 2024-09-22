package unsw.blackout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import unsw.entities.*;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import java.io.FileWriter;
import java.io.IOException;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private List<Device> deviceList = new ArrayList<>();
    private List<StandardSatellite> satelliteList = new ArrayList<>();

    private Object findEntity(String id) {
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

        File file = new File(filename);
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        device.getFiles().add(file);
    }

    public EntityInfoResponse getInfo(String id) {
        Object entity = findEntity(id);

        if (entity == null) {
            return null;
        } else {
            if (entity instanceof Device) {
                Device device = (Device) entity;
                int deviceHeight = 0;
                EntityInfoResponse deviceInfo = new EntityInfoResponse(device.getId(), device.getPosition(),
                        deviceHeight, device.getType());
                return deviceInfo;
            } else if (entity instanceof StandardSatellite) {
                StandardSatellite satellite = (StandardSatellite) entity;
                EntityInfoResponse satelliteInfo = new EntityInfoResponse(satellite.getId(), satellite.getPosition(),
                        satellite.getHeight(), satellite.getType());
                return satelliteInfo;
            }
        }
        return null;
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
