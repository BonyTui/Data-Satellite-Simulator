package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite {
    private String id;
    private String type;
    private double height;
    private Angle position;

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        this.id = satelliteId;
        this.type = type;
        this.height = height;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getHeight() {
        return height;
    }

    public Angle getPosition() {
        return position;
    }

}
