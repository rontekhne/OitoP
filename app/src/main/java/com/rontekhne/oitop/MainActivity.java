package com.rontekhne.oitop;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
                    r = randomNumber.nextInt(3*3);
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
                btns[k].setText(s);
            }
        }
    }

    public boolean move()
    {
        int eq, i, j, zx, zy, inpx, inpy;
        boolean s;

        eq = i = j = zx = zy = inpx = inpy = 0;

        // get zero position
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    eq = 1;
                    zx = i;
                    zy = j;
                    break;
                }
            }
            if (eq == 1) {
                break;
            }
        }

        // get input position
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] == input) {
                    eq = 1;
                    inpx = i;
                    inpy = j;
                    break;
                }
            }
            if (eq == 1) {
                break;
            }
        }

        // apply rules for movement
        if (((zx == 0 && zy == 0) && (board[zx+1][zy] == input || board[zx][zy+1] == input)) ||
                ((zx == 0 && zy == 1) && (board[zx][zy-1] == input || board[zx+1][zy] == input || board[zx][zy+1] == input)) ||
                ((zx == 0 && zy == 2) && (board[zx][zy-1] == input || board[zx+1][zy] == input)) ||
                ((zx == 1 && zy == 0) && (board[zx-1][zy] == input || board[zx][zy+1] == input || board[zx+1][zy] == input)) ||
                ((zx == 1 && zy == 1) && (board[zx-1][zy] == input || board[zx][zy+1] == input || board[zx+1][zy] == input || board[zx][zy-1] == input)) ||
                ((zx == 1 && zy == 2) && (board[zx-1][zy] == input || board[zx][zy-1] == input || board[zx+1][zy] == input)) ||
                ((zx == 2 && zy == 0) && (board[zx-1][zy] == input || board[zx][zy+1] == input)) ||
                ((zx == 2 && zy == 1) && (board[zx][zy-1] == input || board[zx-1][zy] == input || board[zx][zy+1] == input)) ||
                ((zx == 2 && zy == 2) && (board[zx][zy-1] == input || board[zx-1][zy] == input))) {

            // swap
            Log.e("OITO", "zx: " + zx);
            Log.e("OITO", "zy: " + zy);
            Log.e("OITO", "inpx: " + inpx);
            Log.e("OITO", "inpy: " + inpy);
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