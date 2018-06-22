package com.gupta.ram.assignment;

import android.content.Intent;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gupta.ram.assignment.fragment.FragmentAdapter;

public class WelcomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout mTablayout;
    private ViewPager mViewpager;
    private TextView mName;
    String status;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRootRef;

    FragmentAdapter fragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("BigTrade");

        mViewpager = findViewById(R.id.viewpager);
        mTablayout = findViewById(R.id.main_tab);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(fragmentAdapter);
        mTablayout.setupWithViewPager(mViewpager);


        mName = findViewById(R.id.user_name);

        //-------Firebase variables initialization;
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);


        //-----fetching user name from firebase realtime database
        mRootRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
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
           // sendTostart();
        }
        return true;
    }
}
