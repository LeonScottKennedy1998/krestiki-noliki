package com.example.milanahametova;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer backgroundMusic;
    private char[][] board = new char[3][3];
    private boolean vsBot = true;
    private SharedPreferences preferences;
    private SharedPreferences statsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundMusic = MediaPlayer.create(this, R.raw.lp2);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();


        preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        statsPreferences = getSharedPreferences("GameStats", MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("darkTheme", false);

        if (isDarkTheme) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        setContentView(R.layout.activity_main);
        ImageView themeSwitcher = findViewById(R.id.buttonSwitchTheme);
        updateThemeIcon(isDarkTheme);

        themeSwitcher.setOnClickListener(v -> {
            boolean currentTheme = preferences.getBoolean("darkTheme", false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("darkTheme", !currentTheme);
            editor.apply();
            recreate();
        });
        setupGame();
        loadStatistics();
    }
    private void updateThemeIcon(boolean isDarkTheme) {
        ImageView themeSwitcher = findViewById(R.id.buttonSwitchTheme);
        if (isDarkTheme) {
            themeSwitcher.setImageResource(R.drawable.milanastar);
        } else {
            themeSwitcher.setImageResource(R.drawable.milanahametova);
        }
    }
    private void setupGame() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final int row = i / 3;
            final int col = i % 3;

            Button button = (Button) gridLayout.getChildAt(i);
            button.setOnClickListener(v -> makeMove(button, row, col));
        }

        findViewById(R.id.buttonRestart).setOnClickListener(v -> resetGame());
        findViewById(R.id.buttonSwitchTheme).setOnClickListener(v -> toggleTheme());
    }

    private void makeMove(Button button, int row, int col) {
        if (board[row][col] == '\0') {
            board[row][col] = 'X';
            button.setText("X");

            if (checkWin()) {
                updateStatistics(true, false);
                resetGame();
                return;

        } else if (isBoardFull()) {
                updateStatistics(false, false);
                resetGame();
                return;
            }

            if (vsBot) {
                botMove();

                if (checkWin()) {
                    updateStatistics(false, true);
                    resetGame();
                } else if (isBoardFull()) {
                    updateStatistics(false, false);
                    resetGame();
                }
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '\0') {
                return true;
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '\0') {
                return true;
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '\0') {
            return true;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '\0') {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setText("");
        }
        board = new char[3][3];
    }

    private void botMove() {
        if (tryToWinOrBlock('O')) return;
        if (tryToWinOrBlock('X')) return;
        makeRandomMove();
    }

    private boolean tryToWinOrBlock(char symbol) {
        for (int i = 0; i < 3; i++) {
            if (checkLine(symbol, i, 0, i, 1, i, 2)) return true;
            if (checkLine(symbol, 0, i, 1, i, 2, i)) return true;
        }
        if (checkLine(symbol, 0, 0, 1, 1, 2, 2)) return true;
        if (checkLine(symbol, 0, 2, 1, 1, 2, 0)) return true;

        return false;
    }

    private boolean checkLine(char symbol, int r1, int c1, int r2, int c2, int r3, int c3) {
        if (board[r1][c1] == symbol && board[r2][c2] == symbol && board[r3][c3] == '\0') {
            board[r3][c3] = 'O';
            Button button = (Button) ((GridLayout) findViewById(R.id.gridLayout)).getChildAt(r3 * 3 + c3);
            button.setText("O");
            return true;
        }
        if (board[r1][c1] == symbol && board[r3][c3] == symbol && board[r2][c2] == '\0') {
            board[r2][c2] = 'O';
            Button button = (Button) ((GridLayout) findViewById(R.id.gridLayout)).getChildAt(r2 * 3 + c2);
            button.setText("O");
            return true;
        }
        if (board[r2][c2] == symbol && board[r3][c3] == symbol && board[r1][c1] == '\0') {
            board[r1][c1] = 'O';
            Button button = (Button) ((GridLayout) findViewById(R.id.gridLayout)).getChildAt(r1 * 3 + c1);
            button.setText("O");
            return true;
        }
        return false;
    }

    private void makeRandomMove() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != '\0');

        board[row][col] = 'O';
        Button button = (Button) ((GridLayout) findViewById(R.id.gridLayout)).getChildAt(row * 3 + col);
        button.setText("O");
    }

    private void toggleTheme() {
        boolean isDarkTheme = preferences.getBoolean("darkTheme", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("darkTheme", !isDarkTheme);
        editor.apply();
        recreate();
    }

    private void loadStatistics() {
        int wins = statsPreferences.getInt("wins", 0);
        int losses = statsPreferences.getInt("losses", 0);
        int draws = statsPreferences.getInt("draws", 0);

        TextView statsView = findViewById(R.id.statistics);
        statsView.setText("Победы: " + wins + ", Поражения: " + losses + ", Ничьи: " + draws);
    }

    private void updateStatistics(boolean playerWon, boolean botWon) {
        SharedPreferences.Editor editor = statsPreferences.edit();
        if (playerWon) {
            int wins = statsPreferences.getInt("wins", 0);
            editor.putInt("wins", wins + 1);
        } else if (botWon) {
            int losses = statsPreferences.getInt("losses", 0);
            editor.putInt("losses", losses + 1);
        } else {
            int draws = statsPreferences.getInt("draws", 0);
            editor.putInt("draws", draws + 1);
        }
        editor.apply();
        loadStatistics();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}