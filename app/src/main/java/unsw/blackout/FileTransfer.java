package unsw.blackout;

import unsw.entities.Entity;
import unsw.response.models.FileInfoResponse;

import java.util.Map;

public class FileTransfer {
    private Entity sourceEntity;
    private Entity destinationEntity;
    private String fileName;
    private String fileContent;
    private Map<String, FileInfoResponse> sourceFileStorage;;
    private Map<String, FileInfoResponse> destinationFileStorage;
    private int byteTransferred;

    public FileTransfer(Entity sourceEntity, Entity destinationEntity, String fileName, String fileContent,
            Map<String, FileInfoResponse> destinationFileStorage, Map<String, FileInfoResponse> sourceFileStorage,
            int byteTransferred) {
        this.sourceEntity = sourceEntity;
        this.destinationEntity = destinationEntity;
        this.sourceFileStorage = sourceFileStorage;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.destinationFileStorage = destinationFileStorage;
        this.byteTransferred = byteTransferred;
    }

    public Map<String, FileInfoResponse> getSourceFileStorage() {
        return sourceFileStorage;
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

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
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
