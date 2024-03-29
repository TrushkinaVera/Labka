package mrak.top.server;

import mrak.top.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface CollectionCommand {
    String getName();

    Object doCommand(Connection conn, Object arg, User usr) throws SQLException;
}