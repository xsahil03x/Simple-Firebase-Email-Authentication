package com.magarex.grandmint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magarex.grandmint.Models.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button btnRegister, btnLinkToSignin;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase db;
    private DatabaseReference users;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initializing various components
        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.txtUsername);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLinkToSignin = findViewById(R.id.btnLinkToLoginScreen);

        //Initializing progressdialog with title and message
        dialog = new ProgressDialog(this);
        dialog.setTitle("Registering User");
        dialog.setMessage("Please Wait");

        //Initializing FirebaseDatabase and giving reference to the Database Reference
        db = FirebaseDatabase.getInstance();
        users = db.getReference("UserInformation");

        //Sending user to Login screen
        btnLinkToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        //OnClick Listener of btnRegister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                registerUser();
            }
        });
    }

    //Method for registering User With Firebase
    private void registerUser() {

        final String Name = username.getText().toString().trim();
        final String Email = email.getText().toString().trim();
        final String Pass = password.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Error Try Again",Toast.LENGTH_LONG).show();
                }else {

                    //Saving User Data into firebase Database After Successfully creating user with firebase
                    User user = new User();
                    user.setName(Name);
                    user.setEmail(Email);
                    user.setPassword(Pass);

                    users.child(firebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this,"Error Try Again",Toast.LENGTH_LONG).show();
                            }else{

                                //Sending User to Welcome Screen after Successful Registration
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this,WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }

            }
        });
    }
}
