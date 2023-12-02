package com.rontekhne.oitop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.SecureRandom;
public class MainActivity extends AppCompatActivity {
    public int input, counter;
    public boolean success;
    public int[][] board;
    public int[][] goal;
    public Button[] btns;
    public TextView msg;
    public TextView count;
    public Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        success = false;
        board = new int[3][3];
        goal = new int[3][3];
        btns = new Button[9];

        btns[0] = findViewById(R.id.button00);
        btns[1] = findViewById(R.id.button01);
        btns[2] = findViewById(R.id.button02);
        btns[3] = findViewById(R.id.button10);
        btns[4] = findViewById(R.id.button11);
        btns[5] = findViewById(R.id.button12);
        btns[6] = findViewById(R.id.button20);
        btns[7] = findViewById(R.id.button21);
        btns[8] = findViewById(R.id.button22);

        count = findViewById(R.id.count);
        msg = findViewById(R.id.message);
        resetBtn = findViewById(R.id.buttonReset);

        counter = 1;
        count.setText("0");

        resetBoard();
        initGoal();
        initBoard(); // randomly
        populateBoard();
    }

    public void getInput(View view)
    {
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        input = Integer.parseInt(buttonText);

        success = move();

        if (success) {
            populateBoard();
            String c = (counter++) + "ª jogada";
            count.setText(c);
        }

        success = checkWinner();

        if (success) {
            msg.setText("Parabéns! Você venceu!");
            resetBoard();
            initBoard(); // aleatoriamente
            populateBoard();
            counter = 0;
        }

    }

    public void resetBtn(View view)
    {
        resetBoard();
        initBoard(); // randomly
        populateBoard();
        counter = 1;
        count.setText("0");
    }

    public void resetBoard()
    {
        // set all to -1
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = -1;
            }
        }
    }

    public void initGoal()
    {
        // set [0][0] = 1, [0][1] = 2 etc .. [n-1][n-1] = 0
        for (int count = 1, i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++, count++) {
                goal[i][j] = count;
            }
        }
        goal[2][2] = 0;
    }

    public void initBoard()
    {
        SecureRandom randomNumber = new SecureRandom();
        int i, j, x, y, r, k, d;

        i = j = x = y = r = k = d = 0;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                k = 0; // quit do...while
                do { // check for duplicates
                    r = randomNumber.nextInt(9);
                    d = 0; // loop flag

                    // verify duplicates
                    for (x = 0; x < 3; x++) {
                        for (y = 0; y < 3; y++) {
                            if (r == board[x][y]) { // found duplicates
                                d = 1;
                                break;
                            }
                        }
                        if (d == 1) { // quit the loop
                            break;
                        }
                    }

                    if (x == 3 && y == 3) { // no duplicates
                        board[i][j] = r;
                        k = 1;
                    }

                } while (k != 1);
            }
        }
    }

    public void populateBoard()
    {
        int i, j, k;
        String s;

        for (k = 0, i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++, k++) {
                s = Integer.toString(board[i][j]);
                if (board[i][j] == 0) {
                    btns[k].setBackgroundColor(0x00FFFFFF);
                    btns[k].setText("");
                }else {
                    if (board[i][j] == goal[i][j]) {
                        btns[k].setBackground(null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btns[k].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
                        }
                        btns[k].setBackgroundColor(0xFF1BFF79);
                    }else {
                        btns[k].setBackground(null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btns[k].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.pink));
                        }
                        btns[k].setBackgroundColor(0xFFE91E63);
                    }

                    btns[k].setText(s);
                }
            }
        }
    }

    public boolean move()
    {
        int i, j, zx, zy, inpx, inpy;
        boolean s;

        zx = zy = inpx = inpy = 0;

        // get zero and input psoition
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    zx = i;
                    zy = j;
                }else if (board[i][j] == input) {
                    inpx = i;
                    inpy = j;
                }
            }
        }

        // apply rules for movement
        if ((Math.abs(zx - inpx) == 1 && zy == inpy) ||
                (zx == inpx && Math.abs(zy - inpy) == 1)) {
            // swap
            board[zx][zy] = board[inpx][inpy];
            board[inpx][inpy] = 0;


            msg.setText("Boa sorte!");
            s = true;
        } else {
            msg.setText("Movimento inválido!");
            s = false;
        }

        return s;
    }

    public boolean checkWinner()
    {
        int i, j;
        boolean s = true;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] != goal[i][j])
                    s = false;
            }
        }
        return s;
    }
}