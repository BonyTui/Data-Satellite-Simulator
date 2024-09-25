package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite extends Entity {
    private final double linearVelocity = 2500;
    private final double angularVelocity = linearVelocity / this.getHeight();

    // private int speed;
    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, position, height, type);
    }

    public void move() {
        Angle currentPosition = this.getPosition();
        System.out.println(currentPosition);
        Angle newPosition = this.getPosition().subtract(Angle.fromRadians(angularVelocity));
        this.setPosition(newPosition);
    }
}
