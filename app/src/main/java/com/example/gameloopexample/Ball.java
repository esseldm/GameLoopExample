package com.example.gameloopexample;

/**
 * Created by richardselep on 4/1/15.
 */

public class Ball {

    private int positionX;
    private int positionY;
    private double xVel;
    private double yVel;


    public Ball(int positionX, int positionY, double xVel, double yVel) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.xVel = xVel;
        this.yVel = yVel;
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
