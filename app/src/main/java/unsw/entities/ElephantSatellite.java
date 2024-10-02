package unsw.entities;

import unsw.utils.Angle;

public class ElephantSatellite extends StandardSatellite {
    public ElephantSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setDirection(-1);
    }
}
