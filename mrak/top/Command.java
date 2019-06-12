package mrak.top;

import java.io.Serializable;

/**
 * Объекты данного класса представляют собой команды, которые передат пользователь на сервер
 */
public class Command implements Serializable {
    private String text = "";
    private Object argument;

    public Command(String text, Object argument) {
        this.text = text;
        this.argument = argument;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getArgument() {
        return argument;
    }

    public void setArgument(Object argument) {
        this.argument = argument;

    }

}
