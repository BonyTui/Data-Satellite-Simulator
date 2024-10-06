package unsw.entities;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setLinearVelocity(1500);
        setMaxRange(300000);
        setFileStorageLimit(0);
        setByteStorageLimit(0);
        setDownloadSpeed(Integer.MAX_VALUE);
        setUploadSpeed(Integer.MAX_VALUE);
        getSupportedTypes().remove("DesktopDevice");
    }

    @Override
    public void move() {
        int halfwayPoint = 345;
        Angle currentPosition = getPosition();
        Angle newPosition;
        double currentPositionDegrees = getPosition().toDegrees();

        if (currentPositionDegrees <= 140
                || (currentPositionDegrees >= halfwayPoint && currentPositionDegrees <= 360)) {
            setDirection(-1);
        } else if (currentPositionDegrees >= 190 && currentPositionDegrees < halfwayPoint) {
            setDirection(1);
        }

        if (getDirection() == 1) {
            newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
        } else {
            newPosition = currentPosition.add(Angle.fromRadians(this.getAngularVelocity()));
        }

        setPosition(newPosition);
    }

    public void relay() {

    }
}
