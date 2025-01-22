package com.aghakhani.froggame;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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
    private int timeLeft = 30; // 3 minutes in seconds
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
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        frogImageView = findViewById(R.id.frogImageView);
        gameLayout = findViewById(R.id.gameLayout);

        // Load animations
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        // Initialize sounds
        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        winSound = MediaPlayer.create(this, R.raw.win_sound);
        loseSound = MediaPlayer.create(this, R.raw.lose_sound);

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
                        setRandomPosition(frogImageView);
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

        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_end_game, null);
        builder.setView(dialogView);

        // Initialize dialog views
        TextView titleTextView = dialogView.findViewById(R.id.dialogTitle);
        TextView messageTextView = dialogView.findViewById(R.id.dialogMessage);
        Button playAgainButton = dialogView.findViewById(R.id.playAgainButton);
        ImageView iconImageView = dialogView.findViewById(R.id.dialogIcon);

        // Set dialog content based on game result
        if (score >= 10) {
            titleTextView.setText("You Win! 🎉");
            messageTextView.setText("Your score: " + score);
            iconImageView.setImageResource(R.drawable.ic_win); // Win icon
            if (winSound != null) {
                winSound.start();
            }
        } else {
            titleTextView.setText("You Lose! 😢");
            messageTextView.setText("Your score: " + score);
            iconImageView.setImageResource(R.drawable.ic_lose); // Lose icon
            if (loseSound != null) {
                loseSound.start();
            }
        }

        // Set up the Play Again button
        AlertDialog dialog = builder.create();
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                restartGame();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void restartGame() {
        score = 0;
        timeLeft = 180;
        isGameActive = true;
        scoreTextView.setText("Score: 0");
        timerTextView.setText("Time: 180");
        startGame();
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