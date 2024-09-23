package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite extends Entity {
    // private int speed;
    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, position, height, type);
    }
}
