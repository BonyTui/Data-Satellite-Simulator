package unsw.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

public class Entity {
    private String id;
    private Angle position;
    private double height;
    private String type;
    private int maxRange;
    private double linearVelocity;
    private double angularVelocity;
    private int direction = 1;

    private int fileStorageLimit;
    private int byteStorageLimit;
    private int downloadSpeed;
    private int uploadSpeed;

    private Map<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();
    private List<String> supportedTypes = new ArrayList<>();

    public Entity(String id, Angle position, double height, String type) {
        this.id = id;
        this.position = position;
        this.height = height;
        this.type = type;

        final List<String> allSatellites = new ArrayList<>();
        allSatellites.add("StandardSatellite");
        allSatellites.add("RelaySatellite");
        allSatellites.add("TeleportingSatellite");
        allSatellites.add("ElephantSatellite");

        final List<String> allDevices = new ArrayList<>();
        allSatellites.add("HandheldDevice");
        allSatellites.add("LaptopDevice");
        allSatellites.add("DesktopDevice");

        if (type.equals("StandardSatellite")) {
            this.linearVelocity = 2500;
            this.maxRange = 150000;
            this.fileStorageLimit = 3;
            this.byteStorageLimit = 80;
            this.downloadSpeed = 1;
            this.uploadSpeed = 1;
            supportedTypes.addAll(allDevices);
            supportedTypes.remove("DesktopDevice");
            supportedTypes.addAll(allSatellites);
        } else if (type.equals("TeleportingSatellite")) {
            this.linearVelocity = 1000;
            this.maxRange = 200000;
            this.fileStorageLimit = Integer.MAX_VALUE;
            this.byteStorageLimit = 200;
            this.downloadSpeed = 15;
            this.uploadSpeed = 10;
            supportedTypes.addAll(allDevices);
            supportedTypes.addAll(allSatellites);
            supportedTypes.remove("ElephantSatellite");
        } else if (type.equals("RelaySatellite")) {
            this.linearVelocity = 1500;
            this.maxRange = 300000;
            this.fileStorageLimit = 0;
            this.byteStorageLimit = 0;
            this.downloadSpeed = Integer.MAX_VALUE;
            this.uploadSpeed = Integer.MAX_VALUE;
            supportedTypes.addAll(allDevices);
            supportedTypes.addAll(allSatellites);
        } else if (type.equals("ElephantSatellite")) {
            this.linearVelocity = 2500;
            this.maxRange = 400000;
            this.fileStorageLimit = Integer.MAX_VALUE;
            this.byteStorageLimit = 90;
            this.downloadSpeed = 20;
            this.uploadSpeed = 20;
            supportedTypes.addAll(allDevices);
            supportedTypes.remove("HandheldDevice");
            supportedTypes.addAll(allSatellites);
            supportedTypes.remove("TeleportingSatellite");
        } else if (type.equals("HandheldDevice")) {
            this.maxRange = 50000;
            this.fileStorageLimit = Integer.MAX_VALUE;
            this.byteStorageLimit = Integer.MAX_VALUE;
            this.downloadSpeed = Integer.MAX_VALUE;
            this.uploadSpeed = Integer.MAX_VALUE;
            supportedTypes.addAll(allSatellites);
            supportedTypes.remove("ElephantSatellite");
        } else if (type.equals("LaptopDevice")) {
            this.maxRange = 100000;
            supportedTypes.addAll(allSatellites);
        } else if (type.equals("DesktopDevice")) {
            this.maxRange = 200000;
            supportedTypes.addAll(allSatellites);
            supportedTypes.remove("StandardSatellite");
        }

        this.angularVelocity = linearVelocity / height;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Angle getPosition() {
        return position;
    }

    public double getLinearVelocity() {
        return linearVelocity;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public int getDirection() {
        return direction;
    }

    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    public int getUploadSpeed() {
        return uploadSpeed;
    }

    public int getFileStorageLimit() {
        return fileStorageLimit;
    }

    public int getByteStorageLimit() {
        return byteStorageLimit;
    }

    public double getHeight() {
        return height;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
    }

    public List<String> getSupportedTypes() {
        return supportedTypes;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public void setLinearVelocity(int linearVelocity) {
        this.linearVelocity = maxRange;
    }

    public void move() {
        Angle currentPosition = this.getPosition();
        Angle newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
        this.setPosition(newPosition);
    }
}
