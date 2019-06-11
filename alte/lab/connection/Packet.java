package alte.lab.connection;

import alte.lab.Command;
import alte.lab.Pair;
import alte.lab.SerAble;
import alte.lab.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class
Packet implements SerAble {
    /**
     * Информация от сервера
     */
    private Map<Header, Object> data;

    public Packet() {
        data = new HashMap<>();
    }

    /**
     * Получить сырые данные
     * @param header заголовок
     */
    public Object getData(Header header) {
        return data.get(header);
    }
    /**
     * Получить объект кода-ответа [ТОЛЬКО ОТ СЕРВЕРА]
     */
    public ResponseCode getReponseCode() { return (ResponseCode) getData(Header.CODE); }

    /**
     * Получить команду [ТОЛЬКО ОТ КЛИЕТА]
     */
    public Command getCommand() { return (Command) getData(Header.COMMAND); }
    /**
     * Получить пользовательскую информацию [ТОЛЬКО ОТ КЛИЕНТА]
     */
    public User getUser() { return (User) getData(Header.USER); }

    /**
     * Получить ответ сервера
     */
    public Object getRsponse() { return getData(Header.DATA); }
    public String getStringResponse() { return (String)getRsponse(); }
    public Integer getIntResponse() { return (Integer)getRsponse(); }

    /**
     * Положить информацию в ответ
     * @param header заголовок
     * @param data данные
     */
    public void putData(Header header, Object data) {
        this.data.put(header, data);
    }

    /* ------------------------ */

    /**
     * Создать пакет из списка значений
     * @param data значения в пакете
     */
    public static Packet formPacket(Pair<Header, Object>... data) {
        Packet response = new Packet();
        for(Pair<Header, Object> d : data)
            response.putData(d.getKey(), d.getValue());
        return response;
    }

    /**
     * Распаковать пакет из байтов в объект
     * @param request байтовый поток
     */
    static Packet fromBytes(byte[] request) {
        ObjectInputStream ois;
        Packet req = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(request));
            req = (Packet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return req;
    }
}
