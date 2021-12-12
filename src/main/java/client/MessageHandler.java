package client;

import common.AbstractMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage>  {

    private final Callback callback;


    public MessageHandler(Callback callback){
        this.callback = callback;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMessage msg) throws Exception {
      callback.onReceive(msg);
    }

}
