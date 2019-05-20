package alte.lab.server;

import alte.lab.Human;
import alte.lab.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface CollectionCommand {
    public String getName();
    public Object doCommand(Connection conn, Object arg, User usr) throws SQLException;
}