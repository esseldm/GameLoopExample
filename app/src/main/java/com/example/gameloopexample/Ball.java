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

    public Ball(int positionX, int positionY, double xVel, double yVel) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xVel = xVel;
        this.yVel = yVel;
        bounce = false;
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public Ball(int positionX, int positionY, double xVel, double yVel, int radius, int color) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xVel = xVel;
        this.yVel = yVel;

        this.radius = radius;
        this.paint = new Paint();
        paint.setColor(color);
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

    class BallThread extends Thread {

        //Lots of fun stuff will go here eventually
    }
}
