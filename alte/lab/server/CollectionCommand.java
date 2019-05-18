package alte.lab.server;

import alte.lab.Human;

import java.sql.Connection;
import java.sql.SQLException;

public interface CollectionCommand {
    public String getName();
    public Object doCommand(Connection conn, Human arg) throws SQLException;
}