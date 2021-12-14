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
                if(sendFileMessage.getPartNumber() == 1) {
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
            Path pathToFile = Paths.get(path.toAbsolutePath() + "\\" + downloadRequestMessage.getFileName());
            String destinationPath = downloadRequestMessage.getDestinationPath();
            sendFile(pathToFile, destinationPath, channelHandlerContext);
        }

        if(msg instanceof ChangeDirectoryMessage) {
            ChangeDirectoryMessage changeDirectoryMessage = (ChangeDirectoryMessage) msg;
            changeDirectory(channelHandlerContext, changeDirectoryMessage);
        }

        if(msg instanceof RenameFileMessage) {
            RenameFileMessage renameFileMessage = (RenameFileMessage) msg;
            String oldName = renameFileMessage.getOldName();
            String newName = renameFileMessage.getNewName();
            renameFile(channelHandlerContext, oldName, newName);
        }

        if(msg instanceof CreateDirectoryMessage) {
            CreateDirectoryMessage createDirectoryMessage = (CreateDirectoryMessage) msg;
            String directoryName = createDirectoryMessage.getDirectoryName();
            createDirectory(channelHandlerContext, directoryName);
        }

        if(msg instanceof DeleteFileMessage) {
            DeleteFileMessage deleteFileMessage = (DeleteFileMessage) msg;
            String fileName = deleteFileMessage.getFileName();
            deleteFile(channelHandlerContext, fileName);
        }

    }

    private FileListMessage getFileListMessage(Path path) {
        try {
            int quantityOfFiles = Files.list(path).collect(Collectors.toList()).size();
            if (quantityOfFiles > 0) {
                ArrayList<String> fileList = (ArrayList<String>) Files.list(path).map(file -> file.getFileName().toString()).collect(Collectors.toList());
                log.debug(path.toString());
                return new FileListMessage(path.toString(), fileList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileListMessage(path.toString(), new ArrayList<>());
    }

    public void saveFile(SendFileMessage sendFileMessage) {
        String fileName = sendFileMessage.getName();
        log.debug(fileName);
        String destinationPath = sendFileMessage.getDestinationPath();
        Path newFile = Paths.get(destinationPath + "\\" + fileName);
        log.debug(newFile.getFileName().toString());
        if(Files.exists(newFile)) {
            try {
                long fileSize = Files.size(newFile);
                if(fileSize >= sendFileMessage.getLength()) {
                    newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
                    if(Files.exists(newFile) && Files.size(newFile) >= sendFileMessage.getLength()) {
                        while (true) {
                            newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
                            if (!Files.exists(newFile)) {
                                newFile = Paths.get(destinationPath + "\\" + newFile.getFileName().toString().substring(5));
                                log.debug(newFile.getFileName().toString());
                                break;
                            }
                        }
                        long lastCopySize = Files.size(newFile);
                        if (lastCopySize >= sendFileMessage.getLength()) {
                            newFile = Paths.get(destinationPath + "\\copy " + newFile.getFileName());
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


    public void sendFile(Path path, String destinationPath, ChannelHandlerContext channelHandlerContext) {
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
                    channelHandlerContext.writeAndFlush(new SendFileMessage(fileName, destinationPath, fileSize, buffer, partNumber));
                } else {
                    channelHandlerContext.writeAndFlush(new SendFileMessage(fileName, destinationPath, fileSize, buffer, partNumber));
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

    private void changeDirectory(ChannelHandlerContext channelHandlerContext, ChangeDirectoryMessage changeDirectoryMessage) {
        String command = changeDirectoryMessage.getCommand();
        if(command.equals("up") && !path.toString().equals("server_files")) {
            path = path.getParent();
            channelHandlerContext.writeAndFlush(getFileListMessage(path));
        }
        if(command.equals("down")) {
            path = Paths.get(path + "\\" + changeDirectoryMessage.getNewFolderName());
            channelHandlerContext.writeAndFlush(getFileListMessage(path));
        }
    }


    private void renameFile(ChannelHandlerContext channelHandlerContext, String oldName, String newName) {
        Path newNamePath;
        Path oldNamePath = Paths.get(path + "\\" + oldName);
        if(Files.isDirectory(oldNamePath)) {
            newNamePath = Paths.get(path + "\\" + newName);
        } else {
            newNamePath = Paths.get(path + "\\" + newName + "." + getFileExtension(oldName));
        }
        if(Files.exists(newNamePath)) {
            channelHandlerContext.writeAndFlush(new OperationConfirmMessage(false));
        } else {
            try {
                if(Files.isDirectory(oldNamePath)) {
                    Files.move(oldNamePath, oldNamePath.resolveSibling(newName));
                } else {
                    Files.move(oldNamePath, oldNamePath.resolveSibling(newName + "." + getFileExtension(oldName)));
                }
                channelHandlerContext.writeAndFlush(getFileListMessage(path));
                channelHandlerContext.writeAndFlush(new OperationConfirmMessage(true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private void createDirectory(ChannelHandlerContext channelHandlerContext, String directoryName) {
        Path newDirPath = Paths.get(path + "\\" + directoryName);
        if(Files.exists(newDirPath)) {
            channelHandlerContext.writeAndFlush(new OperationConfirmMessage(false));
        } else {
            try {
                Files.createDirectory(newDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            channelHandlerContext.writeAndFlush(getFileListMessage(path));
            channelHandlerContext.writeAndFlush(new OperationConfirmMessage(true));
        }
    }

    private void deleteFile(ChannelHandlerContext channelHandlerContext, String fileName) {
        Path pathToFile = Paths.get(path + "\\" + fileName);
        try {
            Files.delete(pathToFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!Files.exists(pathToFile)) {
            channelHandlerContext.writeAndFlush(getFileListMessage(path));
            channelHandlerContext.writeAndFlush(new OperationConfirmMessage(true));
        } else {
            channelHandlerContext.writeAndFlush(new OperationConfirmMessage(false));
        }
    }

}


