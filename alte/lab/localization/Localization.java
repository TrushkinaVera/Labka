package alte.lab.localization;

import java.util.ResourceBundle;

public class Localization {
    private ResourceBundle res;

    public Localization() {
         res = ResourceBundle.getBundle("localization");
    }

    public String getString(String keyName) {
        return res.getString(keyName);
    }
}
