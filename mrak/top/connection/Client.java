package mrak.top.connection;

import mrak.top.Command;

interface Client {
    boolean connect(String host);

    Packet last_response();

    Packet send(Command command);
}
