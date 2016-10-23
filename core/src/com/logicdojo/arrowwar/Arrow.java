package com.logicdojo.arrowwar;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

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
    private long timeAlive;
    private int powerScore;
    private BonusImage bi;

    public Arrow(int width, int height, boolean leftDir, int bowColor) {
        this.width = width;
        this.height = height;
        this.leftDir = leftDir;
        this.powerScore = bowColor;
        this.bonus1Hit = false;

        Random rand = new Random();
        int randomNum = rand.nextInt((10 - 1) + 1) + 1;
        if (randomNum >= 8) {
            this.bonus1Hit = true;
        }

        timeAlive = TimeUtils.millis();

        myShape = new Rectangle(240, 0, 10, 30);
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
    public boolean getBonus1Hit() {
        return this.bonus1Hit;
    }
    public void setBonus1Hit(boolean b) { this.bonus1Hit = b; }
    public long getTimeAlive() {
        return this.timeAlive;
    }
    public void setBonusImage(BonusImage bonus) {
        this.bi = bonus;
    }
    public int getPowerScore() {
        return this.powerScore;
    }


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
