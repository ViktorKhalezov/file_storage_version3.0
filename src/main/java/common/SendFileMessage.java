package common;

public class SendFileMessage extends AbstractMessage {
    private String name;
    private long length;
    private byte[] buffer;
 //   private int partNumber;
    private boolean lastPart;


    public SendFileMessage(String name, long length, byte[] buffer, boolean lastPart) {
        this.name = name;
        this.length = length;
        this.buffer = buffer;
       // this.partNumber = partNumber;
       this.lastPart = lastPart;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public byte[] getBuffer() {
        return buffer;
    }

 /*   public int getPartNumber() {
        return partNumber;
    } */

    public boolean isLastPart() {
        return lastPart;
    }
}

