package unsw.entities;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class Device extends Entity {
    public Device(String deviceId, String type, Angle position) {
        super(deviceId, position, RADIUS_OF_JUPITER, type);
    }
}
