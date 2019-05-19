package alte.lab;

import java.io.*;

public interface SerAble extends Serializable {

    default byte[] getBytes() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
