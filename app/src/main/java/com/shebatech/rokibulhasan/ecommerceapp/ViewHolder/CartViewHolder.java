package com.shebatech.rokibulhasan.ecommerceapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shebatech.rokibulhasan.ecommerceapp.Interface.ItemClickListener;
import com.shebatech.rokibulhasan.ecommerceapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductPrice,txtProductQuantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);
        txtProductName=(TextView) itemView.findViewById(R.id.cart_product_name);
        txtProductQuantity=(TextView) itemView.findViewById(R.id.cart_product_quantiry);
        txtProductPrice=(TextView) itemView.findViewById(R.id.cart_product_price);
    }

    @Override
    public void onClick(View view)
    {
itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
