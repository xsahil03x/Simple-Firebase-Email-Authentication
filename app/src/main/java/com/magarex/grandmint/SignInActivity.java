package com.magarex.grandmint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogin, btnLinkToSignUp;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initializing FirebaseAuth to check if the user is already logged in or not
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        //Initializing various fields and button
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnLinkToSignUp = findViewById(R.id.btnLinkToRegisterScreen);

        //Initializing ProgressDialog with title and message
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loging In");
        dialog.setMessage("Please Wait");

        //Setting onClickListener on btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                String email = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();

                //Signing In User with username and Password with the help of firebase
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(SignInActivity.this,"Invalid Credentials Try Again",Toast.LENGTH_LONG).show();
                        }else{

                            //After successfull Signin taking user to welcome screen
                            dialog.dismiss();
                            Toast.makeText(SignInActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignInActivity.this,WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });


        //Sending user to Register Screen
        btnLinkToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
}
