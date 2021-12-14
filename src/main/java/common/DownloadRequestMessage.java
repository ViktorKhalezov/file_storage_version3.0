package common;

public class DownloadRequestMessage extends AbstractMessage {
    private String fileName;
    private String destinationPath;

   public DownloadRequestMessage (String fileName, String destinationPath) {
        this.fileName = fileName;
        this.destinationPath = destinationPath;
    }

    public String getFileName() {
       return fileName;
    }

    public String getDestinationPath() {
       return destinationPath;
    }

}

