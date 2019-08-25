package com.shebatech.rokibulhasan.ecommerceapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shebatech.rokibulhasan.ecommerceapp.Model.Product;
import com.shebatech.rokibulhasan.ecommerceapp.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
private Button addToCartButton;
private ImageView productImage;
private ElegantNumberButton numberButton;
private TextView productName,productDescription,productPrice;
    private String productID = ""  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID=getIntent().getStringExtra("pid");
        addToCartButton=findViewById(R.id.add_product_to_cart);
        numberButton=findViewById(R.id.number_button);
        productImage=findViewById(R.id.product_image_details);
        productName=findViewById(R.id.product_name_details);
        productDescription=findViewById(R.id.product_description_details);
        productPrice=findViewById(R.id.product_price_details);

        getproductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCartlist();
            }
        });
    }

    private void addingToCartlist()
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd ,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentDate.format(calForDate.getTime());

     final  DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

       final HashMap<String,Object> cartmap=new HashMap<>();
       cartmap.put("pid",productID);
       cartmap.put("pname",productName.getText().toString());
       cartmap.put("price",productPrice.getText().toString());
       cartmap.put("date",saveCurrentDate);
       cartmap.put("time",saveCurrentTime);
       cartmap.put("quantity",numberButton.getNumber());
       cartmap.put("discount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful())
{
    Toast.makeText(ProductDetailsActivity.this, "Added to cart List...", Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
    startActivity(intent);
}
                                }
                            });
                }
            }
        });
    }

    private void getproductDetails(String productID)
    {
        DatabaseReference productRef=FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Product product=dataSnapshot.getValue(Product.class);
                    productName.setText(product.getPname());
                    productPrice.setText(product.getPrice());
                    productDescription.setText(product.getDescription());

                    Picasso.get().load(product.getImage()).into(productImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
