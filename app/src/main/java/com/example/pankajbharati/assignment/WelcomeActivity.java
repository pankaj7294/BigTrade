package com.example.pankajbharati.assignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.pankajbharati.assignment.R;
import com.example.pankajbharati.assignment.bigTrade.FragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView mName;
    String status;

    private FirebaseAuth Auth;
    private FirebaseUser mUser;
    private DatabaseReference rootRef;

    FragmentAdapter fragmentAdapter;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("BigTrade");

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.main_tab);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        mName = findViewById(R.id.user_name);

        Auth = FirebaseAuth.getInstance();
        String userID = Auth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        rootRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                mName.setText("Hello " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.logoutBtn)
        {
            FirebaseAuth.getInstance().signOut();
            Intent mainActivity = new Intent(WelcomeActivity.this,MainActivity.class);
            startActivity(mainActivity);
        }
        return true;
    }
}
