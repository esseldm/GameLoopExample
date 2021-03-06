package com.example.gameloopexample;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by richardselep on 4/1/15.
 */

public class Ball {

    public boolean bounce;
    public int radius = 25;
    public Paint paint;
    private int positionX;
    private int positionY;
    private double xVel;
    private double yVel;
    private int special_ability;

    public Ball(int positionX, int positionY, double xVel, double yVel) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xVel = xVel;
        this.yVel = yVel;
        bounce = false;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        special_ability = 0;
    }

    public Ball(int positionX, int positionY, double xVel, double yVel, int radius, int color, int special_ability) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xVel = xVel;
        this.yVel = yVel;

        this.radius = radius;
        this.paint = new Paint();
        paint.setColor(color);
        this.special_ability = special_ability;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public double getxVel() {
        return xVel;
    }

    public void setxVel(double xVel) {
        this.xVel = xVel;
    }

    public double getyVel() {
        return yVel;
    }

    public void setyVel(double yVel) {
        this.yVel = yVel;
    }

    public int getSpecial_ability() {
        return special_ability;
    }
}
