package unsw.entities;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private String id;
    private Angle position;
    private double height;
    private String type;
    private int maxRange;
    private double linearVelocity;
    private double angularVelocity;
    private int direction = 1;
    private int downloadSpeed;
    private int uploadSpeed;
    private Map<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();

    public Entity(String id, Angle position, double height, String type, double linearVelocity) {
        this.id = id;
        this.position = position;
        this.height = height;
        this.type = type;
        this.linearVelocity = linearVelocity;
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

    public double getHeight() {
        return height;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
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

    public void move() {
        Angle currentPosition = this.getPosition();
        Angle newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
        this.setPosition(newPosition);
    }
}
