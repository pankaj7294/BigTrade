package com.gupta.ram.assignment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by RAMJEE on 07-06-2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<Product> productList;
    Context context;

    private DatabaseReference mReference;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final Product product = productList.get(position);
        Log.i("product","data "+product);
        holder.productName.setText(product.getName());
        holder.mrp.setText("MRP: "+product.getMrp());
        holder.sell_price.setText("Selling Price :"+product.getSelling_price());
        holder.stock.setText("Available: "+product.getQuantity());
        holder.description.setText(product.getDescription());

        if(product.getImage() != "default"){

            Picasso.get().load(product.getImage()).placeholder(R.drawable.image).into(holder.image);
        }



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,AddProductActivity.class);
                Bundle bundle = new Bundle();
                //bundle.putAll("Bundle",produ);
                bundle.putString("id",productList.get(position).getId());
                bundle.putString("p_name",productList.get(position).getName());
                bundle.putString("mrp",productList.get(position).getMrp());
                bundle.putString("sell_price",productList.get(position).getSelling_price());
                bundle.putString("available",productList.get(position).getQuantity());
                bundle.putString("desc", productList.get(position).getDescription());

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, mrp,sell_price, description, stock;

        ImageView image;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            mrp = itemView.findViewById(R.id.mrp);
            sell_price = itemView.findViewById(R.id.sell_price);
            description = itemView.findViewById(R.id.description);
            stock = itemView.findViewById(R.id.stock);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.productCard);


        }
    }
}
