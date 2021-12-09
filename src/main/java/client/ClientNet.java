package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;


@Slf4j
public class ClientNet {
    private SocketChannel channel;
   private Callback callback;
   private String message;
    private MessageHandler messageHandler;
    private static ClientNet clientNet;
    private PipedInputStream pis;
    private final byte[] buf;
    private static ArrayList<String> serverFileList;
    private static boolean authorized;

    private ClientNet(Callback callback) {
      this.callback = callback;
      buf = new byte[8192];
     messageHandler = new MessageHandler();
        new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                ChannelFuture future = bootstrap.channel(NioSocketChannel.class)
                        .group(group)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                channel = ch;
                                ch.pipeline().addLast(
                                   //     new ObjectEncoder(),
                                   //  new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new StringEncoder(),
                                        new StringDecoder(),
                                    //   messageHandler
                                      //   new MessageHandler()
                                        messageHandler
                                   //     new ClientMessageHandler(callback)
                                );
                            }
                        }).connect("localhost", 8189).sync();
                future.channel().closeFuture().sync(); // block

            } catch (Exception e) {
                log.error("e=", e);
            } finally {
                group.shutdownGracefully();
            }
        }).start();
        Thread readThread = new Thread(() -> {
            readMessages(messageHandler.getPos());
        });
        readThread.setDaemon(true);
        readThread.start();
    }


    public void sendMessage(String message) {
        channel.writeAndFlush(message);
    }

    protected void readMessages(PipedOutputStream pos) {
        try {
            pis = new PipedInputStream(pos);
            while(true) {
             //  try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pis))) {
                    int read = pis.read(buf);
                    String msg = new String(buf, 0, read).trim();
               //     String msg = bufferedReader.readLine();
                    //     String msg = String.valueOf(pis.read());
                callback.onReceive(msg);
             //   }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PipedInputStream getPis() {
        return pis;
    }

    public void authorizing() {

    }

   /* public String readMessages(){
        String msg ="";
        while (true) {
            msg = String.valueOf(channel.read());
        }
        return msg;
    } */

    public SocketChannel getChannel() {
        return channel;
    }


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

 /*   public static String receiveMessage(String message) {
        return message;
    } */

  /*  public String receiveMessage() {
        return String.valueOf(channel.read());
    } */

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }



    public static synchronized ClientNet getClientNet() {
        if(clientNet == null) {
            clientNet = new ClientNet(msg -> handleMessage(msg));
        }
        return clientNet;
    }

    public ArrayList<String> getServerFileList() {
        return serverFileList;
    }

    public boolean isAuthorized () {
        return authorized;
    }

    private synchronized static void handleMessage(String message) {
      // System.out.println(message);
       String[] strArray = message.split("\n");
       for(int i = 0; i < strArray.length; i++) {
           System.out.println(i+ ": " + strArray[i]);
       }
       if(strArray[0].equals("enter")) {
           int quantityOfFiles = Integer.parseInt(strArray[1]);
           if(quantityOfFiles > 0){
               serverFileList = new ArrayList<>();
               for(int i = 2; i < quantityOfFiles; i++) {
                   serverFileList.add(strArray[i]);
               }
           }
            authorized = true;
           clientNet.sendMessage(String.valueOf(authorized));
       }

     //   System.out.println(message);
    }


}
