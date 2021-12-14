package common;

public class SendFileMessage extends AbstractMessage {
    private String name;
    private String destinationPath;
    private long length;
    private byte[] buffer;
    private int partNumber;


    public SendFileMessage(String name, String destinationPath, long length, byte[] buffer, int partNumber) {
        this.name = name;
        this.destinationPath = destinationPath;
        this.length = length;
        this.buffer = buffer;
        this.partNumber = partNumber;
    }

    public String getName() {
        return name;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public long getLength() {
        return length;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getPartNumber() {
        return partNumber;
    }

}

