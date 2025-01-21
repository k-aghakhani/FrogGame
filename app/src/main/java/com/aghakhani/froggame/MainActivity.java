package com.aghakhani.froggame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView scoreTextView;
    private TextView timerTextView;
    private ImageView frogImageView;
    private RelativeLayout gameLayout;
    private int score = 0;
    private int timeLeft = 180; // 3 minutes in seconds
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isGameActive = true;
    private Animation fadeIn, fadeOut;

    private MediaPlayer clickSound;
    private MediaPlayer winSound;
    private MediaPlayer loseSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        frogImageView = findViewById(R.id.frogImageView);
        gameLayout = findViewById(R.id.gameLayout);

        // Initialize Media Players
        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        winSound = MediaPlayer.create(this, R.raw.win_sound);
        loseSound = MediaPlayer.create(this, R.raw.lose_sound);

        // Load animations
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        startGame();


    }

    private void startGame() {
        updateTimer();
        spawnFrog();
    }

    private void updateTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0 && isGameActive) {
                    timeLeft--;
                    timerTextView.setText("Time: " + timeLeft);
                    updateTimer();
                } else {
                    endGame();
                }
            }
        }, 1000);
    }

    private void spawnFrog() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 1000; // Random delay between 1 to 4 seconds
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isGameActive) {
                        // Set random position for the frog
                        setRandomPosition(frogImageView);

                        // Show frog with fade-in animation
                        frogImageView.startAnimation(fadeIn);
                        frogImageView.setVisibility(View.VISIBLE);

                        frogImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                score++;
                                scoreTextView.setText("Score: " + score);
                                if (clickSound != null) {
                                    clickSound.start();
                                }
                                frogImageView.startAnimation(fadeOut);
                                frogImageView.setVisibility(View.INVISIBLE);
                                spawnFrog();
                            }
                        });

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (frogImageView.getVisibility() == View.VISIBLE) {
                                    frogImageView.startAnimation(fadeOut);
                                    frogImageView.setVisibility(View.INVISIBLE);
                                    spawnFrog();
                                }
                            }
                        }, 1000); // Frog disappears after 1 second
                    }
                }
            }, delay);
        }
    }

    private void setRandomPosition(ImageView imageView) {
        int screenWidth = gameLayout.getWidth();
        int screenHeight = gameLayout.getHeight();
        int frogWidth = imageView.getWidth();
        int frogHeight = imageView.getHeight();

        int maxX = screenWidth - frogWidth;
        int maxY = screenHeight - frogHeight;

        int randomX = random.nextInt(maxX);
        int randomY = random.nextInt(maxY);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.leftMargin = randomX;
        params.topMargin = randomY;
        imageView.setLayoutParams(params);
    }

    private void endGame() {
        isGameActive = false;
        frogImageView.setVisibility(View.INVISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (score >= 10) {
            builder.setTitle("You Win! 🎉")
                    .setMessage("Your score: " + score)
                    .setPositiveButton("OK", null)
                    .setCancelable(false);
            if (winSound != null) {
                winSound.start();
            }
        } else {
            builder.setTitle("You Lose! 😢")
                    .setMessage("Your score: " + score)
                    .setPositiveButton("OK", null)
                    .setCancelable(false);
            if (loseSound != null) {
                loseSound.start();
            }
            builder.create().getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
        }
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
        if (winSound != null) {
            winSound.release();
            winSound = null;
        }
        if (loseSound != null) {
            loseSound.release();
            loseSound = null;
        }
    }


}