package com.rontekhne.oitop;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.SecureRandom;
public class MainActivity extends AppCompatActivity {
    public int input, counter, mx, my;
    public boolean success = false;
    public int[][] board = new int[3][3];
    public int[][] goal = new int[3][3];
    public TextView[] btns = new TextView[9];
    public TextView msg;
    public TextView count;
    public TextView resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btns[0] = findViewById(R.id.button00);
        btns[1] = findViewById(R.id.button01);
        btns[2] = findViewById(R.id.button02);
        btns[3] = findViewById(R.id.button10);
        btns[4] = findViewById(R.id.button11);
        btns[5] = findViewById(R.id.button12);
        btns[6] = findViewById(R.id.button20);
        btns[7] = findViewById(R.id.button21);
        btns[8] = findViewById(R.id.button22);

        msg = findViewById(R.id.message);
        count = findViewById(R.id.count);
        resetBtn = findViewById(R.id.buttonReset);

        counter = 1;

        resetBoard();
        initGoal();
        initBoard(); // randomly
        populateBoard();
    }

    public void getInputBtn(View view)
    {
        if (view instanceof Button) {
            Button clickedButton = (Button) view;
            String buttonText = clickedButton.getText().toString();

            try {
                input = Integer.parseInt(buttonText);

                success = move();

                if (success) {
                    populateBoard();
                    count.setText(counter++ + "ª jogada");
                }

                success = checkWinner();

                if (success) {
                    msg.setText("Parabéns! Você venceu!");
                    resetBoard();
                    initBoard(); // aleatoriamente
                    populateBoard();
                    counter = 0;
                }
            } catch (NumberFormatException e) {
                msg.setText("Entrada inválida");
            }
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
        goal[3-1][3-1] = 0;
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

        for (k = 0, i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++, k++) {
                btns[k].setText(String.valueOf(board[i][j]));
            }
        }
    }

    public boolean move()
    {
        int eq, i, j, zx, zy, inpx, inpy;
        boolean s = false;

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
        if (zx >= 0 && zx < 3 && zy >= 0 && zy < 3 && // set bounds
                (board[zx-1][zy] == input || board[zx+1][zy] == input ||
                        board[zx][zy-1] == input || board[zx][zy+1] == input)) {

            // swap
            board[zx][zy] = board[inpx][inpy];
            board[inpx][inpy] = 0;

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