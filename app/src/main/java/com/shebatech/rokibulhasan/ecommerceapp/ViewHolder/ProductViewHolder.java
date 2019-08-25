package com.shebatech.rokibulhasan.ecommerceapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shebatech.rokibulhasan.ecommerceapp.Interface.ItemClickListener;
import com.shebatech.rokibulhasan.ecommerceapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductdescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;
    public ProductViewHolder(View itemView)
    {
        super(itemView);
        imageView=(ImageView)itemView.findViewById(R.id.product_image);
        txtProductName=(TextView) itemView.findViewById(R.id.product_name);
        txtProductdescription=(TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice=(TextView) itemView.findViewById(R.id.product_price);

    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view,getAdapterPosition(),false);
    }
}
