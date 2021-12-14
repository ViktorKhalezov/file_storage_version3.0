package common;

public class DeleteFileMessage extends AbstractMessage {
    private String fileName;

    public DeleteFileMessage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
