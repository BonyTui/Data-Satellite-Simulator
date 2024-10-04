package unsw.entities;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public abstract class Device extends Entity {
    public Device(String deviceId, String type, Angle position) {
        super(deviceId, position, RADIUS_OF_JUPITER, type);
        setFileStorageLimit(Integer.MAX_VALUE);
        setByteStorageLimit(Integer.MAX_VALUE);
        setDownloadSpeed(Integer.MAX_VALUE);
        setUploadSpeed(Integer.MAX_VALUE);
        getSupportedTypes().addAll(ALLSATELLITES);
    }
}
