package com.gupta.ram.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEmail, mPassword;
    private Button loginBtn;
    private TextView mCreateBtn;

    private ProgressDialog mProgress;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);

        loginBtn = findViewById(R.id.login_btn);
        mCreateBtn = findViewById(R.id.create_account);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please Wait");


        mAuth = FirebaseAuth.getInstance();


        //----------Click listener to Login Button-----------
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();

                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                    loginUser(email, pass); //calling method to login user
                } else {
                    mProgress.hide();
                    Toast.makeText(LoginActivity.this, "Please Check your Details", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    //---------method to login user----------
    private void loginUser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user.isEmailVerified()) {  // checking whether email has been verified or not

                        Intent statusIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                        startActivity(statusIntent);
                        finish();
                    } else {
                        mAuth.signOut();
                        Toast.makeText(LoginActivity.this, "Please Verify your Email", Toast.LENGTH_LONG).show();
                    }
                }
                mProgress.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    Toast.makeText(LoginActivity.this, "User not verified or not exist", Toast.LENGTH_SHORT).show();
                }
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(LoginActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
