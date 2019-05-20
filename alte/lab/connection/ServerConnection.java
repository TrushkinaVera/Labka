package alte.lab.connection;

import java.net.ServerSocket;

abstract class ServerConnection extends Thread {
    public ServerConnection() {

    }
}


/*



    new ServerConnection((packet) -> {

        System.out.println(packet.getStringResponse());

    }).start();

    return: Pair<Header, Object>[]
    param: Packet



------------------------------------------------------------------------



    IN
    OUT
    readBytes

    OUT.write( FUNCTION(packet.fromBytes(bytes)) )


 */