 package common;

 public class RenameFileMessage extends AbstractMessage {
     String oldName;
     String newName;


     public RenameFileMessage(String oldName, String newName) {
         this.oldName = oldName;
         this.newName = newName;
     }

     public String getOldName() {
         return oldName;
     }

     public String getNewName() {
         return newName;
     }

 }

