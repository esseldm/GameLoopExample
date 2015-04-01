package com.example.gameloopexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 10;
    private static final int TOP_MARGIN = 50;
    private final int PADDLE_THICKNESS=20;
    private final int PADDLE_LENGTH=150;
    private final int BALL_SIZE=25;
    private int xCenter,yCenter;
    private int xPaddle,yPaddle;
    private Rect rect;
    private double xVel,yVel;
    private Paint bgPaint,ballPaint;
    private boolean bounce;
    private MyThread thread;
    private ArrayList<Point> blocks;
    private int blockWidth;
    private int blockHeight;
    private MediaPlayer blockPlayer;
    private MediaPlayer paddlePlayer;

    public MyView(Context context) {
        super(context);
        init(null, 0);

        //I caught ya a dollar  Just kidding.  Why is this so confusing Danny?
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        blockPlayer = MediaPlayer.create(getContext(), R.raw.bounce);
        paddlePlayer = MediaPlayer.create(getContext(), R.raw.bounce2);
        blocks=new ArrayList<Point>();
        xCenter=50;
        yCenter=50;
        xPaddle=50;
        yPaddle=1000;
        xVel=10.0;
        yVel=25.0;
        bgPaint=new Paint();
        bgPaint.setColor(Color.BLACK);
        ballPaint=new Paint();
        ballPaint.setColor(Color.RED);
        thread=new MyThread();
        getHolder().addCallback(this);
        setFocusable(true);
        bounce=false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(),getHeight(),bgPaint);
        canvas.drawCircle(xCenter,yCenter,BALL_SIZE,ballPaint);
        canvas.drawRect(xPaddle,yPaddle,xPaddle+PADDLE_LENGTH,yPaddle+PADDLE_THICKNESS,ballPaint);
        for(Point p:blocks) {
            canvas.drawRect(p.x,p.y,p.x+blockWidth-2,p.y+blockHeight-2,ballPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPaddle=(int)event.getX()-PADDLE_LENGTH/2;
        rect.left=xPaddle;
        rect.right=xPaddle+PADDLE_LENGTH;
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        blockWidth=getWidth()/NUM_COLS;
        blockHeight=getHeight()/50;
        for(int i=0;i<NUM_ROWS;i++) {
            for(int j=0;j<NUM_COLS;j++) {
                blocks.add(new Point(j*blockWidth,i*blockHeight+TOP_MARGIN));
            }
        }
        yPaddle=getHeight()-PADDLE_THICKNESS*6;
        xPaddle=getWidth()/2-PADDLE_LENGTH/2;
        rect=new Rect(xPaddle, yPaddle, xPaddle+PADDLE_LENGTH, yPaddle+PADDLE_THICKNESS);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry=true;
        while(retry) {
            try {
                thread.join();
                retry=false;
            } catch (InterruptedException e) {
                //just do nothing so go to the top of while loop
            }
        }
    }

    class MyThread extends Thread {

        private boolean running;
        private Random rand;

        public MyThread() {
            rand=new Random();
        }

        public void setRunning(boolean b) {
            running=b;
        }

        private int hit(Point p, int x, int y) {
            Rect blockRect = new Rect(p.x, p.y, p.x+blockWidth, p.y+blockHeight);
            if(blockRect.contains(x-BALL_SIZE, y) || blockRect.contains(x+BALL_SIZE, y)) {
                return 0;
            } else if(blockRect.contains(x, y-BALL_SIZE) || blockRect.contains(x, y+BALL_SIZE)) {
                return 1;
            }
            //if(blockRect.contains(x, y+BALL_SIZE) || blockRect.contains(x-BALL_SIZE, y) ||
                    //blockRect.contains(x+BALL_SIZE, y) || blockRect.contains(x, y-BALL_SIZE)) {
                    //return 1;
            //}
            return -1;
        }

        @Override
        public void run() {
            while(running) {
                //update state
                xCenter+=xVel;
                yCenter+=yVel;
                if (xCenter<0 || xCenter>getWidth()) {
                    xVel=-xVel;
                }
                if (yCenter<0 || yCenter>getHeight()) {
                    yVel=-yVel;
                }
                //see if ball has hit paddle (or gone below it)
                if((rect.contains(xCenter, yCenter+BALL_SIZE) || rect.contains(xCenter-BALL_SIZE, yCenter) ||
                        rect.contains(xCenter+BALL_SIZE, yCenter) || rect.contains(xCenter, yCenter-BALL_SIZE)) && !bounce) {
                    if(paddlePlayer.isPlaying()) {
                        paddlePlayer.seekTo(0);
                    } else {
                        paddlePlayer.start();
                    }
                    yVel=-yVel;
                    yVel+=(rand.nextInt(10)-5);
                    xVel+=(rand.nextInt(10)-5);
                    bounce=true;
                } else {
                    bounce=false;
                }

                //see if ball has hit a block
                for(int i=0;i<blocks.size();i++) {
                    Point p=blocks.get(i);
                    int edge=hit(p,xCenter,yCenter);
                    if (edge>=0) {
                        if(blockPlayer.isPlaying()) {
                            blockPlayer.seekTo(0);
                        } else {
                            blockPlayer.start();
                        }
                        blocks.remove(i);
                        switch (edge) {
                            case 0: //bounce x
                                xVel=-xVel;
                                break;
                            case 1: //bounce y
                                yVel=-yVel;
                                break;
                            case 2: //bounce both
                                xVel=-xVel;
                                yVel=-yVel;
                                break;
                        }
                        break;
                    }
                }

                //redraw the screen
                Canvas canvas=getHolder().lockCanvas();
                if (canvas!=null) {
                    synchronized (canvas) {
                        onDraw(canvas);
                    }
                    getHolder().unlockCanvasAndPost(canvas);
                }

                //wait for a short amount of time
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}