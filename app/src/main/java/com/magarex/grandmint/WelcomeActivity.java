package com.magarex.grandmint;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magarex.grandmint.Models.User;

public class WelcomeActivity extends AppCompatActivity {

    private TextView lblWelcome, lblVerifyOrNot, lblVerify;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Initializing various fields and button
        lblWelcome = findViewById(R.id.lblWelcome);
        lblVerifyOrNot = findViewById(R.id.lblVerifyOrNot);
        lblVerify = findViewById(R.id.lblVerify);
        btnLogout = findViewById(R.id.btnLogout);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Getting the userId of the current user from firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Extracting username from firebaseDatabase with the help of userId
        FirebaseDatabase.getInstance().getReference("UserInformation")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        //Printing name on lblWelcome
                        lblWelcome.setText("Hello " + user.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(WelcomeActivity.this, "Error Check Your Connection", Toast.LENGTH_LONG).show();
                    }
                });

        //Checking if the user is Verified or not
        if (user != null && user.isEmailVerified()) {
            lblVerifyOrNot.setText("(Verified)");
            lblVerify.setVisibility(View.INVISIBLE);
        } else {
            lblVerifyOrNot.setText("(Not Verified)");
        }

        //Setting onClickListener on btnLogout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                //taking back the user to login Screen
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        //Setting onClickListener on lblVerify
        lblVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Checking if the user is present or not and sending a verification link on his/her respected email
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(WelcomeActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(WelcomeActivity.this, "Failed to Send Email Verification", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(WelcomeActivity.this, "Check your Email to Verify", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
