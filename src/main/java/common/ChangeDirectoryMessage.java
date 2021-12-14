package common;

public class ChangeDirectoryMessage extends AbstractMessage {
    private String command;
    private String newFolderName;

    public ChangeDirectoryMessage(String command, String newFolderName) {
        this.command = command;
        this.newFolderName = newFolderName;
    }

    public String getCommand() {
        return command;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

}
