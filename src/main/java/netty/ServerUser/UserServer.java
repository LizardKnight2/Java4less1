package netty.ServerUser;

import lombok.Getter;
import netty.database.DBImplements;
import netty.database.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class UserServer {
    private DBImplements db;
    @Getter
    private static ConcurrentHashMap<String, Path> logDir;
    @Getter
    private static ConcurrentHashMap<String, Path> loginRootDir;

    public UserServer(){
        this.db = new DBImplements();
        logDir = new ConcurrentHashMap<>();
        loginRootDir = new ConcurrentHashMap<>();
    }
    public boolean isAuth(String login, String password){
        if(isAuthorize(login,password)){
            String id = db.getId(login);
            userIfNotExists(id);
            loginRootDir.put(login, Path.of(id));
            logDir.put(login, Path.of(id));
            return true;
        }
        return false;
    }
    public void authorize (User user){
        db.insertUser(user.getLogin(),user.getPassword(),user.getFirstName(),user.getLastName(), user.getEmail());
    }
    private void userIfNotExists(String id){
        try {
            if(!new File(id).exists())
                Files.createDirectory(Path.of(id));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private boolean isAuthorize(String login, String password){
        return db.isExists(login,password);
    }
}
