package mrak.top.connection;

import mrak.top.Command;

public class TCP_Client implements Client {
    @Override
    public boolean connect(String host) {
        return false;
    }

    @Override
    public Packet last_response() {
        return null;
    }

    @Override
    public Packet send(Command command) {
        return null;
    }
}
