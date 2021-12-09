package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MessageHandler extends SimpleChannelInboundHandler<String>  {
 //   private String message;
 //   private final Callback callback;
//    private ArrayList<String> messageList;
   // private DataInputStream dis;
  //  private byte[] buffer;
  //  ByteArrayInputStream baos;
//   ByteBuf byteBuf;
   // DataOutputStream das;
   private PipedOutputStream pos;

    public MessageHandler(){
      pos = new PipedOutputStream();
     //   messageList = new ArrayList<>();
  //      buffer = new byte[8192];
    //    dis = new DataInputStream(new ByteArrayInputStream(buffer));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
      //  message = msg;
     //   byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
      //  dis(buffer);
     // callback.onReceive(msg);
       // channelHandlerContext.read();
    // byteBuf.readableBytes();
        pos.write(msg.getBytes(StandardCharsets.UTF_8));
     //   pos.flush();
    }

    public PipedOutputStream getPos() {
        return pos;
    }

}
