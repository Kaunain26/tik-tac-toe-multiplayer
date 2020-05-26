package com.knesar.tictactoe;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SoundPool soundPool;
    private int button_press, button_sound, ohh_sound, chearing_sound;
    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCounts = 0;
    int player1Points = 0;
    int player2Points = 0;
    String text1, text2;
    Button textViewPlayer1, textViewPlayer2, reset_game_button, reset_score_button;
    TextView xTurnTextView, yTurnTextView, xPlayerNameText, yPlayerNameText;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //********  fetching player name from DialogBox *******
        Intent intent = getIntent();
        text1 = intent.getStringExtra(StartActivity.EXTRA_TEXT1);
        text2 = intent.getStringExtra(StartActivity.EXTRA_TEXT2);

        xPlayerNameText = findViewById(R.id.xPlayerNameText);
        yPlayerNameText = findViewById(R.id.yPlayerNameText);

        xPlayerNameText.setText(text1);
        yPlayerNameText.setText(text2);

        xPlayerNameText = findViewById(R.id.xPlayerNameText);
        yPlayerNameText = findViewById(R.id.yPlayerNameText);

        // *********** setting sound to the button *****************
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        button_press = soundPool.load(this, R.raw.button_press, 1);
        button_sound = soundPool.load(this, R.raw.button_sound, 1);
        ohh_sound = soundPool.load(this, R.raw.ohh_sound, 1);
        chearing_sound = soundPool.load(this, R.raw.chearing_sound, 1);


        //getting the id of textView of score from xml file
        textViewPlayer1 = findViewById(R.id.playerX_Score);
        textViewPlayer2 = findViewById(R.id.playerY_Score);

        xTurnTextView = findViewById(R.id.xTurnTextView);
        yTurnTextView = findViewById(R.id.yTurnTextView);

        // getting id of reset button of score from xml file
        reset_game_button = findViewById(R.id.resetButton);
        reset_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        reset_score_button = findViewById(R.id.resetScore);
        reset_score_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetScore();
            }
        });


        buttons[0][0] = findViewById(R.id.button00);
        buttons[0][1] = findViewById(R.id.button01);
        buttons[0][2] = findViewById(R.id.button02);
        buttons[1][0] = findViewById(R.id.button10);
        buttons[1][1] = findViewById(R.id.button11);
        buttons[1][2] = findViewById(R.id.button12);
        buttons[2][0] = findViewById(R.id.button20);
        buttons[2][1] = findViewById(R.id.button21);
        buttons[2][2] = findViewById(R.id.button22);

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        backPressedAlert();
    }

    @Override
    public void onClick(View v) {

        Button b = (Button) v;

        if (!b.getText().toString().equals("")) {

            return;
        }
        if (player1Turn) {

            //play a sound using SoundPool while pressing a button
            soundPool.play(button_press, 1, 1, 0, 0, 1);

            //setting the X at the button
            b.setText("X");
            xTurnTextView.setText("O's Turn");
            yTurnTextView.setText("");
            xTurnTextView.setTextSize(20);

            b.setTextSize(80);
            b.setTextColor(Color.parseColor(("#0000a0")));

        } else {

            //play a sound while pressing a button
            soundPool.play(button_sound, 1, 1, 0, 0, 1);

            //setting the O at the button
            b.setText("O");
            yTurnTextView.setText("X's Turn");
            xTurnTextView.setText("");
            yTurnTextView.setTextSize(20);

            b.setTextSize(80);
            b.setTextColor(Color.parseColor(("#ffff00")));
        }
        roundCounts++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();

            } else {
                player2Wins();

            }
        } else if (roundCounts == 9) {
            gameDraw();


        } else {
            player1Turn = !player1Turn;
        }
    }


    private void player1Wins() {

        player1Points++;
        soundPool.play(chearing_sound, 1, 1, 0, 0, 1);
        xTurnTextView.setText("");
        yTurnTextView.setText("");
        ShowAlert(text1 + " is Winner!!");
        updatePointsText();

    }

    private void player2Wins() {

        player2Points++;
        soundPool.play(chearing_sound, 1, 1, 0, 0, 1);
        xTurnTextView.setText("");
        yTurnTextView.setText("");
        ShowAlert(text2 + " is Winner!!");
        updatePointsText();

    }

    private void gameDraw() {
        xTurnTextView.setText("");
        yTurnTextView.setText("");
        soundPool.play(ohh_sound, 1, 1, 0, 0, 1);
        ShowDrawGameAlert("Game Draw!!");
        resetBoard();
    }

    private void updatePointsText() {

        textViewPlayer1.setText("X's Scores:" + player1Points);
        textViewPlayer2.setText("0's Scores:" + player2Points);

    }

    public void backPressedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure want to go back to menu");
        builder.setCancelable(true);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Intent intent = new Intent(getApplicationContext(),StartActivity.class);
               startActivity(intent);
                finish();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    void ShowAlert(String Title) {

        AlertDialog.Builder b = new AlertDialog.Builder(this,R.style.TransparentDialogButton);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.winnig_dialog, null);


        TextView tv2 = dialogView.findViewById(R.id.tvDescription);
        tv2.setText("Do You Want to play again?");

        TextView tv1 = dialogView.findViewById(R.id.tvTitle);
        tv1.setText(Title);

        Button mainMenu = dialogView.findViewById(R.id.mainButton);
        mainMenu.setText("Main menu");
        Button yesButton = dialogView.findViewById(R.id.yesButton);
        yesButton.setText("Yes");
        b.setView(dialogView);

        final AlertDialog d = b.create();
        d.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(false);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                d.dismiss();
            }
        });

       mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        finish();

            }
        });


    }

    void ShowDrawGameAlert(String Title) {
        AlertDialog.Builder b = new AlertDialog.Builder(this,R.style.TransparentDialogButton);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.draw_game_dialog, null);


        TextView tv2 = dialogView.findViewById(R.id.tvDescription);
        tv2.setText("Do You Want to play again?");

        TextView tv1 = dialogView.findViewById(R.id.tvTitle);
        tv1.setText(Title);

        Button mainMenu = dialogView.findViewById(R.id.mainButton);
        mainMenu.setText("Main menu");
        Button yesButton = dialogView.findViewById(R.id.yesButton);
        yesButton.setText("Yes");
        b.setView(dialogView);

        final AlertDialog d = b.create();
        d.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(false);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                d.dismiss();
            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }


    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");

        xTurnTextView.setText("");
        yTurnTextView.setText("X's Turn");
        roundCounts = 0;
        player1Turn = true;

    }

    private void resetScore() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
    }

    private void resetGame() {

        updatePointsText();
        resetBoard();
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                field[i][j] = buttons[i][j].getText().toString();


        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }

        }
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }
        return false;
    }


}