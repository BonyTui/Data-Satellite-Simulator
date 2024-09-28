package unsw.blackout;

import unsw.entities.Entity;
import unsw.response.models.FileInfoResponse;

import java.util.Map;

public class FileTransfer {
    private Entity sourceEntity;
    private Entity destinationEntity;
    private String fileName;
    private String fileContent;
    private Map<String, FileInfoResponse> destinationFileStorage;
    private int byteTransferred;

    public FileTransfer(Entity sourceEntity, Entity destinationEntity, String fileName, String fileContent,
            Map<String, FileInfoResponse> destinationFileStorage, int byteTransferred) {
        this.sourceEntity = sourceEntity;
        this.destinationEntity = destinationEntity;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.destinationFileStorage = destinationFileStorage;
        this.byteTransferred = byteTransferred;
    }

    public Entity getSourceEntity() {
        return sourceEntity;
    }

    public Entity getDestinationEntity() {
        return destinationEntity;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public Map<String, FileInfoResponse> getDestinationFileStorage() {
        return destinationFileStorage;
    }

    public int getByteTransferred() {
        return byteTransferred;
    }

    public void setByteTransferred(int byteTransferred) {
        this.byteTransferred = byteTransferred;
    }

}
