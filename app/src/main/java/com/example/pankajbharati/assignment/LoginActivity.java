package com.example.pankajbharati.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.pankajbharati.assignment.*;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEmail, mPassword;
    private Button signInBtn;
    private TextView signUpTV;

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

        signInBtn = findViewById(R.id.login_btn);
        signUpTV = findViewById(R.id.create_account);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please Wait");


        mAuth = FirebaseAuth.getInstance();


       
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();

                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                    signInUser(email, pass); //calling method to login user
                } else {
                    mProgress.hide();
                    Toast.makeText(LoginActivity.this, "Please Check your Details", Toast.LENGTH_LONG).show();
                }
            }
        });

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
    private void signInUser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user.isEmailVerified()) {

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

    public static class AddProductActivity extends AppCompatActivity {

        private Toolbar mToolbar;
        private ImageView mProductImage;
        private EditText mProduct_id;
        private EditText mProduct_mrp;
        private EditText mProduct_sell_price;
        private EditText mProduct_name;
        private EditText mProduct_quantity;
        private EditText mProductDescription;
        private Button mSave_product;
        private Button mEdit_product;
        private Button mUpdate;


        private FirebaseAuth mAuth;
        private DatabaseReference mReference;
        private StorageReference storageReference;
        private ProgressDialog progressDialog;

        private final static int GALLARY_PIC = 1;
        private final static String TAG = "Storage Permission";

        String imge = "default";
        String mUuid;
        private Bundle bundle;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_product);

            mToolbar = findViewById(R.id.addProduct_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Add Product");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            initView();  // initialize Views


            // firebase initialization
            mAuth = FirebaseAuth.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference().child("images");



            progressDialog = new ProgressDialog(this);


            bundle = getIntent().getExtras();

            if (!bundle.isEmpty()) {

                mProduct_id.setText(bundle.getString("id"));
                mProduct_id.setEnabled(false);

                mProduct_name.setText(bundle.getString("p_name"));
                mProduct_name.setEnabled(false);

                mProduct_quantity.setText(bundle.getString("available"));
                mProduct_quantity.setEnabled(false);

                mProductDescription.setText(bundle.getString("desc"));
                mProductDescription.setEnabled(false);

                mProduct_mrp.setText(bundle.getString("mrp"));
                mProduct_mrp.setEnabled(false);
                mProduct_sell_price.setText(bundle.getString("sell_price"));
                mProduct_sell_price.setEnabled(false);

                mSave_product.setVisibility(View.INVISIBLE);
                mEdit_product.setVisibility(View.VISIBLE);


            }

            //click listener of save button
            mSave_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = mProduct_id.getText().toString();
                    String mrp = mProduct_mrp.getText().toString();
                    String sell_price = mProduct_sell_price.getText().toString();
                    String name = mProduct_name.getText().toString();
                    String quantity = mProduct_quantity.getText().toString();
                    String description = mProductDescription.getText().toString();

                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(mrp) && !TextUtils.isEmpty(sell_price)
                            && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(quantity) && !TextUtils.isEmpty(description)) {

                        progressDialog.setMessage("Saving");
                        progressDialog.show();
                        saveData(imge, id, mrp, sell_price, name, quantity, description);
                    } else {
                        Toast.makeText(AddProductActivity.this, "Every field should be filled", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            // listener to upload image
            mProductImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isStoragePermissionGranted()) {

                        loadImage();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Please give permission to use Storage", Toast.LENGTH_SHORT).show();
                    }


                }
            });


            //listener to Edit product details
            mEdit_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mEdit_product.setVisibility(View.INVISIBLE);
                    mUpdate.setVisibility(View.VISIBLE);

                   // mProduct_id.setEnabled(true);
                    mProduct_mrp.setEnabled(true);
                    mProduct_name.setEnabled(true);
                    mProduct_quantity.setEnabled(true);
                    mProduct_sell_price.setEnabled(true);
                    mProductDescription.setEnabled(true);


                }
            });

            //listener to update image
            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String id = mProduct_id.getText().toString();
                    String mrp = mProduct_mrp.getText().toString();
                    String sell_price = mProduct_sell_price.getText().toString();
                    String name = mProduct_name.getText().toString();
                    String quantity = mProduct_quantity.getText().toString();
                    String description = mProductDescription.getText().toString();

                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(mrp) && !TextUtils.isEmpty(sell_price)
                            && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(quantity) && !TextUtils.isEmpty(description)) {

                        progressDialog.setMessage("Saving");
                        progressDialog.show();
                        UpdateData(imge, id, mrp, sell_price, name, quantity, description);
                    }

                }
            });

        }




        @Override
        protected void onStart() {
            super.onStart();

            FirebaseUser current_user = mAuth.getCurrentUser();

            mUuid = current_user.getUid();
        }


        //method to initialize view
        private void initView() {

            mProductImage = findViewById(R.id.product_image);
            mProduct_id = findViewById(R.id.product_id);
            mProduct_mrp = findViewById(R.id.product_mrp);
            mProduct_sell_price = findViewById(R.id.product_sell_price);
            mProduct_name = findViewById(R.id.product_name);
            mProduct_quantity = findViewById(R.id.product_quantity);
            mProductDescription = findViewById(R.id.product_description);
            mSave_product = findViewById(R.id.save_btn);
            mEdit_product = findViewById(R.id.edit_btn);
            mUpdate = findViewById(R.id.update_btn);
        }

        // Save data to firebase realtime database
        private void saveData(String imge, final String id, String mrp, String sell_price, String name, String quantity, String description) {

            mReference = FirebaseDatabase.getInstance().getReference().child("Products").child(mUuid).push();

            //String device_token = FirebaseInstanceId.getInstance().getToken();
            HashMap<String, String> productMap = new HashMap<>();
            productMap.put("image", imge);
            productMap.put("id", id);
            productMap.put("mrp", mrp);
            productMap.put("selling_price", sell_price);
            productMap.put("name", name);
            productMap.put("quantity", quantity);
            productMap.put("description", description);


            mReference.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        clearEditText();

                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "product saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.hide();
                        Toast.makeText(AddProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        private void clearEditText() {
            mProduct_id.setText("");
            mProduct_mrp.setText("");
            mProduct_sell_price.setText("");
            mProduct_name.setText("");
            mProduct_quantity.setText("");
            mProductDescription.setText("");


        }


        //method to update product details
        private void UpdateData(String imge, final String id, String mrp, String sell_price, String name, String quantity, String description) {



            mReference = FirebaseDatabase.getInstance().getReference().child("Products").child(mUuid);
            //String device_token = FirebaseInstanceId.getInstance().getToken();
            final Map<String, String> productMap = new HashMap<>();
            productMap.put("image", imge);
            productMap.put("id", id);
            productMap.put("mrp", mrp);
            productMap.put("selling_price", sell_price);
            productMap.put("name", name);
            productMap.put("quantity", quantity);
            productMap.put("description", description);

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        String iid = ds.getValue(Product.class).getId();

                        if(iid.equals(id)){

                            mReference.child(ds.getKey()).setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(AddProductActivity.this, "Upadted", Toast.LENGTH_SHORT).show();
                                        progressDialog.hide();
                                    }else{

                                        Toast.makeText(AddProductActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                        progressDialog.hide();
                                    }
                                }
                            });
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        private void loadImage() {

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLARY_PIC);

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    //        if (requestCode == GALLARY_PIC && resultCode == RESULT_OK) {
    //            Uri imageUri = data.getData();


            if (requestCode == GALLARY_PIC && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                mProductImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                StorageReference filepath = storageReference.child(mUuid + ".jpg");

                filepath.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            final String download_uri = task.getResult().getDownloadUrl().toString();

                            imge = download_uri;

                        }
                    }
                });

            }
        }


        public boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission is granted");
                    return true;
                } else {

                    Log.v(TAG, "Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v(TAG, "Permission is granted");
                return true;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("Storage Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
            }
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
             super.onOptionsItemSelected(item);

             onBackPressed();
             return true;
        }
    }
}
