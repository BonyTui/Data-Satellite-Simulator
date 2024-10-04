package unsw.entities;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
        setMaxRange(100000);
        getSupportedTypes().remove("ElephantSatellite");
    }
}
