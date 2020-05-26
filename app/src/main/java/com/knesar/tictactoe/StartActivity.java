package com.knesar.tictactoe;


import android.app.AlertDialog;

import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class StartActivity extends AppCompatActivity {

//    Button startButton,multiPlayer,about, exitButton;
    static final String EXTRA_TEXT1 = "com.knesar.tictactoe.knesar.EXTRA_TEXT1";
    static final String EXTRA_TEXT2 = "com.knesar.tictactoe.knesar.EXTRA_TEXT2";
    private EditText editTextFirst;
    private EditText editTextSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
    }

    @Override
    public void onBackPressed() {
        alert();
    }

    public void StartGame(View v) {

        playerInfoDialog();
    }

    public void playerInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);

        LayoutInflater inflater = StartActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        editTextFirst = view.findViewById(R.id.enterXPlayerName);
        editTextSecond = view.findViewById(R.id.enterYPlayerName);
        TextView title = view.findViewById(R.id.dialogTitle);
        Button button1 = view.findViewById(R.id.cancelButton);
        Button button2 = view.findViewById(R.id.okButton);

        title.setText("Enter Player's Names");

        button2.setText("Cancel");
        button1.setText("Ok");

        builder.setView(view);
        final AlertDialog d = builder.create();
        d.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(false);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String player1 = editTextFirst.getText().toString().trim();
                final String player2 = editTextSecond.getText().toString().trim();

                if (!player1.isEmpty() && !player2.isEmpty()) {
                    Intent i = new Intent(StartActivity.this, MainActivity.class);
                    i.putExtra(EXTRA_TEXT1, player1);
                    i.putExtra(EXTRA_TEXT2, player2);
                    startActivity(i);
                    finish();
                    d.dismiss();
                } else
                    Toast.makeText(StartActivity.this, "Please Enter the Player's Names", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void EndGame(View v) {
        alert();

    }

    public void StartGameOnline(View view) {
        Intent intent = new Intent(getApplicationContext(), OnlineLoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void ShowAboutNote(View view) {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }



    public void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        builder.setMessage("Are you sure want to exit")

        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        })
       .setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        })

        .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show();
    }



    }


