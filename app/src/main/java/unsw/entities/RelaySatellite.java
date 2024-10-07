package unsw.entities;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite implements Movable {
    private static final int INTERVAL_POINT_1 = 140;
    private static final int INTERVAL_POINT_2 = 190;
    private static final int INTERVAL_POINT_3 = 360;
    private static final int HALFWAY_POINT = 345;

    public RelaySatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setLinearVelocity(1500);
        setMaxRange(300000);
        setFileStorageLimit(0);
        setByteStorageLimit(0);
        setDownloadSpeed(Integer.MAX_VALUE);
        setUploadSpeed(Integer.MAX_VALUE);
    }

    @Override
    public void move() {
        Angle currentPosition = getPosition();
        Angle newPosition;
        double currentPositionDegrees = getPosition().toDegrees();

        if (currentPositionDegrees <= INTERVAL_POINT_1
                || (currentPositionDegrees >= HALFWAY_POINT && currentPositionDegrees <= INTERVAL_POINT_3)) {
            setDirection(-1);
        } else if (currentPositionDegrees >= INTERVAL_POINT_2 && currentPositionDegrees < HALFWAY_POINT) {
            setDirection(1);
        }

        if (getDirection() == 1) {
            newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
        } else {
            newPosition = currentPosition.add(Angle.fromRadians(this.getAngularVelocity()));
        }

        setPosition(newPosition);
    }
}
