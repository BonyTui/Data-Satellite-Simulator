package unsw.entities;

import unsw.utils.Angle;

public class RelaySatellite extends StandardSatellite {
    public RelaySatellite(String satelliteId, String type, double height, Angle position, double linearVelocity) {
        super(satelliteId, type, height, position, linearVelocity);
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
}
