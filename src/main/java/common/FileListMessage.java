 package common;

 import java.util.ArrayList;

 public class FileListMessage extends AbstractMessage {
     private String folderName;
     private ArrayList<String> fileList;

     public FileListMessage(String folderName, ArrayList<String> fileList) {
         this.folderName = folderName;
         this.fileList = fileList;
     }

     public ArrayList<String> getFileList() {
         return fileList;
     }

     public String getFolderName() {
         return folderName;
     }
 }

