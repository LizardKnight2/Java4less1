package netty.database;

import java.sql.*;

public class DBImplements implements DB{
    private Connection connection;
    public Statement statement;

    public DBImplements(){
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc.sqlite.identifier.sqlite");
            statement = connection.createStatement();
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insertUser(String login, String password, String firstName, String lastName, String email) {
        try {
            statement.executeUpdate(String.format("INSERT INTO users (login, password, String first_name, String last_name, String email)" + "VALUES('%s', '%s', '%s','%s')",
                    login, password, firstName, lastName, email));

            }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isExists(String login, String password) {
        try {
        ResultSet resultSet = statement.executeQuery(String.format("SELECT login, password FROM users WHERE login = '%s' and password = '%s'", login, password));
        if (resultSet.getString("login") != null && resultSet.getString("password") != null)
            return true;
    } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getId(String login) {
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT id FROM users WHERE login = '%s'", login));
            return resultSet.getString("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Zug Zug");
    }
}
