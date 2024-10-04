package unsw.entities;

import unsw.utils.Angle;

public class ElephantSatellite extends StandardSatellite {
    public ElephantSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setDirection(-1);
        setLinearVelocity(2500);
        setMaxRange(400000);
        setFileStorageLimit(Integer.MAX_VALUE);
        setByteStorageLimit(90);
        setDownloadSpeed(20);
        setUploadSpeed(20);
        getSupportedTypes().remove("HandheldDevice");
        getSupportedTypes().remove("TeleportingSatellite");
    }
}
