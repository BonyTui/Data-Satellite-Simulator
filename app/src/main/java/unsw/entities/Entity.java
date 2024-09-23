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
    private Map<String, FileInfoResponse> files = new HashMap<String, FileInfoResponse>();

    public Entity(String id, Angle position, double height, String type) {
        this.id = id;
        this.position = position;
        this.height = height;
        this.type = type;
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

    public double getHeight() {
        return height;
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
    }
}
