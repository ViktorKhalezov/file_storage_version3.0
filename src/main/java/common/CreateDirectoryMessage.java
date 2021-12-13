 package common;

 public class CreateDirectoryMessage extends AbstractMessage {
     String directoryName;

     public CreateDirectoryMessage(String directoryName) {
         this.directoryName = directoryName;
     }

     public String getDirectoryName() {
         return directoryName;
     }

 }
