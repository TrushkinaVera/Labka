package mrak.top;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class Human implements Serializable, Comparable<Human> {
    private String name;
    private int age;
    private int xPos;
    private int yPos;
    private Integer id;
    private OffsetDateTime tTime;

    //TODO: ADD DATE
    public Human(String name, int age) {
        this(name, age, 0, 0);
    }

    public Human(String name, int age, int xPos, int yPos) {
        this.name = name;
        this.age = age;
        this.xPos = xPos;
        this.yPos = yPos;
        this.tTime = OffsetDateTime.now();
        this.id = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void move(double x, double y) {
        xPos += x;
        yPos += y;
    }

    public Integer getId() {
        return id;
    }

    public OffsetDateTime gettTime() {
        return tTime;
    }

    public void settTime(OffsetDateTime tTime) {
        this.tTime = tTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPosX() {
        return xPos;
    }

    public void setPosX(int xPos) {
        this.xPos = xPos;
    }

    public int getPosY() {
        return yPos;
    }

    public void setPosY(int yPos) {
        this.yPos = yPos;
    }

    @Override
    public int compareTo(Human o) {
        return age - o.getAge();
    }

    public JSONObject toJSON() {
        JSONObject oneMan = new JSONObject();
        oneMan.put("Name", name);
        oneMan.put("Age", age);
        oneMan.put("PosX", xPos);
        oneMan.put("PosY", yPos);
        //TODO: нужен ли айдишник в JSON
        return oneMan;
    }

    public String getDate() {
        return tTime.toString();
    }

    @Override
    public String toString() {
        return "Human: " + name + ", " + age + " years old, Position:[" + xPos + ";" + yPos + "]";
        //return super.toString();
    }
}