package client;


import common.AbstractMessage;

public interface Callback {

    void onReceive(AbstractMessage msg);
}
