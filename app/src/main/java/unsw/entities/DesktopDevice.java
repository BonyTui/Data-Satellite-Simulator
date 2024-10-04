package unsw.entities;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public DesktopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
        setMaxRange(200000);
        getSupportedTypes().remove("StandardSatellite");
    }
}
