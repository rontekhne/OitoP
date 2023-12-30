package com.rontekhne.oitop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import android.os.Build;
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

    private InterstitialAd mInterstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-6776577184982448/1901801652";
    private static final String TAG = "MainActivity";


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

        for (int i = 0; i <= 8; i++) {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getInput(v);
                }
            });
        }

        // ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    public void getInput(View view)
    {
        Button b = (Button) view;
        String buttonText = b.getText().toString();

        if (!buttonText.isEmpty()) {
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
                // resetBoard();
                // initBoard(); // aleatoriamente
                // populateBoard();
                counter = 0;
            }
        } else {
            Log.d("OitoP", "Empty string");
        }
    }

    public void resetBtn(View view)
    {
        // Ads
        AdRequest adRequest = new AdRequest.Builder().build();

        // test ID, change it
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d(TAG, loadAdError.toString());
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });

            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

        // App
        msg.setText("Boa sorte!");
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
                        btns[k].setBackgroundColor(0xFF079E65);
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

        // get zero and input position
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