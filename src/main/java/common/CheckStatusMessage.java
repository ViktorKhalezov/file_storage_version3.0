package common;


public class CheckStatusMessage extends AbstractMessage {
    private String nameOfObject;

    public CheckStatusMessage(String nameOfObject) {
        this.nameOfObject = nameOfObject;
    }

    public String getNameOfObject() {
        return nameOfObject;
    }

}


