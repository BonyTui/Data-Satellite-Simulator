package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite extends Entity {
    public StandardSatellite(String satelliteId, String type, double height, Angle position, double linearVelocity) {
        super(satelliteId, position, height, type, linearVelocity);

        if (type == "StandardSatellite") {
            setMaxRange(150000);
        } else if (type == "TeleportingSatellite") {
            setMaxRange(200000);
        } else if (type == "RelaySatellite") {
            setMaxRange(300000);
        }
    }
}
