package com.aghakhani.froggame;

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

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView scoreTextView;
    private TextView timerTextView;
    private ImageView frogImageView;
    private RelativeLayout gameLayout;
    private int score = 0;
    private int timeLeft = 60; // 3 minutes in seconds
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isGameActive = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        frogImageView = findViewById(R.id.frogImageView);
        gameLayout = findViewById(R.id.gameLayout);

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

                        frogImageView.setVisibility(View.VISIBLE);
                        frogImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                score++;
                                scoreTextView.setText("Score: " + score);
                                frogImageView.setVisibility(View.INVISIBLE);
                                spawnFrog();
                            }
                        });

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (frogImageView.getVisibility() == View.VISIBLE) {
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
        // Get screen dimensions
        int screenWidth = gameLayout.getWidth();
        int screenHeight = gameLayout.getHeight();

        // Get frog dimensions
        int frogWidth = imageView.getWidth();
        int frogHeight = imageView.getHeight();

        // Generate random position within screen bounds
        int maxX = screenWidth - frogWidth;
        int maxY = screenHeight - frogHeight;

        int randomX = random.nextInt(maxX);
        int randomY = random.nextInt(maxY);

        // Set new position
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
            builder.setTitle("You Win!")
                    .setMessage("Your score: " + score)
                    .setPositiveButton("OK", null)
                    .setCancelable(false);
        } else {
            builder.setTitle("You Lose!")
                    .setMessage("Your score: " + score)
                    .setPositiveButton("OK", null)
                    .setCancelable(false);
            builder.create().getWindow().setBackgroundDrawableResource(android.R.color.holo_red_light);
        }
        builder.show();
    }


}