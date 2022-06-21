package netty;

import cloud.CloudMessage;
import cloud.FileMessage;
import cloud.FileRequest;
import cloud.ListFiles;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;

public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private Path currentDir;
    private Path rootDir;

    public CloudFileHandler() {
        currentDir = Path.of("server_files");
        rootDir = currentDir;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListFiles(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));

        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));
            ListFiles ltFs = new ListFiles(currentDir);
            if(!currentDir.equals(rootDir)) ltFs.getFiles().add(0,"..");
            ctx.writeAndFlush(ltFs);
        } else if(cloudMessage instanceof PathInRequest rqt){
            String filename = rqt.getName();
            currentDir = currentDir.resolve(Path.of(filename));
            ListFiles listFiles = new ListFiles(currentDir);
            listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);

        } else if(cloudMessage instanceof PathUpRequest ){
            currentDir = currentDir.getParent();
            ListFiles listFiles = new ListFiles(currentDir);
            if(currentDir.equals(rootDir)){
                ctx.writeAndFlush(listFiles);
                return;
            }
            listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);

        } else if(cloudMessage instanceof FileDeleteRequest request){
            String fileName = request.getName();
            Path file = currentDir.resolve(Path.of(fileName));
            try {
                Files.delete(file);
            }catch (Exception e){
                e.printStackTrace();
            }
            ListFiles listFiles = new ListFiles(currentDir);
            if(!currentDir.equals(rootDir)) listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);
        }
    }
}
