package unsw.entities;

import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

public class Device {
    private String id;
    private String type;
    private Angle position;
    private int maxRange;
    private List<File> files = new ArrayList<>();

    public Device(String deviceId, String type, Angle position) {
        this.id = deviceId;
        this.type = type;
        this.position = position;

        if (type == "HandheldDevice") {
            maxRange = 50000;
        } else if (type == "LaptopDevice") {
            maxRange = 100000;
        } else if (type == "DesktopDevice") {
            maxRange = 200000;
        }
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

    public int getMaxRange() {
        return maxRange;
    }

    public List<File> getFiles() {
        return files;
    }
}
