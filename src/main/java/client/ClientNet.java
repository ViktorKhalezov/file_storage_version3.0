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
      private static String serverFolderName;
      private static String fileForDownload;
      private static String status;
      private static String command;
      private static String fileForRename;
      private static String incorrectNameCommand;
      private static Boolean operationConfirmed;

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

      public String getCommand() {
          return command;
      }

      public void setCommand(String command) {
          this.command = command;
      }

      public String getFileForDownload() {
          return fileForDownload;
      }

      public void setFileForDownload(String fileForDownload) {
          this.fileForDownload = fileForDownload;
      }

      public String getFileForRename() {
          return fileForRename;
      }

      public void setFileForRename(String fileForRename) {
          this.fileForRename = fileForRename;
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

      public static String getServerFolderName() {
          return serverFolderName;
      }

      public String getStatus() {
          return status;
      }

      public String getIncorrectNameCommand() {
          return incorrectNameCommand;
      }

      public void setIncorrectNameCommand(String incorrectNameCommand) {
          this.incorrectNameCommand = incorrectNameCommand;
      }

      public Boolean isOperationConfirmed() {
          return operationConfirmed;
      }

      public void setOperationConfirmed(Boolean operationConfirmed) {
          this.operationConfirmed = operationConfirmed;
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
              serverFolderName = fileListMessage.getFolderName();
              serverFileList = fileListMessage.getFileList();
          }

          if(message instanceof ConfirmStatusMessage) {
              ConfirmStatusMessage confirmStatusMessage = (ConfirmStatusMessage) message;
              status = confirmStatusMessage.getStatus();
          }

          if(message instanceof SendFileMessage) {
              SendFileMessage sendFileMessage = (SendFileMessage) message;
              saveFile(sendFileMessage);
          }

          if(message instanceof OperationConfirmMessage) {
              OperationConfirmMessage operationConfirmMessage = (OperationConfirmMessage) message;
             operationConfirmed = operationConfirmMessage.isOperationConfirmed();
          }

      }


      public void sendFile(Path path) {
          String destinationPath = serverFolderName;
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
                     sendMessage(new SendFileMessage(fileName, destinationPath, fileSize, buffer, true));
                 } else {
                     sendMessage(new SendFileMessage(fileName, destinationPath, fileSize, buffer, false));
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
          String destinationPath = sendFileMessage.getDestinationPath();
          Path newFile = Paths.get(destinationPath + "\\" + fileName);
          if (Files.exists(newFile)) {
              try {
                  long fileSize = Files.size(newFile);
                  if (fileSize >= sendFileMessage.getLength()) {
                      newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
                      if (Files.exists(newFile) && Files.size(newFile) >= sendFileMessage.getLength()) {
                          while (true) {
                              newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
                              if (!Files.exists(newFile)) {
                                  newFile = Paths.get(destinationPath + "\\" + newFile.getFileName().toString().substring(5));
                                  break;
                              }
                          }
                          long lastCopySize = Files.size(newFile);
                          if (lastCopySize >= sendFileMessage.getLength()) {
                              newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
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
