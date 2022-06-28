package netty.database;

public interface DB {
    void insertUser(String login, String password, String firstName, String lastName, String email);
    boolean isExists(String login, String password);
    String getId(String login);
}
