package com.example.gameloopexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int NUM_COLS = 10;
    private static final int TOP_MARGIN = 50;
    private final int BALL_SIZE=25;
    private final int SPECIAL_ABILITY_BALL_SIZE = 10;
    private final int MULTIPLE_BALL_COLOR = Color.RED;
    private final int PADDLE_INCREASE_COLOR = Color.GREEN;
    private final int SPECIAL_ABILITY_FALL_SPEED = 15;
    private final int PADDLE_LENGTH_INCREASE = 50;
    private final int PADDLE_THICKNESS_INCREASE = 0;
    private final int PADDLE_INCREASE_TIME = 15;
    private final int MULTIPLE_BALLS = 1;
    private final int PADDLE_INCREASE = 2;
    public int paddle_thickness = 20;
    public int paddle_length = 150;
    private Handler handler;
    private Runnable paddleRunnable;
    private ArrayList<Ball> balls;
    private int xPaddle,yPaddle;
    private Rect rect;
    private Paint bgPaint, blockPaint;
    private MyThread thread;
    private ArrayList<Block> blocks;
    private int blockWidth;
    private int blockHeight;
    private MediaPlayer blockPlayer;
    private MediaPlayer paddlePlayer;

    private int[][] gameBoard;

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
        blocks=new ArrayList<Block>();
        balls = new ArrayList<Ball>();
        Ball ball1 = new Ball(400, 400, 10, 25);
        balls.add(ball1);
        xPaddle=50;
        yPaddle=1000;
        bgPaint=new Paint();
        bgPaint.setColor(Color.BLACK);
        thread=new MyThread();
        getHolder().addCallback(this);
        setFocusable(true);
        gameBoard = new int[20][10];
        handler = new Handler();
        blockPaint = new Paint();
        blockPaint.setColor(Color.BLUE);

        paddleRunnable = new Runnable() {
            @Override
            public void run() {
                paddle_length -= PADDLE_LENGTH_INCREASE;
                paddle_thickness -= PADDLE_THICKNESS_INCREASE;
            }
        };


        InputStream s = getResources().openRawResource(R.raw.boardlayout1);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s));
        int row = 0;
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] values = line.split(" ");

                for(int i = 0; i < values.length;i++){
                    gameBoard[row][i]= Integer.parseInt(values[i]);

                }
                line = bufferedReader.readLine();
                row++;
            }
        }catch(Exception ex){

        }finally{

        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(),getHeight(),bgPaint);
        for(Ball p: balls) {
            canvas.drawCircle(p.getPositionX(), p.getPositionY(), p.radius, p.paint);
        }
        canvas.drawRect(xPaddle, yPaddle, xPaddle + paddle_length, yPaddle + paddle_thickness, blockPaint);
        for(Block p:blocks) {
            canvas.drawRect(p.getX, p.getY, p.getX + blockWidth - 2, p.getY + blockHeight - 2, blockPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPaddle = (int) event.getX() - paddle_length / 2;
        rect.left=xPaddle;
        rect.right = xPaddle + paddle_length;
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        blockWidth=getWidth()/NUM_COLS;
        blockHeight=getHeight()/50;

        for(int i=0;i<gameBoard.length;i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == 1) {
                    blocks.add(new Block(j * blockWidth, i * blockHeight + TOP_MARGIN, 0));

                } else if (gameBoard[i][j] == 2) {

                    blocks.add(new Block(j * blockWidth, i * blockHeight + TOP_MARGIN, MULTIPLE_BALLS));

                } else if (gameBoard[i][j] == 3) {

                    blocks.add(new Block(j * blockWidth, i * blockHeight + TOP_MARGIN, PADDLE_INCREASE));
                }

            }
        }
        yPaddle = getHeight() - paddle_thickness * 6;
        xPaddle = getWidth() / 2 - paddle_length / 2;
        rect = new Rect(xPaddle, yPaddle, xPaddle + paddle_length, yPaddle + paddle_thickness);
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

        private int hit(Block p, int x, int y) {
            Rect blockRect = new Rect(p.getX, p.getY, p.getX+blockWidth, p.getY+blockHeight);
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
                for (int count = 0; count < balls.size(); count++) {
                    Ball p = balls.get(count);
                    //up
                    p.setPositionX((int) ((double) p.getPositionX() + p.getxVel()));
                    p.setPositionY((int) ((double) p.getPositionY() + p.getyVel()));
                    if (p.getPositionX() - BALL_SIZE < 0 || p.getPositionX() + BALL_SIZE > getWidth()) {
                        p.setxVel(-p.getxVel());
                    }
                    if (p.getPositionY() - BALL_SIZE < 0 || p.getPositionY() + BALL_SIZE > getHeight()) {
                        p.setyVel(-p.getyVel());
                    }
                    //Balls
                    //Check to see if ball is below paddle.  If yes, remove ball.  If no more balls exists, game over
                    if (p.getPositionY() - BALL_SIZE > rect.bottom) {
                        balls.remove(p);
                        if (balls.size() == 0) {
                            //User lost.  Spawning in another ball
                            balls.add(new Ball(450, 450, 10, 25));
                        }
                        continue;
                    }
                    //see if ball has hit paddle (or gone below it)
                    if ((rect.contains(p.getPositionX(), p.getPositionY() + BALL_SIZE) || rect.contains(p.getPositionX() - BALL_SIZE, p.getPositionY()) ||
                            rect.contains(p.getPositionX() + BALL_SIZE, p.getPositionY()) || rect.contains(p.getPositionX(), p.getPositionY() - BALL_SIZE)) && !p.bounce) {
                        //Check to see if ball that hit paddle was special ability
                        if (p.getSpecial_ability() == MULTIPLE_BALLS) {
                            balls.remove(p);
                            balls.add(new Ball(getWidth() / 2, getHeight() / 2, 15, 15));
                            continue;
                        } else if (p.getSpecial_ability() == PADDLE_INCREASE) {
                            paddle_length += PADDLE_LENGTH_INCREASE;
                            paddle_thickness += PADDLE_THICKNESS_INCREASE;
                            handler.postDelayed(paddleRunnable, PADDLE_INCREASE_TIME * 1000);
                        } else {
                            if (paddlePlayer.isPlaying()) {
                                paddlePlayer.seekTo(0);
                            } else {
                                paddlePlayer.start();
                            }
                            p.setyVel(-p.getyVel());
                            p.setyVel(p.getyVel() + 1);
                            p.setxVel(p.getxVel() + Math.abs(p.getPositionX() - rect.centerX()) / 5);
                            p.bounce = true;
                        }
                    } else {
                        p.bounce = false;
                    }

                    //see if ball has hit a block
                    for (int i = 0; i < blocks.size(); i++) {
                        Block b = blocks.get(i);
                        int edge = hit(b, p.getPositionX(), p.getPositionY());
                        if (edge >= 0) {
                            if (blockPlayer.isPlaying()) {
                                blockPlayer.seekTo(0);
                            } else {
                                blockPlayer.start();
                            }
                            if (blocks.get(i).specialAbility > 0) {
                                if (blocks.get(i).specialAbility == MULTIPLE_BALLS) {
                                    Log.d("Balls", "Creating Multiple Ball Special Ability");
                                    balls.add(new Ball(blocks.get(i).getX + (blockWidth / 2), blocks.get(i).getY - blockHeight, 0, SPECIAL_ABILITY_FALL_SPEED, SPECIAL_ABILITY_BALL_SIZE, MULTIPLE_BALL_COLOR, blocks.get(i).specialAbility));
                                } else if (blocks.get(i).specialAbility == PADDLE_INCREASE) {
                                    Log.d("Balls", "Creating Increase Paddle Special Ability");
                                    balls.add(new Ball(blocks.get(i).getX + (blockWidth / 2), blocks.get(i).getY - blockHeight, 0, SPECIAL_ABILITY_FALL_SPEED, SPECIAL_ABILITY_BALL_SIZE, PADDLE_INCREASE_COLOR, blocks.get(i).specialAbility));
                                }
                            }

                            blocks.remove(i);
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