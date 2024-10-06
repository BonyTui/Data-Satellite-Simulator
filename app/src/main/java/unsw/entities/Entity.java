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

    public static final List<String> ALLSATELLITES = new ArrayList<>();
    public static final List<String> ALLDEVICES = new ArrayList<>();

    public Entity(String id, Angle position, double height, String type) {
        this.id = id;
        this.position = position;
        this.height = height;
        this.type = type;

        ALLSATELLITES.clear();
        ALLDEVICES.clear();

        ALLSATELLITES.add("StandardSatellite");
        ALLSATELLITES.add("RelaySatellite");
        ALLSATELLITES.add("TeleportingSatellite");
        ALLSATELLITES.add("ElephantSatellite");

        ALLDEVICES.add("HandheldDevice");
        ALLDEVICES.add("LaptopDevice");
        ALLDEVICES.add("DesktopDevice");
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
        setAngularVelocity(getLinearVelocity() / getHeight());
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

    public void setLinearVelocity(double linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public void setFileStorageLimit(int fileStorageLimit) {
        this.fileStorageLimit = fileStorageLimit;
    }

    public void setByteStorageLimit(int byteStorageLimit) {
        this.byteStorageLimit = byteStorageLimit;
    }

    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public void setUploadSpeed(int uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public int getFileStorageLimit() {
        return fileStorageLimit;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
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
        this.linearVelocity = linearVelocity;
    }

    public void move() {
        Angle currentPosition = this.getPosition();
        Angle newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
        this.setPosition(newPosition);
    }
}
