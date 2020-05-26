package com.knesar.tictactoe;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineGameActivity extends AppCompatActivity {

    TextView xTurnTextView, yTurnTextView, xPlayerNameText, yPlayerNameText;

    SoundPool soundPool;
    int button_press, button_sound, ohh_sound, chearing_sound;

    String playerSession = "";
    String userName = "";
    String otherPlayer = "";
    String loginUID = "";
    String requestType = "", myGameSign = "X";

    Button scoreBoardPlayer1, scoreBoardPlayer2;


    int playerScore1 = 0;
    int playerScore2 = 0;
    int gameState = 0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        userName = getIntent().getExtras().get("user_name").toString();
        loginUID = getIntent().getExtras().get("login_uid").toString();
        otherPlayer = getIntent().getExtras().get("other_player").toString();
        requestType = getIntent().getExtras().get("request_type").toString();
        playerSession = getIntent().getExtras().get("player_session").toString();

        xTurnTextView = findViewById(R.id.xTurnTextView);
        yTurnTextView = findViewById(R.id.yTurnTextView);

        xPlayerNameText = findViewById(R.id.xPlayerNameText);
        yPlayerNameText = findViewById(R.id.yPlayerNameText);

        scoreBoardPlayer1 = findViewById(R.id.playerX_Score);
        scoreBoardPlayer2 = findViewById(R.id.playerY_Score);





        // *********** setting sound to the button *****************
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        button_press = soundPool.load(this, R.raw.button_press, 1);
        button_sound = soundPool.load(this, R.raw.button_sound, 1);
        ohh_sound = soundPool.load(this, R.raw.ohh_sound, 1);
        chearing_sound = soundPool.load(this, R.raw.chearing_sound, 1);


        //  ******** Creating Score child in the Database  ******* //
        myRef.child("Scores").child(userName).setValue(playerScore1);
        myRef.child("Scores").child(otherPlayer).setValue(playerScore2);



        myRef.child("Scores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String score1 ="";

                score1 = dataSnapshot.child(userName).getValue().toString();
                scoreBoardPlayer2.setText("Scores:" + score1);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child("Scores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String score2="";
                score2 = dataSnapshot.child(otherPlayer).getValue().toString();

                scoreBoardPlayer1.setText("Scores : " + score2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // **********  send request will get X  ************ //
        gameState = 1;
        if (requestType.equals("From")) {

            myGameSign = "X";

            xTurnTextView.setText("");
            xTurnTextView.setTextSize(20);

            yTurnTextView.setText("Your turn");
            yTurnTextView.setTextSize(20);

            xPlayerNameText.setText(otherPlayer);
            yPlayerNameText.setText(userName);


            myRef.child("playing").child(playerSession).child("turn").setValue(otherPlayer);
            //setEnableClick(false);

            // ***********  accepted request will get O  *************  //
        } else {


            myGameSign = "O";

            xTurnTextView.setText(otherPlayer + "\'s turn");
            xTurnTextView.setTextSize(20);

            yTurnTextView.setText("");
            yTurnTextView.setTextSize(20);

            xPlayerNameText.setText(otherPlayer);
            yPlayerNameText.setText(userName);





            myRef.child("playing").child(playerSession).child("turn").setValue(userName);
            //setEnableClick(true);

        }

        myRef.child("playing").child(playerSession).child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String value = (String) dataSnapshot.getValue();
                    if (value.equals(userName)) {
                        xTurnTextView.setText("Your turn");
                        yTurnTextView.setText("");
                        setEnableClick(true);
                        activePlayer = 1;
                    } else if (value.equals(otherPlayer)) {
                        yTurnTextView.setText(otherPlayer + "\'s turn");
                        xTurnTextView.setText("");
                        setEnableClick(false);
                        activePlayer = 2;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child("playing").child(playerSession).child("game")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            player1.clear();
                            player2.clear();
                            activePlayer = 2;
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
                                String value = "";
                                String firstPlayer = userName;
                                for (String key : map.keySet()) {
                                    value = (String) map.get(key);
                                    if (value.equals(userName)) {
                                        //activePlayer = myGameSign.equals("X")?1:2;
                                        activePlayer = 2;
                                    } else {
                                        //activePlayer = myGameSign.equals("X")?2:1;
                                        activePlayer = 1;
                                    }
                                    firstPlayer = value;
                                    String[] splitID = key.split(":");
                                    OtherPlayer(Integer.parseInt(splitID[1]));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
        builder.setMessage("Are you sure want to go back ?");
        builder.setCancelable(true);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent i = new Intent( OnlineGameActivity.this , StartActivity.class);
                startActivity(i);
                finish();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void btnOnClick(View view) {

        Button btnSelected = (Button) view;

        if (playerSession.length() <= 0) {
            Intent intent = new Intent(getApplicationContext(), OnlineLoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            int boxId = 0;

            switch ((btnSelected.getId())) {
                case R.id.button00:
                    boxId = 1;
                    break;

                case R.id.button01:
                    boxId = 2;
                    break;

                case R.id.button02:
                    boxId = 3;
                    break;

                case R.id.button10:
                    boxId = 4;
                    break;


                case R.id.button11:
                    boxId = 5;
                    break;

                case R.id.button12:
                    boxId = 6;
                    break;

                case R.id.button20:
                    boxId = 7;
                    break;

                case R.id.button21:
                    boxId = 8;
                    break;

                case R.id.button22:
                    boxId = 9;
                    break;
            }
            myRef.child("playing").child(playerSession).child("game").child("block:" + boxId).setValue(userName);
            myRef.child("playing").child(playerSession).child("turn").setValue(otherPlayer);
            setEnableClick(false);
            activePlayer = 2;
            playGame(boxId, btnSelected);
        }
    }

    int activePlayer = 1;
    ArrayList<Integer> player1 = new ArrayList<>();  // player 1 data
    ArrayList<Integer> player2 = new ArrayList<>();  //player 2 data



    //  ******  playing game  ****** //

    private void playGame(int boxId, Button btnSelected) {

        if (gameState == 1) {
            if (activePlayer == 1) {

                //play a sound using SoundPool while pressing a button
                soundPool.play(button_sound, 1, 1, 0, 0, 1);

                btnSelected.setText("O");
                btnSelected.setTextSize(80);
                btnSelected.setTextColor(Color.parseColor("#ffff00"));
                player1.add(boxId);
                activePlayer = 2;
            } else if (activePlayer == 2) {

                //play a sound while pressing a button
                soundPool.play(button_sound, 1, 1, 0, 0, 1);

                btnSelected.setText("X");
                btnSelected.setTextSize(80);
                btnSelected.setTextColor(Color.parseColor("#0000a0"));
                player2.add(boxId);
                activePlayer = 1;
            }
            btnSelected.setEnabled(false);
            checkWinner();
        }
    }

    //  ************* checking winner ********* //

    public void checkWinner() {

        int winner = 0;
        //row1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3))
            winner = 1;

        if (player2.contains(1) && player2.contains(2) && player2.contains(3))
            winner = 2;

        //row2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6))
            winner = 1;
        if (player2.contains(4) && player2.contains(5) && player2.contains(6))
            winner = 2;

        //row3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9))
            winner = 1;
        if (player2.contains(7) && player2.contains(8) && player2.contains(9))
            winner = 2;

        //column1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7))
            winner = 1;
        if (player2.contains(1) && player2.contains(4) && player2.contains(7))
            winner = 2;

        //column2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8))
            winner = 1;
        if (player2.contains(2) && player2.contains(5) && player2.contains(8))
            winner = 2;

        //column3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9))
            winner = 1;
        if (player2.contains(3) && player2.contains(6) && player2.contains(9))
            winner = 2;

        //diagonal
        if (player1.contains(1) && player1.contains(5) && player1.contains(9))
            winner = 1;
        if (player2.contains(1) && player2.contains(5) && player2.contains(9))
            winner = 2;

        //diagonal2
        if (player1.contains(3) && player1.contains(5) && player1.contains(7))
            winner = 1;
        if (player2.contains(3) && player2.contains(5) && player2.contains(7))
            winner = 2;


        if (winner != 0 && gameState == 1) {

            if (winner == 1) {
                playerScore1++;
                myRef.child("Scores").child(otherPlayer).setValue(playerScore1);
//                 resetGame();
                ShowLooserAlert(otherPlayer + " is winner");

                // It will play a chearing sound
                soundPool.play(chearing_sound, 1, 1, 0, 0, 1);

            } else if (winner == 2) {
                playerScore2++;
                myRef.child("Scores").child(userName).setValue(playerScore2);
//                resetGame();
                ShowWinnerAlert("You won the game");

                // It will play a chearing sound
                soundPool.play(chearing_sound, 1, 1, 0, 0, 1);



            }
            gameState = 2;
        }

        ArrayList<Integer> emptyBlocks = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (!(player1.contains(i) || player2.contains(i))) {
                emptyBlocks.add(i);
            }
        }
        if (emptyBlocks.size() == 0) {
            if (gameState == 1) {

                // it will play a Ohh_sound.
                soundPool.play(ohh_sound, 1, 1, 0, 0, 1);

                ShowDrawGameAlert("Game Draw");

            }
            gameState = 3;
        }

    }

    void OtherPlayer(int boxId) {

        Button btnSelected = findViewById(R.id.button00);
        switch (boxId) {
            case 1:
                btnSelected = findViewById(R.id.button00);
                break;
            case 2:
                btnSelected = findViewById(R.id.button01);
                break;
            case 3:
                btnSelected = findViewById(R.id.button02);
                break;

            case 4:
                btnSelected = findViewById(R.id.button10);
                break;
            case 5:
                btnSelected = findViewById(R.id.button11);
                break;
            case 6:
                btnSelected = findViewById(R.id.button12);
                break;

            case 7:
                btnSelected = findViewById(R.id.button20);
                break;
            case 8:
                btnSelected = findViewById(R.id.button21);
                break;
            case 9:
                btnSelected = findViewById(R.id.button22);
                break;
        }

        playGame(boxId, btnSelected);
    }


    ///////////  ***************** resting game from database and from application  ************* /////////////


    void resetGame() {
        gameState = 1;
        activePlayer = 1;
        player1.clear();
        player2.clear();

        myRef.child("playing").child(playerSession).removeValue();


        Button b;
        b = findViewById(R.id.button00);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button01);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button02);
        b.setText("");
        b.setEnabled(true);

        b = findViewById(R.id.button10);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button11);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button12);
        b.setText("");
        b.setEnabled(true);

        b = findViewById(R.id.button20);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button21);
        b.setText("");
        b.setEnabled(true);
        b = findViewById(R.id.button22);
        b.setText("");
        b.setEnabled(true);


    }
//    void resetGame2() {
//        gameState = 1;
//        activePlayer = 1;
//        player1.clear();
//        player2.clear();
//
////        myRef.child("playing").child(playerSession).removeValue();
//
//
//        Button b;
//        b = findViewById(R.id.button00);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button01);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button02);
//        b.setText("");
//        b.setEnabled(true);
//
//        b = findViewById(R.id.button10);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button11);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button12);
//        b.setText("");
//        b.setEnabled(true);
//
//        b = findViewById(R.id.button20);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button21);
//        b.setText("");
//        b.setEnabled(true);
//        b = findViewById(R.id.button22);
//        b.setText("");
//        b.setEnabled(true);
//
//
//    }

//
//    ////// **********  resting Score from the Database and from the application ************  ////////
//
//    public void resetScores(View view) {
//        int resetScr = 0;
//        myRef.child("Scores").child(userName).setValue(resetScr);
//        myRef.child("Scores").child(otherPlayer).setValue(resetScr);
//    }


    /////////   ***************  AlertDialogs  ****************  ////////////////////


    void ShowWinnerAlert(String Title) {

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

    void ShowLooserAlert(String Title){

        AlertDialog.Builder b = new AlertDialog.Builder(this,R.style.TransparentDialogButton);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.lossing_dialog, null);


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

    // ***** setting button Enable or Disable ****** //

    void setEnableClick(boolean trueORfalse) {
        Button b;
        b = findViewById(R.id.button00);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button01);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button02);
        b.setClickable(trueORfalse);

        b = findViewById(R.id.button10);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button11);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button12);
        b.setClickable(trueORfalse);

        b = findViewById(R.id.button20);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button21);
        b.setClickable(trueORfalse);
        b = findViewById(R.id.button22);
        b.setClickable(trueORfalse);

    }


    public void goBack(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGameActivity.this);
        builder.setMessage("Are you sure want to go back ?");
        builder.setCancelable(true);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent i = new Intent( OnlineGameActivity.this , StartActivity.class);
                startActivity(i);
                finish();
            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });
        builder.create();
        builder.show();
    }
}

