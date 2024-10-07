package unsw.entities;

import unsw.utils.Angle;
import unsw.response.models.FileInfoResponse;

import java.util.List;
import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransfer;

public class TeleportingSatellite extends Satellite {
    private static final int TELEPORT_IN_ANGLE = 180;
    private static final int TELEPORT_OUT_ANGLE = 360;
    private static final String REMOVE_CHAR = "t";

    public TeleportingSatellite(String satelliteId, String type, double height, Angle position) {
        super(satelliteId, type, height, position);
        setDirection(-1);
        setLinearVelocity(1000);
        setMaxRange(200000);
        setFileStorageLimit(Integer.MAX_VALUE);
        setByteStorageLimit(200);
        setDownloadSpeed(15);
        setUploadSpeed(10);
        getSupportedTypes().remove("ElephantSatellite");
    }

    @Override
    public void move() {
        Angle currentPosition = getPosition();
        Angle newPosition = Angle.fromDegrees(0);
        double currentPositionDegrees = getPosition().toDegrees();

        double offset = Angle.fromRadians(this.getAngularVelocity()).toDegrees();

        if (getDirection() == 1) {
            // Moving Clockwise
            if (currentPositionDegrees >= TELEPORT_IN_ANGLE && currentPositionDegrees <= TELEPORT_IN_ANGLE + offset) {
                newPosition = teleport();
            } else {
                newPosition = currentPosition.subtract(Angle.fromRadians(this.getAngularVelocity()));
            }
        } else {
            // Moving Anti-Clockwise
            if (currentPositionDegrees <= TELEPORT_IN_ANGLE && currentPositionDegrees >= TELEPORT_IN_ANGLE - offset) {
                newPosition = teleport();
            } else {
                newPosition = currentPosition.add(Angle.fromRadians(this.getAngularVelocity()));
            }
        }

        setPosition(newPosition);
    }

    public Angle teleport() {
        // This method violates Law of Demeter, but is necessary to access file that exists in
        // so many locations: destination, source, transferList

        // Reverse Direction
        setDirection(getDirection() * -1);

        // Get all the file transfers going from devices to this satellite
        List<FileTransfer> deviceToSatelliteTransfers = BlackoutController.getFileTransferList().stream()
                .filter(file -> (file.getSourceEntity() instanceof Device) && (file.getDestinationEntity() == this))
                .toList();

        if (!deviceToSatelliteTransfers.isEmpty()) {
            // Remove incomplete files from the destination satellite.
            getFiles().entrySet().removeIf(entry -> !entry.getValue().isFileComplete());

            // Modify file on source device to replace all 't' character.
            for (FileTransfer file : deviceToSatelliteTransfers) {
                String fileName = file.getFileName();
                String fileContent = file.getFileContent();
                String modifiedContent = fileContent.replace(REMOVE_CHAR, "");

                FileInfoResponse updatedFile = new FileInfoResponse(fileName, modifiedContent, modifiedContent.length(),
                        true);
                file.getSourceEntity().getFiles().put(fileName, updatedFile);
            }
        }

        // Get all the file transfers going from this satellite to other satellites and devices
        List<FileTransfer> satelliteToOtherTransfers = BlackoutController.getFileTransferList().stream()
                .filter(file -> (file.getSourceEntity() == this)).toList();

        if (!satelliteToOtherTransfers.isEmpty()) {
            // Modify file on destination entities to replace all 't' character.
            for (FileTransfer file : satelliteToOtherTransfers) {
                String fileName = file.getFileName();

                // Concat downloaded content with the 't byte removed' rest of the content
                String fileContent = file.getFileContent();
                String downloadedContent = fileContent.substring(0, file.getByteTransferred());
                String remainingContent = fileContent.substring(file.getByteTransferred() - 1, fileContent.length())
                        .replace(REMOVE_CHAR, "");
                String modifiedContent = downloadedContent + remainingContent;

                FileInfoResponse updatedFile = new FileInfoResponse(fileName, modifiedContent, modifiedContent.length(),
                        true);
                file.getDestinationEntity().getFiles().put(fileName, updatedFile);
            }
        }

        return Angle.fromDegrees(TELEPORT_OUT_ANGLE);
    }
}
