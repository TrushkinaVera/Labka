package alte.lab.connection;

import alte.lab.Command;

interface Client {
    boolean connect(String host);

    Packet last_response();

    Packet send(Command command);
}
