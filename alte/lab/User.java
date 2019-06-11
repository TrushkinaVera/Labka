package alte.lab;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Serializable {
    private String login;
    private String password;

    public User(String login) {
        this.login = login;
        if (login == null) throw new NullPointerException();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    static public String encrypt(String source) {
        String encoded = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            encoded = new String(md.digest(source.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public void hashAndSetPassword(String password) {
        //TODO:hasher
        this.password = encrypt(password);
    }

    public JSONObject toJSON() {
        JSONObject oneUser = new JSONObject();
        oneUser.put("Login", login);
        oneUser.put("Password", password);
        return oneUser;
    }
}
