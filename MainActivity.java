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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mName, mEmail, mPassword, mConfirm_pass;
    private TextView mLoginText;
    private Button mRegBtn;
    private ProgressDialog mProgress;

    private FirebaseUser mCurrent_user;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("BigTrade");

        mName = findViewById(R.id.user_name_id);
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.password_id);
        mConfirm_pass = findViewById(R.id.confirm_pass_id);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");


        mRegBtn = findViewById(R.id.reg_btn_id);
        mLoginText = findViewById(R.id.login_text);


        //----------Firebase initializations -------------
        mAuth = FirebaseAuth.getInstance();
       // mCurrent_user = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //-----------Click Listener of Register Button -----------------//
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String name = mName.getText().toString().trim();
               String email = mEmail.getText().toString().trim();
               String password = mPassword.getText().toString();
               String confirm_pass = mConfirm_pass.getText().toString();
               mProgress.show();

               //--------checking for valid details-----------
               if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                       && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_pass)){


                   if(password.equals(confirm_pass)){

                       registerUser( name, email,password);   //method to register user if details are correct
                   }else{
                       mProgress.hide();
                       Toast.makeText(MainActivity.this,"Password didn't matched",Toast.LENGTH_LONG).show();
                   }

               }else{
                   mProgress.hide();
                   Toast.makeText(MainActivity.this,"Please give valid inputs ",Toast.LENGTH_LONG).show();
               }


            }
        });


        //-----------Click Listener if User already have account -------
        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
               startActivity(loginIntent);

            }
        });


    }

    //------------------Register method --------------
    private void registerUser(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String uid = mAuth.getCurrentUser().getUid();

                    mRootRef.child(uid).child("name").setValue(name);  //saving user name into realtime firebase database
                    sendVerificationEmail();  //calling method to send verification link to users email id

                }else{
                    mProgress.hide();
                    Toast.makeText(MainActivity.this,"Error Occurred. Please try Again :)",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    //------------------method to send verification mail to user email id ----------
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

    @Override
    protected void onStart() {
        super.onStart();
        //--------if user already logged in then send directly to welcome page
        if(mAuth.getCurrentUser() != null){
            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(welcomeIntent);
            finish();
        }
    }
}
