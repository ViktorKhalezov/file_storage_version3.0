package client;

import common.*;
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
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


@Slf4j
public class ClientNet {
    private SocketChannel channel;
    private Callback callback;
    private static ClientNet clientNet;
    private static ArrayList<String> serverFileList;
    private static boolean authorized;
    private static String folderName;
    private static String fileForDownload;
    private static String status;
    private static Path path;


    private ClientNet(Callback callback) {
      this.callback = callback;
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
                                        new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new MessageHandler(callback)
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
    }


    public void sendMessage(AbstractMessage message) {
        channel.writeAndFlush(message);
    }


    public void authorizing() {

    }

    public String getFileForDownload() {
        return fileForDownload;
    }

    public void setFileForDownload(String fileForDownload) {
        this.fileForDownload = fileForDownload;
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

    public static String getFolderName() {
        return folderName;
    }

    public String getStatus() {
        return status;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    private synchronized static void handleMessage(AbstractMessage message) {
        if(message instanceof AuthResponse) {
            AuthResponse authResponse = (AuthResponse) message;
            if(authResponse.isAuth() == true) {
                authorized = true;
            }
        }

        if(message instanceof FileListMessage) {
            FileListMessage fileListMessage = (FileListMessage) message;
            folderName = fileListMessage.getFolderName();
            if(fileListMessage.getQuantityOfFiles() > 0) {
                serverFileList = fileListMessage.getFileList();
            } else {
                serverFileList = new ArrayList<>();
            }
        }

        if(message instanceof ConfirmStatusMessage) {
            ConfirmStatusMessage confirmStatusMessage = (ConfirmStatusMessage) message;
            status = confirmStatusMessage.getStatus();
        }

        if(message instanceof SendFileMessage) {
            SendFileMessage sendFileMessage = (SendFileMessage) message;
            saveFile(sendFileMessage);
        }

    }


    public void sendFile(Path path) {
        int bufSize = 1024;
        long fileSize = 0;
        byte[] buffer = new byte[bufSize];
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int partNumber = 1;
        int quantityOfParts = (int) fileSize / bufSize + 1;
        String fileName = path.getFileName().toString();

       try(FileInputStream fis = new FileInputStream(path.toString())) {
           while(fis.read(buffer) != -1) {
               if(partNumber == quantityOfParts) {
                   sendMessage(new SendFileMessage(fileName, fileSize, buffer, true));
               } else {
                   sendMessage(new SendFileMessage(fileName, fileSize, buffer, false));
                   partNumber++;
                   buffer = new byte[bufSize];
               }
           }
       } catch (Exception e){
           e.printStackTrace();
       }
    }


    private static void saveFile(SendFileMessage sendFileMessage) {
        String fileName = sendFileMessage.getName();
        Path newFile = Paths.get(path.toAbsolutePath() + "\\" + fileName);
        if (Files.exists(newFile)) {
            try {
                long fileSize = Files.size(newFile);
                if (fileSize >= sendFileMessage.getLength()) {
                    newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                    if (Files.exists(newFile) && Files.size(newFile) >= sendFileMessage.getLength()) {
                        while (true) {
                            newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                            if (!Files.exists(newFile)) {
                                newFile = Paths.get(path.toAbsolutePath() + "\\" + newFile.getFileName().toString().substring(5));
                                break;
                            }
                        }
                        long lastCopySize = Files.size(newFile);
                        if (lastCopySize >= sendFileMessage.getLength()) {
                            newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fis = new FileOutputStream(newFile.toString(), true)) {
            fis.write(sendFileMessage.getBuffer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
