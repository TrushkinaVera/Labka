package alte.lab;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class Human implements Serializable, Comparable<Human> {
    private String name;
    private int age;
    private int xPos;
    private int yPos;
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
    public void move(double x, double y){
        xPos+=x;
        yPos+=y;
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
        return age-o.getAge();
    }

    public JSONObject toJSON() {
        JSONObject oneMan = new JSONObject();
        oneMan.put("Name",name);
        oneMan.put("Age", age);
        oneMan.put("PosX", xPos);
        oneMan.put("PosY", yPos);
        return oneMan;
    }

    @Override
    public String toString() {
        return "alte.lab.Human: "+name +", "+ Integer.toString(age) +" years old, Position:[" + Double.toString(xPos)+";"+Double.toString(yPos)+"]";
        //return super.toString();
    }
}