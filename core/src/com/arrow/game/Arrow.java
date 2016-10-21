package com.arrow.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/**
 * Created by matt on 2016-10-18.
 */
public class Arrow {
    private int upV;
    private int sideV;
    private boolean leftDir;
    public Rectangle myShape;
    public Vector2 myVector;
    private float x;
    private float y;
    private int width;
    private int height;
    private boolean bonus1Hit;

    public Arrow(int width, int height, boolean leftDir) {
        this.width = width;
        this.height = height;
        this.leftDir = leftDir;
        this.bonus1Hit = false;

        Random rand = new Random();
        int randomNum = rand.nextInt((10 - 1) + 1) + 1;
        if (randomNum >= 8) {
            this.bonus1Hit = true;
        }

        myShape = new Rectangle(240, 0, 10, 30);

        System.out.println("HEIGHT -- " + height);
        System.out.println("WIDTH -- " + width);
    }

    public int getUpV() {
        return this.upV;
    }
    public int getSideV() {
        return this.sideV;
    }
    public boolean getLeftDir() {
        return this.leftDir;
    }
    public Rectangle getRectangle() {
        return this.myShape;
    }
    public void setRectangle(Rectangle r) {
        myShape = r;
    }
    public void setVector(Vector2 v2) {  }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setX(float newX) {
        this.x = newX;
    }
    public void setY(float newY) {
        this.y = newY;
    }
    public int getWidth() {
        return this.width;
    }
    public int Height() {
        return this.height;
    }
    public boolean getBonus1Hit() {
        return this.bonus1Hit;
    }
    public void setBonus1Hit(boolean b) { this.bonus1Hit = b; }

    public void step()
    {
        if (leftDir) {
            this.myShape.x -= this.width / 10;
        }
        else {
            this.myShape.x += this.width / 10;
        }
        this.myShape.y += this.height/10;
    }
}
