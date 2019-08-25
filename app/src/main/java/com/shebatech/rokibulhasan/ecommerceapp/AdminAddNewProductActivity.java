package com.shebatech.rokibulhasan.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
private String categoryName,description,price,productname,saveCurrentDate,saveCurrentTime;
private Button AddNewProductButton;
private ImageView InputProductImage;
private EditText InputProductName,InputProductDescription,InputproductPrice;
private static final int Gallerypick=1;
private Uri ImageUri;
private String productRandomKey, DownloadImageUrl;
private StorageReference ProductImageRef;
private DatabaseReference ProductRef;

    private ProgressDialog loadindBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        InputProductImage=findViewById(R.id.select_product_images);
        AddNewProductButton=findViewById(R.id.add_new_product);
        InputProductName=findViewById(R.id.product_name);
        InputProductDescription=findViewById(R.id.product_description);
        InputproductPrice=findViewById(R.id.product_price);
        loadindBar = new ProgressDialog(this);

        categoryName=getIntent().getExtras().get("category").toString();
        ProductImageRef=FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef=FirebaseDatabase.getInstance().getReference().child("Products");
       // Toast.makeText(this,categoryName,Toast.LENGTH_SHORT).show();



        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }

    private void ValidateProductData()
    {
        description=InputProductDescription.getText().toString();
        price=InputproductPrice.getText().toString();
        productname=InputProductName.getText().toString();

        if (ImageUri ==null)
        {
                Toast.makeText(this,"Product image is mandatory...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this,"Please write product description...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this,"Please write product pricr...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(productname))
        {
            Toast.makeText(this,"Please write product name...",Toast.LENGTH_SHORT).show();
        }else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadindBar.setTitle("Add New Product..");
        loadindBar.setMessage("Dear Admin,Please wait while we are adding the new product");
        loadindBar.setCanceledOnTouchOutside(false);

        loadindBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        productRandomKey=saveCurrentDate +saveCurrentTime;

        final StorageReference filePath=ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey +".jpg");

        final UploadTask uploadTask=filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                loadindBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this,"Product Image Uploaded Successfully...",Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        DownloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            DownloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"got the Product images url successfully...",Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });

            }
        });
    }

    private void saveProductInfoToDatabase()
    {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",DownloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("pname",productname);

        ProductRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent=new Intent(AdminAddNewProductActivity.this,AdminAddCategoryActivity.class);
                            startActivity(intent);
                            loadindBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,"Product is Added Successfully..",Toast.LENGTH_SHORT).show();

                        }else
                        {
                            loadindBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    private void openGallery()
    {
        Intent gallryIntent=new Intent();
        gallryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallryIntent.setType("image/*");
      startActivityForResult(gallryIntent,Gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallerypick && resultCode==RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);

        }
    }
}
