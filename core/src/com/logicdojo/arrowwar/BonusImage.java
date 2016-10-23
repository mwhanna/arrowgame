package com.logicdojo.arrowwar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by matt on 2016-10-22.
 */
public class BonusImage {
    private Texture myImage;
    private long myTime;
    private Rectangle myRect;
    private int myType;

    public BonusImage(Texture t, Rectangle r, int type) {
        myImage = t;
        myRect = r;
        myTime = TimeUtils.millis();
        myType = type;
        if (myType == 2) {
            Rectangle temp = new Rectangle();
            temp.x = r.x - 128;
            temp.y = r.y - 128;
            temp.setHeight(256);
            temp.setWidth(256);
            myRect = temp;
        }
    }

    public Texture getImage() {
        return myImage;
    }

    public long getTime() {
        return myTime;
    }

    public int getType() {
        return myType;
    }

    public Rectangle getRect() {
        return myRect;
    }
}
