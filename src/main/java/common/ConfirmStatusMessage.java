package common;

public class ConfirmStatusMessage extends AbstractMessage {
    private String status;

    public ConfirmStatusMessage(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}

