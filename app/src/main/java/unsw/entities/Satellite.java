package unsw.entities;

import unsw.utils.Angle;

public abstract class Satellite extends Entity {
    public Satellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, position, height, type);
        getSupportedTypes().addAll(ALLDEVICES);
        getSupportedTypes().addAll(ALLSATELLITES);
    }
}
