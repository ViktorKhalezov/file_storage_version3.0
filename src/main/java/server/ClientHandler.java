package server;

import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    Path path;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractMessage msg) throws Exception {
        if(msg instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) msg;
            if(authMessage.getLogin().equals("Viktor") && authMessage.getPassword().equals("123")) {
                AuthResponse authResponse = new AuthResponse(true);
                channelHandlerContext.writeAndFlush(authResponse);
                path = Paths.get("server_files");
                FileListMessage fileListMessage = getFileListMessage(path);
                channelHandlerContext.writeAndFlush(fileListMessage);
            }
        }

        if(msg instanceof SendFileMessage) {
            SendFileMessage sendFileMessage = (SendFileMessage) msg;
                saveFile(sendFileMessage);
            if(sendFileMessage.isLastPart() == true) {
                channelHandlerContext.writeAndFlush(getFileListMessage(path));
            }
        }

        if(msg instanceof CheckStatusMessage) {
            CheckStatusMessage checkStatusMessage = (CheckStatusMessage) msg;
            String nameOfObject = checkStatusMessage.getNameOfObject();
            if(nameOfObject != null) {
                channelHandlerContext.writeAndFlush(new ConfirmStatusMessage(checkStatus(nameOfObject)));
            }
        }


        if(msg instanceof DownloadRequestMessage) {
            DownloadRequestMessage downloadRequestMessage = (DownloadRequestMessage) msg;
            Path pathToFile = Paths.get(path.toAbsolutePath() + "\\" + ((DownloadRequestMessage) msg).getFileName());
            sendFile(pathToFile, channelHandlerContext);
        }

    }


    private FileListMessage getFileListMessage(Path path) {
        try {
            int quantityOfFiles = Files.list(path).collect(Collectors.toList()).size();
         if(quantityOfFiles > 0) {
            ArrayList<String> fileList = (ArrayList<String>) Files.list(path).map(file -> file.getFileName().toString()).collect(Collectors.toList());
            log.debug(path.toString());
            return new FileListMessage(path.toString(), quantityOfFiles, fileList);
         }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveFile(SendFileMessage sendFileMessage) {
        String fileName = sendFileMessage.getName();
        log.debug(fileName);
        Path newFile = Paths.get(path.toAbsolutePath() + "\\" + fileName);
        log.debug(newFile.getFileName().toString());
        if(Files.exists(newFile)) {
            try {
                long fileSize = Files.size(newFile);
                if(fileSize >= sendFileMessage.getLength()) {
                    newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                    if(Files.exists(newFile) && Files.size(newFile) >= sendFileMessage.getLength()) {
                        while (true) {
                            newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                            if (!Files.exists(newFile)) {
                                newFile = Paths.get(path.toAbsolutePath() + "\\" + newFile.getFileName().toString().substring(5));
                                log.debug(newFile.getFileName().toString());
                                break;
                            }
                        }
                        long lastCopySize = Files.size(newFile);
                        if (lastCopySize >= sendFileMessage.getLength()) {
                            newFile = Paths.get(path.toAbsolutePath() + "\\copy " + newFile.getFileName());
                            log.debug(newFile.getFileName().toString());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(FileOutputStream fis = new FileOutputStream(newFile.toString(), true)) {
            fis.write(sendFileMessage.getBuffer());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendFile(Path path, ChannelHandlerContext channelHandlerContext) {
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
                    channelHandlerContext.writeAndFlush(new SendFileMessage(fileName, fileSize, buffer, true));
                    channelHandlerContext.writeAndFlush(getFileListMessage(path));
                } else {
                    channelHandlerContext.writeAndFlush(new SendFileMessage(fileName, fileSize, buffer, false));
                    partNumber++;
                    buffer = new byte[bufSize];
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private String checkStatus(String nameOfObject) {
        Path pathToObject = Paths.get(path.toAbsolutePath() + "\\" + nameOfObject);
        if(Files.isDirectory(pathToObject)) {
            return "isDirectory";
        } else {
            return "isFile";
        }
    }

}

