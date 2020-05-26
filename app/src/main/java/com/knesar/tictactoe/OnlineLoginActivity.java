package com.knesar.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class OnlineLoginActivity extends AppCompatActivity {

    ListView lv_loginUsers;
    ArrayList<String> list_loginUsers = new ArrayList<String>();
    ArrayAdapter adpt;

    ListView lv_requstedUsers;
    ArrayList<String> list_requestedUsers = new ArrayList<String>();
    ArrayAdapter reqUsersAdpt;

    TextView tvUserID, tvSendRequest, tvAcceptRequest;
    String LoginUserID, UserName, LoginUID;

    FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_login);

        tvSendRequest =  findViewById(R.id.tvSendRequest);
        tvAcceptRequest = findViewById(R.id.tvAcceptRequest);

        tvSendRequest.setText("Please wait...");
        tvAcceptRequest.setText("Please wait...");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        lv_loginUsers= findViewById(R.id.lv_loginUsers);
        adpt = new ArrayAdapter(this, R.layout.while_color, list_loginUsers);
        lv_loginUsers.setAdapter(adpt);


        lv_requstedUsers =  findViewById(R.id.lv_requestedUsers);
        reqUsersAdpt = new ArrayAdapter(this, R.layout.while_color, list_requestedUsers);
        lv_requstedUsers.setAdapter(reqUsersAdpt);



        tvUserID = findViewById(R.id.tvLoginUser);




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    LoginUID = user.getUid();
                    Log.d("Auth", "onAuthStateChanged:signed_in:" + LoginUID);
                    LoginUserID = user.getEmail();
                    tvUserID.setText(LoginUserID);
                    UserName = convertEmailToString(LoginUserID);
                    UserName = UserName.replace(".", "");
                    myRef.child("users").child(UserName).child("request").setValue(LoginUID);
                    reqUsersAdpt.clear();
                    AcceptIncommingRequests();
                } else {
                    // User is signed out
                    Log.d("Auth", "onAuthStateChanged:signed_out");
                    JoinOnlineGame();
                }
            }
        };



        myRef.getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateLoginUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        lv_loginUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String requestToUser = ((TextView)view).getText().toString();
                confirmRequest(requestToUser, "To");
            }
        });



        lv_requstedUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String requestFromUser = ((TextView)view).getText().toString();
                confirmRequest(requestFromUser, "From");
            }
        });
    }



    void confirmRequest(final String OtherPlayer, final String reqType){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.connect_player_dialog, null);
        b.setView(dialogView);

        b.setTitle("Start Game?");
        b.setMessage("Connect with " + OtherPlayer);
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myRef.child("users")
                        .child(OtherPlayer).child("request").push().setValue(LoginUserID);
                if(reqType.equalsIgnoreCase("From")) {
                    StartGame(OtherPlayer + ":" + UserName, OtherPlayer, "From");
                }else{
                    StartGame(UserName + ":" + OtherPlayer, OtherPlayer, "To");
                }
            }
        });
        b.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();



    }


    void StartGame(String PlayerGameID, String OtherPlayer, String requestType){
        myRef.child("playing").child(PlayerGameID).removeValue();
        Intent intent = new Intent(getApplicationContext(), OnlineGameActivity.class);
        intent.putExtra("player_session", PlayerGameID);
        intent.putExtra("user_name", UserName);
        intent.putExtra("other_player", OtherPlayer);
        intent.putExtra("login_uid", LoginUID);
        intent.putExtra("request_type", requestType);
        startActivity(intent);
        finish();
    }

    private String convertEmailToString(String Email){
        String value = Email.substring(0, Email.indexOf('@'));
        value = value.replace(".", "");
        return value;
    }


    void AcceptIncommingRequests(){
        myRef.child("users").child(UserName).child("request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if(map != null){
                                String value = "";
                                for(String key:map.keySet()){
                                    value = (String) map.get(key);
                                    reqUsersAdpt.add(convertEmailToString(value));
                                    reqUsersAdpt.notifyDataSetChanged();
                                    myRef.child("users").child(UserName).child("request").setValue(LoginUID);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    public void updateLoginUsers(DataSnapshot dataSnapshot){
        String key = "";
        Set<String> set = new HashSet<String>();
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            key = ((DataSnapshot) i.next()).getKey();
            if(!key.equalsIgnoreCase(UserName)) {
                set.add(key);
            }
        }

        adpt.clear();
        adpt.addAll(set);
        adpt.notifyDataSetChanged();
        tvSendRequest.setText("Send request to");
        tvAcceptRequest.setText("Accept request from");
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void RegisterUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Auth Complete", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Your account is Registered",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    public void JoinOnlineGame() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.login_dialog, null);


        TextView tv2 = dialogView.findViewById(R.id.tvDescription);
        tv2.setText("Enter you email and password for registration");

        Button backButton = dialogView.findViewById(R.id.backButton);
        Button okButton = dialogView.findViewById(R.id.okButton);
        b.setView(dialogView);

        final AlertDialog d = b.create();
        d.show();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setCanceledOnTouchOutside(false);
        final EditText etEmail = dialogView.findViewById(R.id.etEmail);
        final EditText etPassword = dialogView.findViewById(R.id.etPassword);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = etEmail.getText().toString();
                String text2 = etPassword.getText().toString();
                if (!text1.isEmpty() && !text2.isEmpty()) {
                    RegisterUser(text1, text2);

                } else {
                    Toast.makeText(OnlineLoginActivity.this, "Incorrect Email and Password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}