package unsw.entities;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class Device extends Entity {
    private int maxRange;

    public Device(String deviceId, String type, Angle position) {
        super(deviceId, position, RADIUS_OF_JUPITER, type);

        if (type == "HandheldDevice") {
            maxRange = 50000;
        } else if (type == "LaptopDevice") {
            maxRange = 100000;
        } else if (type == "DesktopDevice") {
            maxRange = 200000;
        }
    }

    public int getMaxRange() {
        return maxRange;
    }
}
