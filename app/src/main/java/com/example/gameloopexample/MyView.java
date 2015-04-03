package com.example.gameloopexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 10;
    private static final int TOP_MARGIN = 50;
    private final int PADDLE_THICKNESS=20;
    private final int PADDLE_LENGTH=150;
    private final int BALL_SIZE=25;
    private ArrayList<Ball> balls;
    private int xPaddle,yPaddle;
    private Rect rect;
    private Paint bgPaint,ballPaint;
    private MyThread thread;
    private ArrayList<Point> blocks;
    private int blockWidth;
    private int blockHeight;
    private MediaPlayer blockPlayer;
    private MediaPlayer paddlePlayer;

    public MyView(Context context) {
        super(context);
        init(null, 0);
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
        balls = new ArrayList<Ball>();
        Ball ball1 = new Ball(50, 600, 10.0, 25.0);
        //TEST
        Ball ball2 = new Ball(300, 200, 10.0, 25.0);
        balls.add(ball1);
        //TEST
        balls.add(ball2);
        xPaddle=50;
        yPaddle=1000;
        bgPaint=new Paint();
        bgPaint.setColor(Color.BLACK);
        ballPaint=new Paint();
        ballPaint.setColor(Color.RED);
        thread=new MyThread();
        getHolder().addCallback(this);
        setFocusable(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(),getHeight(),bgPaint);
        for(Ball p: balls) {
            canvas.drawCircle(p.getPositionX(), p.getPositionY(), BALL_SIZE, ballPaint);
        }
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
        //CHange this
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

            return -1;
        }

        @Override
        public void run() {
            while(running) {
                for (Ball p : balls) {
                    //up
                    p.setPositionX((int) ((double) p.getPositionX() + p.getxVel()));
                    p.setPositionY((int) ((double) p.getPositionY() + p.getyVel()));
                    if (p.getPositionX() < 0 || p.getPositionX() > getWidth()) {
                        p.setxVel(-p.getxVel());
                    }
                    if (p.getPositionY() < 0 || p.getPositionY() > getHeight()) {
                        p.setyVel(-p.getyVel());
                    }
                    if (p.getPositionY() + BALL_SIZE > rect.bottom) {
                        Log.d("BALLS", "x:" + p.getPositionX() + "y:" + p.getPositionY());
                        Log.d("IN the paddle", "x:" + rect.centerX() + "y:" + rect.centerY());
                    }
                    //Balls
                    //see if ball has hit paddle (or gone below it)
                    if ((rect.contains(p.getPositionX(), p.getPositionY() + BALL_SIZE) || rect.contains(p.getPositionX() - BALL_SIZE, p.getPositionY()) ||
                            rect.contains(p.getPositionX() + BALL_SIZE, p.getPositionY()) || rect.contains(p.getPositionX(), p.getPositionY() - BALL_SIZE)) && !p.bounce) {
                        if (paddlePlayer.isPlaying()) {
                            paddlePlayer.seekTo(0);
                        } else {
                            paddlePlayer.start();
                        }
                        p.setyVel(-p.getyVel());
                        p.setyVel(p.getyVel() + (rand.nextInt(10) - 5));
                        p.setxVel(p.getxVel() + (rand.nextInt(10) - 5));
                        p.bounce = true;
                    } else {
                        p.bounce = false;
                    }

                    //see if ball has hit a block
                    for (int i = 0; i < blocks.size(); i++) {
                        Point b = blocks.get(i);
                        Log.d("Points", b.toString());
                        int edge = hit(b, p.getPositionX(), p.getPositionY());
                        if (edge >= 0) {
                            if (blockPlayer.isPlaying()) {
                                blockPlayer.seekTo(0);
                            } else {
                                blockPlayer.start();
                            }
                            blocks.remove(i);
                            Log.d("Edge", edge + "");
                            switch (edge) {
                                case 0: //bounce x
                                    p.setxVel(-p.getxVel());
                                    break;
                                case 1: //bounce y
                                    p.setyVel(-p.getyVel());
                                    break;
                                case 2: //bounce both
                                    p.setxVel(-p.getxVel());
                                    p.setyVel(-p.getyVel());
                                    break;
                            }
                            break;
                        }
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