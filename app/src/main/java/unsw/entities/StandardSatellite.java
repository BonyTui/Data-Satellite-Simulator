package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite extends Entity {
    private final double linearVelocity = 2500;
    private final double angularVelocity = linearVelocity / this.getHeight();

    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, position, height, type);
        if (type == "StandardSatellite") {
            setMaxRange(150000);
        } else if (type == "TeleportingSatellite") {
            setMaxRange(200000);
        } else if (type == "RelaySatellite") {
            setMaxRange(300000);
        }
    }

    public void move() {
        Angle currentPosition = this.getPosition();
        Angle newPosition = currentPosition.subtract(Angle.fromRadians(angularVelocity));
        this.setPosition(newPosition);
    }
}
