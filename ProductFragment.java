package com.gupta.ram.assignment.fragment;


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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gupta.ram.assignment.AddProductActivity;
import com.gupta.ram.assignment.Product;
import com.gupta.ram.assignment.ProductAdapter;
import com.gupta.ram.assignment.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class ProductFragment extends Fragment {

    private TextView mAddProduct;
    private TextView mEmtpyText;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private LinearLayoutManager mLinearLayout;
    private ProductAdapter mAdapter;

    private DatabaseReference mReference;

    private String mUuid;

    private ProgressDialog mProgress;

    public ProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product, container, false);

        mProgress = new ProgressDialog(getActivity());

        mEmtpyText  = view.findViewById(R.id.emptyText);
        mAddProduct = view.findViewById(R.id.add_btn);
        recyclerView = view.findViewById(R.id.recyclerView);

        productList = new ArrayList<>();

        mLinearLayout = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayout);

        mAdapter = new ProductAdapter(getContext(),productList);
        recyclerView.setAdapter(mAdapter);




        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        mUuid = current_user.getUid();

        mReference = FirebaseDatabase.getInstance().getReference().child("Products").child(mUuid);

        Log.i("ChildId",""+mReference);

        loadProduct();



        mAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getContext(), AddProductActivity.class);
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
                    mAdapter.notifyDataSetChanged();
                    Log.i("Data","data "+product);

                }


                if(!productList.isEmpty()){

                    recyclerView.setVisibility(View.VISIBLE);
                    mEmtpyText.setVisibility(View.INVISIBLE);

                }else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    mEmtpyText.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProgress.hide();
    }

}
