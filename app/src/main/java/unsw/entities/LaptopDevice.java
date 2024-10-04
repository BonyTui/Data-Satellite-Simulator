package unsw.entities;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
        setMaxRange(50000);
    }
}
