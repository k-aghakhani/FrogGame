package com.aghakhani.froggame;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private TextView scoreTextView, timerTextView, highScoreTextView;
    private ImageView frogImageView, starImageView, peeImageView;
    private RelativeLayout gameLayout;
    private Button startButton;

    // Game state variables
    private int score = 0, timeLeft = 180, highScore = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isGameActive = false; // Tracks if the game is currently running
    private boolean hasGameStarted = false; // Tracks if the game has ever started

    // Animations and sounds
    private Animation fadeIn, fadeOut, scaleUp;
    private MediaPlayer clickSound, screamSound, starSound, gameOver;

    // SharedPreferences for saving high score
    private SharedPreferences prefs;

    // Constants for better maintainability
    private static final int FROG_SCORE = 1;
    private static final int STAR_SCORE = 20;
    private static final int PEE_PENALTY = 50;
    private static final int INITIAL_TIME = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        frogImageView = findViewById(R.id.frogImageView);
        starImageView = findViewById(R.id.starImageView);
        peeImageView = findViewById(R.id.peeImageView);
        gameLayout = findViewById(R.id.gameLayout);
        startButton = findViewById(R.id.startButton);

        // Load animations
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        // Load sound effects
        clickSound = MediaPlayer.create(this, R.raw.click_sound);
        screamSound = MediaPlayer.create(this, R.raw.scream_sound);
        starSound = MediaPlayer.create(this, R.raw.star_sound);
        gameOver = MediaPlayer.create(this, R.raw.lose_sound);

        // Load high score from SharedPreferences
        prefs = getSharedPreferences("FrogGamePrefs", MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);
        highScoreTextView.setText("High Score: " + highScore);

        // Ensure layout is fully loaded before showing start button
        gameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                showStartButton();
            }
        });
    }

    // Show the start button and initialize UI
    private void showStartButton() {
        startButton.setVisibility(View.VISIBLE);
        scoreTextView.setText("Score: 0");
        timerTextView.setText("Time: " + INITIAL_TIME); // Display initial time without starting countdown
        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.GONE);
            startGame();
        });
    }

    // Start a new game
    private void startGame() {
        score = 0;
        timeLeft = INITIAL_TIME;
        isGameActive = true;
        hasGameStarted = true; // Mark that the game has officially started
        scoreTextView.setText("Score: " + score);
        timerTextView.setText("Time: " + timeLeft);
        handler.removeCallbacksAndMessages(null); // Clear any previous handlers
        updateTimer(); // Start the timer only when the game begins
        spawnFrog();
        spawnStarRandomly();
        spawnPeeRandomly();
    }

    // Update the game timer recursively
    private void updateTimer() {
        handler.postDelayed(() -> {
            if (timeLeft > 0 && isGameActive) {
                timeLeft--;
                timerTextView.setText("Time: " + timeLeft);
                updateTimer(); // Schedule the next tick
            } else if (isGameActive) {
                endGame(); // End the game if time runs out
            }
        }, 1000);
    }

    // Spawn frogs randomly on the screen
    private void spawnFrog() {
        if (isGameActive) {
            int delay = random.nextInt(2000) + 1000; // Random delay between 1-3 seconds
            handler.postDelayed(() -> {
                if (isGameActive) {
                    setRandomPosition(frogImageView);
                    frogImageView.startAnimation(fadeIn);
                    frogImageView.setVisibility(View.VISIBLE);

                    frogImageView.setOnClickListener(v -> {
                        score += FROG_SCORE;
                        updateScore();
                        playSound(clickSound);
                        frogImageView.startAnimation(fadeOut);
                        frogImageView.setVisibility(View.INVISIBLE);
                        spawnFrog();
                    });

                    // Hide frog if not clicked within 1 second
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

    // Spawn stars randomly with a chance
    private void spawnStarRandomly() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 2000; // Random delay between 2-5 seconds
            handler.postDelayed(() -> {
                if (isGameActive && random.nextInt(2) == 0) {
                    setRandomPosition(starImageView);
                    starImageView.startAnimation(scaleUp);
                    starImageView.setVisibility(View.VISIBLE);

                    starImageView.setOnClickListener(v -> {
                        score += STAR_SCORE;
                        updateScore();
                        playSound(starSound);
                        starImageView.startAnimation(fadeOut);
                        starImageView.setVisibility(View.INVISIBLE);
                        spawnStarRandomly();
                    });

                    // Hide star if not clicked within 0.8 seconds
                    handler.postDelayed(() -> {
                        if (starImageView.getVisibility() == View.VISIBLE) {
                            starImageView.startAnimation(fadeOut);
                            starImageView.setVisibility(View.INVISIBLE);
                        }
                        spawnStarRandomly();
                    }, 800);
                } else {
                    spawnStarRandomly(); // Retry if no star spawned
                }
            }, delay);
        }
    }

    // Spawn pee randomly with a chance
    private void spawnPeeRandomly() {
        if (isGameActive) {
            int delay = random.nextInt(3000) + 1000; // Random delay between 1-4 seconds
            handler.postDelayed(() -> {
                if (isGameActive && random.nextInt(2) == 0) {
                    setRandomPosition(peeImageView);
                    peeImageView.startAnimation(fadeIn);
                    peeImageView.setVisibility(View.VISIBLE);

                    peeImageView.setOnClickListener(v -> {
                        score -= PEE_PENALTY;
                        updateScore();
                        playSound(screamSound);
                        peeImageView.startAnimation(fadeOut);
                        peeImageView.setVisibility(View.INVISIBLE);
                        spawnPeeRandomly();
                    });

                    // Hide pee if not clicked within 0.6 seconds
                    handler.postDelayed(() -> {
                        if (peeImageView.getVisibility() == View.VISIBLE) {
                            peeImageView.startAnimation(fadeOut);
                            peeImageView.setVisibility(View.INVISIBLE);
                        }
                        spawnPeeRandomly();
                    }, 600);
                } else {
                    spawnPeeRandomly(); // Retry if no pee spawned
                }
            }, delay);
        }
    }

    // Set a random position for an ImageView within the game layout
    private void setRandomPosition(ImageView imageView) {
        int screenWidth = gameLayout.getWidth();
        int screenHeight = gameLayout.getHeight();
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        int maxX = Math.max(0, screenWidth - width);
        int maxY = Math.max(0, screenHeight - height);

        int randomX = random.nextInt(maxX);
        int randomY = random.nextInt(maxY);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.leftMargin = randomX;
        params.topMargin = randomY;
        imageView.setLayoutParams(params);
    }

    // Update the score and high score display
    private void updateScore() {
        scoreTextView.setText("Score: " + score);
        if (score > highScore) {
            highScore = score;
            highScoreTextView.setText("High Score: " + highScore);
            prefs.edit().putInt("highScore", highScore).apply();
        }
    }

    // Play a sound effect safely
    private void playSound(MediaPlayer sound) {
        if (sound != null) {
            sound.seekTo(0);
            sound.start();
        }
    }

    // End the game and clean up
    private void endGame() {
        isGameActive = false;
        handler.removeCallbacksAndMessages(null); // Stop all scheduled tasks
        frogImageView.setVisibility(View.INVISIBLE);
        starImageView.setVisibility(View.INVISIBLE);
        peeImageView.setVisibility(View.INVISIBLE);
        showGameOverDialog();
    }

    // Show game over dialog with score and replay option
    private void showGameOverDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your Score: " + score + "\nHigh Score: " + highScore + "\nPlay again?")
                .setPositiveButton("Yes", (dialog, which) -> startGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
        playSound(gameOver);
    }

    // Pause the game when the app goes to the background
    @Override
    protected void onPause() {
        super.onPause();
        isGameActive = false;
        handler.removeCallbacksAndMessages(null); // Stop all scheduled tasks
    }

    // Resume the game only if it was previously started
    @Override
    protected void onResume() {
        super.onResume();
        // Only resume the game if it was started and paused
        if (hasGameStarted && !isGameActive && startButton.getVisibility() == View.GONE) {
            isGameActive = true;
            updateTimer(); // Resume the timer
            spawnFrog();
            spawnStarRandomly();
            spawnPeeRandomly();
        }
    }

    // Clean up resources when the activity is destroyed
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
        if (starSound != null) {
            starSound.release();
            starSound = null;
        }
        if (gameOver != null) {
            gameOver.release();
            gameOver = null;
        }
    }
}