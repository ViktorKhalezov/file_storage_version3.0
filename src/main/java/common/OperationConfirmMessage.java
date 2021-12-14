package common;

public class OperationConfirmMessage extends AbstractMessage {
    Boolean operationConfirmed;

    public OperationConfirmMessage(Boolean operationConfirmed) {
        this.operationConfirmed = operationConfirmed;
    }

    public Boolean isOperationConfirmed() {
        return operationConfirmed;
    }

    public void setOperationConfirmed(Boolean operationConfirmed) {
        this.operationConfirmed = operationConfirmed;
    }

}
