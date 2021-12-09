package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    Path path;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        //   System.out.println(msg);
         //  System.out.println(msg.length());
        log.debug(msg);
        log.debug(String.valueOf(msg.length()));
        if(msg.equals("1")) {
            path = Paths.get("server_files");
            String message =  "enter\n";
            log.debug(message);
            channelHandlerContext.writeAndFlush(message);
            int quantityOfFiles = Files.list(path).collect(Collectors.toList()).size();
            log.debug(String.valueOf(quantityOfFiles));
            channelHandlerContext.writeAndFlush(quantityOfFiles + "\n");
            if(quantityOfFiles > 0) {
                Files.list(path).forEach(file -> {
                    channelHandlerContext.writeAndFlush(file.getFileName() + "\n");
                    log.debug(String.valueOf(file.getFileName()));
                });
            }
            }
    }


    private void sendFileList(Path path) {

    }


}
