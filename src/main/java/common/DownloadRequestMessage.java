package common;

public class DownloadRequestMessage extends AbstractMessage {
    private String fileName;

   public DownloadRequestMessage (String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
       return fileName;
    }

}

