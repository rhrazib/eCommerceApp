package com.shebatech.rokibulhasan.ecommerceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shebatech.rokibulhasan.ecommerceapp.Model.Cart;
import com.shebatech.rokibulhasan.ecommerceapp.Prevalent.Prevalent;
import com.shebatech.rokibulhasan.ecommerceapp.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private Button nextProcessButton;
private TextView txtTotalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessButton=findViewById(R.id.next_process_button);
        txtTotalButton=findViewById(R.id.total_price);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options
                =new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantity.setText("Quantity ="+model.getQuantity());
                holder.txtProductPrice.setText("Price ="+model.getPrice());
                holder.txtProductName.setText(model.getPname());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Edit","Remove"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                     if (i==0)
                                     {
                                         Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                         intent.putExtra("pid",model.getPid());
                                         startActivity(intent);
                                     }
                                     if (i==1)
                                     {
                                         cartListRef.child("User View")
                                                 .child(Prevalent.currentOnlineUser.getPhone())
                                                 .child("Products")
                                                 .child(model.getPid())
                                                 .removeValue()
                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {
                                                       if (task.isSuccessful())
                                                       {
                                                           Toast.makeText(CartActivity.this, "Item removed Successfully.", Toast.LENGTH_SHORT).show();
                                                           Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                                                           startActivity(intent);
                                                       }
                                                     }
                                                 });
                                     }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
