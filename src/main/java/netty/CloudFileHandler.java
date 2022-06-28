package netty;

import cloud.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.ServerUser.UserServer;
import netty.database.User;

import java.nio.file.Files;
import java.nio.file.Path;

public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private UserServer userServer;
    private Path currentDir;
    private Path rootDir;

    public CloudFileHandler() {
        /*currentDir = Path.of("server_files");
        rootDir = currentDir;*/
        userServer = new UserServer();
    }

    /*@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListFiles(currentDir));
    }*/

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(UserServer.getLogDir().get(fileRequest.getLogin()).resolve(fileRequest.getName()),fileRequest.getLogin())/*currentDir.resolve(fileRequest.getName()),fileRequest.getLogin*/);

        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(/*currentDir*/UserServer.getLogDir().get(fileMessage.getLogin()).resolve(fileMessage.getName()), fileMessage.getData());
            /*ctx.writeAndFlush(new ListFiles(currentDir));*/
            ListFiles ltFs = new ListFiles(/*currentDir*/UserServer.getLogDir().get(fileMessage.getLogin()));
            if(!/*currentDir*/UserServer.getLogDir().get(fileMessage.getLogin()).equals(/*rootDir*/UserServer.getLoginRootDir().get(fileMessage.getLogin())))
                ltFs.getFiles().add(0,"..");
            ctx.writeAndFlush(ltFs);
        } else if(cloudMessage instanceof PathInRequest rqt){
            String filename = rqt.getName();
            /*currentDir = currentDir.resolve(Path.of(filename));*/
            Path currentDir = UserServer.getLogDir().get(rqt.getLogin()).resolve(Path.of(filename));
            UserServer.getLogDir().put(rqt.getLogin(),currentDir);
            ListFiles listFiles = new ListFiles(currentDir);
            listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);

        } else if(cloudMessage instanceof PathUpRequest rqt ){
            /*currentDir = currentDir.getParent();*/
            Path currentDir = UserServer.getLogDir().get(rqt.getLogin()).getParent();
            UserServer.getLogDir().remove(rqt.getLogin());
            UserServer.getLogDir().put(rqt.getLogin(),currentDir);
            ListFiles listFiles = new ListFiles(currentDir);
            if(currentDir.equals(/*rootDir*/UserServer.getLoginRootDir().get(rqt.getLogin()))){
                ctx.writeAndFlush(listFiles);
                return;
            }
            listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);

        } else if(cloudMessage instanceof FileDeleteRequest request){
            String fileName = request.getName();
            Path file = /*currentDir*/UserServer.getLogDir().get(request.getLogin()).resolve(Path.of(fileName));
            try {
                Files.delete(file);
            }catch (Exception e){
                e.printStackTrace();
            }
            ListFiles listFiles = new ListFiles(/*currentDir*/UserServer.getLogDir().get(request.getLogin()));
            if(!/*currentDir*/UserServer.getLogDir().get(request.getLogin()).equals(/*rootDir*/UserServer.getLoginRootDir().get(request.getLogin())))
                listFiles.getFiles().add(0,"..");
            ctx.writeAndFlush(listFiles);
        } else if(cloudMessage instanceof AuthorizeRequest request){
            User user = new User(request.getLogin(),request.getPassword(),request.getFirstName(),request.getLastName(),request.getEmail());
            userServer.authorize(user);
            if(userServer.isAuth(user.getLogin(),user.getPassword()))
                ctx.writeAndFlush(new AuthResponse(true,user.getLogin()));
            else
                ctx.writeAndFlush(new AuthResponse(false,user.getLogin()));
        } else if(cloudMessage instanceof AuthRequest request){
            if(userServer.isAuth(request.getLogin(),request.getPassword())){
                ctx.writeAndFlush(new AuthResponse(true,request.getLogin()));
            }
            else
                ctx.writeAndFlush(new AuthResponse(false,request.getLogin()));
        }else if(cloudMessage instanceof ListFilesRequest request){
            ListFiles list = new ListFiles(UserServer.getLogDir().get(request.getLogin()));
            ctx.writeAndFlush(list);
        }else if(cloudMessage instanceof RequestPath path){
            ctx.writeAndFlush(new ResponsePath(UserServer.getLogDir().get(path.getLogin())));
        }else if(cloudMessage instanceof LogResponse response){
            ListFiles list = new ListFiles(UserServer.getLogDir().get(response.getLogin()));
            ctx.writeAndFlush(list);
        }
    }
}
