package com.shebatech.rokibulhasan.ecommerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.shebatech.rokibulhasan.ecommerceapp.Model.Users;
import com.shebatech.rokibulhasan.ecommerceapp.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
private EditText InputPhoneNumber,InputPassword;
private Button LoginButton;
    private ProgressDialog loadindBar;
    private TextView AdminLink,NotAdminLink;
    private String parentDBName="Users";
    private CheckBox chkBoxRememberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton=findViewById(R.id.login_btn);
        AdminLink=findViewById(R.id.admin_panel_link);
        NotAdminLink=findViewById(R.id.not_admin_panel_link);
        InputPhoneNumber=findViewById(R.id.login_phone_number_input);
        InputPassword=findViewById(R.id.login_password_input);
        chkBoxRememberMe= (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);
        loadindBar = new ProgressDialog(this);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
               NotAdminLink.setVisibility(View.VISIBLE);
               parentDBName="Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDBName="Users";
            }
        });

    }

    private void LoginUser() {

        String phone=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();

    if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"please write your phone number.. ..",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"please write your password.. ..",Toast.LENGTH_SHORT).show();
        }
        else {
            loadindBar.setTitle("Login Account");
            loadindBar.setMessage("Please wait, while we are checking the credentials.");
            loadindBar.setCanceledOnTouchOutside(false);
            loadindBar.show();

            AllowAccessToAccount(phone,password);
        }

    }

    private void AllowAccessToAccount(final String phone, final String password) {
        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef=FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)){
                            if (parentDBName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadindBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,AdminAddCategoryActivity.class);
                                startActivity(intent);
                            }else if (parentDBName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadindBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser=usersData;
                                startActivity(intent);
                            }
                        }
                        else{
                            loadindBar.dismiss();
                            Toast.makeText(LoginActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Account with this "+phone+" number do not exists", Toast.LENGTH_SHORT).show();
                    loadindBar.dismiss();
                    Toast.makeText(LoginActivity.this, "you need to create a new Account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
