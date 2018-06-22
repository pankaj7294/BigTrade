package com.example.pankajbharati.assignment;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.pankajbharati.assignment.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private EditText mName, mEmail, mPassword, confirPassword;
    private TextView loginTV;
    private Button signUpBtn;
    private ProgressDialog mProgress;

    private FirebaseUser mCurrent_user;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("BigTrade");

        mName = findViewById(R.id.user_name_id);
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.password_id);
        confirPassword = findViewById(R.id.confirm_pass_id);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading...");


        signUpBtn = findViewById(R.id.reg_btn_id);
        loginTV = findViewById(R.id.login_text);



        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String name = mName.getText().toString().trim();
               String email = mEmail.getText().toString().trim();
               String password = mPassword.getText().toString();
               String confirm_pass = confirPassword.getText().toString();
               mProgress.show();

               if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                       && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_pass)){


                   if(password.equals(confirm_pass)){

                       registerUser( name, email,password);
                   }else{
                       mProgress.hide();
                       Toast.makeText(MainActivity.this,"Password Mismatch",Toast.LENGTH_LONG).show();
                   }

               }else{
                   mProgress.hide();
                   Toast.makeText(MainActivity.this,"Enter Password Correctly ",Toast.LENGTH_LONG).show();
               }
               }
        });
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
               startActivity(loginIntent);
            }
        });
    }
    private void sendVerificationEmail() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(MainActivity.this,"Verification link has been sent",Toast.LENGTH_LONG).show();

                    Intent statusIntent = new Intent(MainActivity.this, LoginActivity.class);

                    startActivity(statusIntent);
                    finish();

                    mAuth.signOut();
                }else{
                    mProgress.dismiss();
                    Toast.makeText(MainActivity.this,"Error Occurred. Please try Again :)",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    
    
    private void registerUser(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String uid = mAuth.getCurrentUser().getUid();

                    mRootRef.child(uid).child("name").setValue(name);
                    sendVerificationEmail();
                }else{
                    mProgress.hide();
                    Toast.makeText(MainActivity.this,"Error Occurred. Please try Again :)",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

   

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }
    }
}
