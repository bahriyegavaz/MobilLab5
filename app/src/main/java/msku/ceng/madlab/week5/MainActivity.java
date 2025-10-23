package msku.ceng.madlab.week5;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    static final String PLAYER_1 = "X";
    static final String PLAYER_2 = "O";
    boolean player1_Turn = true;
    byte[][] board = new byte[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.board), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TableLayout table = findViewById(R.id.board);
        for (int i = 0; i < 3; i++) {
            TableRow tableRow = (TableRow) table.getChildAt(i);
            for (int j = 0; j < 3; j++) {
                Button button = (Button) tableRow.getChildAt(j);
                button.setOnClickListener(new CellListener(i, j));
            }
        }
    }

    public boolean isValidMove(int row, int column) {
        return board[row][column] == 0;
    }

    // 🔹 Oyun bitiş kontrolü (yatay, dikey, çapraz + beraberlik)
    public int gameEnded(int row, int col) {
        int symbol = board[row][col];

        if (symbol == 0) {
            return -1;
        }

        boolean win;

        // --- Dikey kontrol ---
        win = true;
        for (int i = 0; i < 3; i++) {
            if (board[i][col] != symbol) {
                win = false;
                break;
            }
        }
        if (win) return symbol;

        // --- Yatay kontrol ---
        win = true;
        for (int j = 0; j < 3; j++) {
            if (board[row][j] != symbol) {
                win = false;
                break;
            }
        }
        if (win) return symbol;

        // --- Ana çapraz kontrolü ---
        if (row == col) {
            win = true;
            for (int i = 0; i < 3; i++) {
                if (board[i][i] != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return symbol;
        }

        // --- Yan çapraz kontrolü ---
        if (row + col == 2) {
            win = true;
            for (int i = 0; i < 3; i++) {
                if (board[i][2 - i] != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return symbol;
        }

        // --- Beraberlik kontrolü ---
        boolean draw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    draw = false;
                    break;
                }
            }
        }
        if (draw) return 0; // berabere

        return -1; // oyun devam ediyor
    }

    class CellListener implements View.OnClickListener {

        int row, column;

        public CellListener(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public void onClick(View v) {
            if (!isValidMove(row, column)) {
                Toast.makeText(MainActivity.this, "Cell is already occupied!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (player1_Turn) {
                ((Button) v).setText(PLAYER_1);
                board[row][column] = 1;
            } else {
                ((Button) v).setText(PLAYER_2);
                board[row][column] = 2;
            }

            int result = gameEnded(row, column);
            if (result == -1) {
                player1_Turn = !player1_Turn;
            } else if (result == 0) {
                Toast.makeText(MainActivity.this, "It's a DRAW!", Toast.LENGTH_LONG).show();
            } else if (result == 1) {
                Toast.makeText(MainActivity.this, "Player X wins!", Toast.LENGTH_LONG).show();
            } else if (result == 2) {
                Toast.makeText(MainActivity.this, "Player O wins!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("player1Turn", player1_Turn);
        byte[] boardSingle = new byte[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardSingle[3 * i + j] = board[i][j];
            }
        }
        outState.putByteArray("board", boardSingle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        player1_Turn = savedInstanceState.getBoolean("player1Turn");
        byte[] boardSingle = savedInstanceState.getByteArray("board");
        for (int i = 0; i < 9; i++) {
            board[i / 3][i % 3] = boardSingle[i];
        }

        TableLayout table = findViewById(R.id.board);
        for (int i = 0; i < 3; i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            for (int j = 0; j < 3; j++) {
                Button button = (Button) row.getChildAt(j);
                if (board[i][j] == 1) {
                    button.setText(PLAYER_1);
                } else if (board[i][j] == 2) {
                    button.setText(PLAYER_2);
                }
            }
        }
    }
}
