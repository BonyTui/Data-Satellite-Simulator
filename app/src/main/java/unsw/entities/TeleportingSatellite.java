package unsw.entities;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setDirection(-1);
        setLinearVelocity(1000);
        setMaxRange(200000);
        setFileStorageLimit(Integer.MAX_VALUE);
        setByteStorageLimit(200);
        setDownloadSpeed(15);
        setUploadSpeed(10);
        getSupportedTypes().remove("ElephantSatellite");
    }

    @Override
    public void move() {
        Angle currentPosition = getPosition();
        Angle newPosition = Angle.fromDegrees(0);
        double currentPositionDegrees = getPosition().toDegrees();

        double offset = Angle.fromRadians(this.getAngularVelocity()).toDegrees();

        if (getDirection() == 1) {
            // Moving Clockwise
            if (currentPositionDegrees >= 180 && currentPositionDegrees <= 180 + offset) {
                newPosition = Angle.fromDegrees(360);
                setDirection(-1);
            } else {
                newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
            }
        } else {
            // Moving Anti-Clockwise
            if (currentPositionDegrees <= 180 && currentPositionDegrees >= 180 - offset) {
                newPosition = Angle.fromDegrees(360);
                setDirection(1);
            } else {
                newPosition = currentPosition.add(Angle.fromRadians(this.getAngularVelocity()));
            }
        }

        setPosition(newPosition);
    }
}
