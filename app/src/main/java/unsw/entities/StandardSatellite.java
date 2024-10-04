package unsw.entities;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    public StandardSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setLinearVelocity(2500);
        setMaxRange(150000);
        setFileStorageLimit(3);
        setByteStorageLimit(80);
        setDownloadSpeed(1);
        setUploadSpeed(1);
        getSupportedTypes().remove("DesktopDevice");
    }
}
