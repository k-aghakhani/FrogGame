package com.aghakhani.froggame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView scoreTextView, timerTextView;
    private ImageView frogImageView, starImageView, peeImageView;
    private RelativeLayout gameLayout;
    private int score = 0, timeLeft = 180;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isGameActive = true;
    private Animation fadeIn, fadeOut;
    private MediaPlayer clickSound, screamSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        frogImageView = findViewById(R.id.frogImageView);
        starImageView = findViewById(R.id.starImageView);
        peeImageView = findViewById(R.id.peeImageView);
        gameLayout = findViewById(R.id.gameLayout);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        screamSound = MediaPlayer.create(this, R.raw.scream_sound);

        startGame();
    }

    private void startGame() {
        updateTimer();
        spawnFrog();
        spawnStarRandomly();
        spawnPeeRandomly();
    }

    private void updateTimer() {
        handler.postDelayed(() -> {
            if (timeLeft > 0 && isGameActive) {
                timeLeft--;
                timerTextView.setText("Time: " + timeLeft);
                updateTimer();
            } else {
                endGame();
            }
        }, 1000);
    }

    private void spawnFrog() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 1000;
            handler.postDelayed(() -> {
                if (isGameActive) {
                    setRandomPosition(frogImageView);
                    frogImageView.startAnimation(fadeIn);
                    frogImageView.setVisibility(View.VISIBLE);

                    frogImageView.setOnClickListener(v -> {
                        score++;
                        scoreTextView.setText("Score: " + score);
                        if (clickSound != null) clickSound.start();
                        frogImageView.startAnimation(fadeOut);
                        frogImageView.setVisibility(View.INVISIBLE);
                        spawnFrog();
                    });

                    handler.postDelayed(() -> {
                        if (frogImageView.getVisibility() == View.VISIBLE) {
                            frogImageView.startAnimation(fadeOut);
                            frogImageView.setVisibility(View.INVISIBLE);
                            spawnFrog();
                        }
                    }, 1000);
                }
            }, delay);
        }
    }

    private void spawnStarRandomly() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 2000;
            handler.postDelayed(() -> {
                if (isGameActive && random.nextInt(2) == 0) {
                    setRandomPosition(starImageView);
                    starImageView.startAnimation(fadeIn);
                    starImageView.setVisibility(View.VISIBLE);

                    starImageView.setOnClickListener(v -> {
                        score += 20;
                        scoreTextView.setText("Score: " + score);
                        if (clickSound != null) clickSound.start();
                        starImageView.startAnimation(fadeOut);
                        starImageView.setVisibility(View.INVISIBLE);
                        spawnStarRandomly();
                    });

                    handler.postDelayed(() -> {
                        if (starImageView.getVisibility() == View.VISIBLE) {
                            starImageView.startAnimation(fadeOut);
                            starImageView.setVisibility(View.INVISIBLE);
                        }
                        spawnStarRandomly();
                    }, 800);
                } else {
                    spawnStarRandomly();
                }
            }, delay);
        }
    }

    private void spawnPeeRandomly() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 1000;
            handler.postDelayed(() -> {
                if (isGameActive && random.nextInt(2) == 0) {
                    setRandomPosition(peeImageView);
                    peeImageView.startAnimation(fadeIn);
                    peeImageView.setVisibility(View.VISIBLE);

                    peeImageView.setOnClickListener(v -> {
                        score -= 50;
                        scoreTextView.setText("Score: " + score);
                        if (screamSound != null) screamSound.start();
                        peeImageView.startAnimation(fadeOut);
                        peeImageView.setVisibility(View.INVISIBLE);
                        spawnPeeRandomly();
                    });

                    handler.postDelayed(() -> {
                        if (peeImageView.getVisibility() == View.VISIBLE) {
                            peeImageView.startAnimation(fadeOut);
                            peeImageView.setVisibility(View.INVISIBLE);
                        }
                        spawnPeeRandomly();
                    }, 600);
                } else {
                    spawnPeeRandomly();
                }
            }, delay);
        }
    }

    private void setRandomPosition(ImageView imageView) {
        int screenWidth = gameLayout.getWidth();
        int screenHeight = gameLayout.getHeight();
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        int maxX = screenWidth - width;
        int maxY = screenHeight - height;

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
        starImageView.setVisibility(View.INVISIBLE);
        peeImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
        if (screamSound != null) {
            screamSound.release();
            screamSound = null;
        }
    }
}