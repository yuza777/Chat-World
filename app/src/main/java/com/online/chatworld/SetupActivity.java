package com.online.chatworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {
    private EditText Username, Fullname;
    private Button signupBtn;
    private ProgressDialog loadingBar;

    String currentuserID;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        loadingBar = new ProgressDialog(this);
        signupBtn = (Button) findViewById(R.id.signupbtn);
        Username = (EditText) findViewById(R.id.username);
        Fullname = (EditText) findViewById(R.id.fullname);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            currentuserID = mAuth.getCurrentUser().getUid();
        }

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountInformation();
            }
        });
    }
    private void SaveAccountInformation(){
        String username = Username.getText().toString();
        String fullname = Fullname.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "You need a Username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating your Account");
            loadingBar.setMessage(" Please wait just a bit");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("Username", username);
            userMap.put("Fullname", fullname);
            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account has been created, " +
                                "Welcome to Chat World", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Something went wrong" + message,
                                Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
