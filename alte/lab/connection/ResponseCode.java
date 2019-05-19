package alte.lab.connection;

import alte.lab.localization.Localization;
import java.io.Serializable;

/**
 * Коды ответов сервера. Все подробности брал на httpstatuses.com
 */
enum ResponseCode implements Serializable {
    OK("ok"),
    UNATHORIZED("unathorized"),
    BAD_REQUEST( "bad_request");

    private String message_key;
    ResponseCode(String message_key) {
        this.message_key = message_key;
    }

    /**
     * Получить локализованное сообщение
     * @param localization объект с инициализированной локализацией
     */
    public String getMessage(Localization localization) {
        return localization.getString(message_key);
    }
}
