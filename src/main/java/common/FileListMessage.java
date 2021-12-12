package common;

import java.util.ArrayList;

public class FileListMessage extends AbstractMessage {
    private String folderName;
    private int quantityOfFiles;
    private ArrayList<String> fileList;


    public FileListMessage(String folderName, int quantityOfFiles, ArrayList<String> fileList) {
        this.folderName = folderName;
        this.quantityOfFiles = quantityOfFiles;
        this.fileList = fileList;
    }

    public int getQuantityOfFiles() {
        return quantityOfFiles;
    }

    public ArrayList<String> getFileList() {
        return fileList;
    }

    public String getFolderName() {
        return folderName;
    }
}
