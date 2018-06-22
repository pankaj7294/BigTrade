package com.example.pankajbharati.assignment.bigTrade;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pankajbharati.assignment.LoginActivity;
import com.example.pankajbharati.assignment.Product;
import com.example.pankajbharati.assignment.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.pankajbharati.assignment.bigTrade.R;

import java.util.ArrayList;
import java.util.List;



public class ProductFragment extends Fragment {

    private TextView addProduct;
    private TextView emptyText;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private LinearLayoutManager linearLayout;
    private ProductAdapter productAdapter;

    private DatabaseReference mReference;

    private String mUuid;

    private ProgressDialog mProgress;

    public ProductFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_product, container, false);

        mProgress = new ProgressDialog(getActivity());

        emptyText  = view.findViewById(R.id.emptyText);
        addProduct = view.findViewById(R.id.add_btn);
        recyclerView = view.findViewById(R.id.recyclerView);

        productList = new ArrayList<>();

        linearLayout = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayout);

        productAdapter = new ProductAdapter(getContext(),productList);
        recyclerView.setAdapter(productAdapter);


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        mUuid = current_user.getUid();

        mReference = FirebaseDatabase.getInstance().getReference().child("Products").child(mUuid);

        Log.i("ChildId",""+mReference);

        loadProduct();



        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getContext(), LoginActivity.AddProductActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadProduct() {

        mProgress.setMessage("Loading..");
        mProgress.show();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    Product product = ds.getValue(Product.class);
                    productList.add(product);
                    productAdapter.notifyDataSetChanged();
                    Log.i("Data","data "+product);

                }


                if(!productList.isEmpty()){

                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.INVISIBLE);

                }else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProgress.hide();
    }

}
