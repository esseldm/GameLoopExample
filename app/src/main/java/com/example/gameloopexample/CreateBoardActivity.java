package com.example.gameloopexample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;


public class CreateBoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);

        final EditText rows = (EditText) findViewById(R.id.rows);

        Button addRows = (Button) findViewById(R.id.addRows);
        Button createBoard = (Button) findViewById(R.id.createBoard);

        addRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numRows = Integer.parseInt(rows.getText().toString());
                int numColumns = 10;

                TableLayout tableLayout = new TableLayout(CreateBoardActivity.this);
                for(int i = 0; i < numRows; i++) {
                    TableRow tableRow = new TableRow(CreateBoardActivity.this);
                    tableLayout.addView(tableRow);
                    for(int j = 0; j < numColumns; j++) {
                        final ImageView imageView = new ImageView(CreateBoardActivity.this);
                        imageView.setBackgroundColor(Color.BLACK);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                int color = bitmap.getPixel((int) v.getX(), (int) v.getY());

                                switch(color) {
                                    case Color.BLACK:
                                        imageView.setBackgroundColor(Color.BLUE);
                                        break;
                                    case Color.BLUE:
                                        imageView.setBackgroundColor(Color.RED);
                                        break;
                                    case Color.RED:
                                        imageView.setBackgroundColor(Color.GREEN);
                                        break;
                                    case Color.GREEN:
                                        imageView.setBackgroundColor(Color.YELLOW);
                                        break;
                                    case Color.YELLOW:
                                        imageView.setBackgroundColor(Color.BLACK);
                                        break;
                                }
                            }
                        });
                    }
                }
            }
        });

        createBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
