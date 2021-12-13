 package common;

 public class SendFileMessage extends AbstractMessage {
     private String name;
     private String destinationPath;
     private long length;
     private byte[] buffer;
  // private int partNumber;
     private boolean lastPart;


     public SendFileMessage(String name, String destinationPath, long length, byte[] buffer, boolean lastPart) {
         this.name = name;
         this.destinationPath = destinationPath;
         this.length = length;
         this.buffer = buffer;
        // this.partNumber = partNumber;
        this.lastPart = lastPart;
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

     public boolean isLastPart() {
         return lastPart;
     }

 }

