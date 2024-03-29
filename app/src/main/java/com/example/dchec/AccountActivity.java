package com.example.dchec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private ImageView arrowBack ;

    private EditText userName , userNickName , userEmail , userPassword , userPhoneNumber , userLocalisation, associationId ;
    private TextInputLayout accountAssocationId , accountUserNickName;
    private Button saveBtn ;

    private FirebaseAuth mAuth;
    private DatabaseReference accountUserRef , accountAssociationRef;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        MainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MainActivity.isSimpleUser = MainActivity.sharedPreferences.getBoolean("isSimpleUser" , true);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        accountUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        accountAssociationRef = FirebaseDatabase.getInstance().getReference().child("association").child(currentUserId);

        String email = mAuth.getCurrentUser().getEmail();

        userName = findViewById(R.id.account_user_name);
        userNickName = findViewById(R.id.account_user_nickname);
        userEmail = findViewById(R.id.account_user_email);
        userPassword = findViewById(R.id.account_user_password);
        userPhoneNumber = findViewById(R.id.account_user_phone_number);
        userLocalisation = findViewById(R.id.account_user_localisation);
        associationId = findViewById(R.id.account_association_id);

        accountAssocationId = findViewById(R.id.account_association_id_input);
        accountUserNickName = findViewById(R.id.account_user_nick_name_input);

        if (MainActivity.isSimpleUser){
            accountUserNickName.setVisibility(View.VISIBLE);
            accountAssocationId.setVisibility(View.GONE);
        }else{
            accountAssocationId.setVisibility(View.VISIBLE);
            accountUserNickName.setVisibility(View.GONE);
        }

        userEmail.setText(email);

        saveBtn = findViewById(R.id.account_save_button);

        arrowBack = findViewById(R.id.account_back_arrow);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (MainActivity.isSimpleUser){
            accountUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String myName = snapshot.child("userName").getValue().toString();
                    String myNickName = snapshot.child("nickName").getValue().toString();
                    String myPhoneNumber = snapshot.child("phoneNumber").getValue().toString();
                    String myLocalisation = snapshot.child("localisation").getValue().toString();
                    String myPassword = snapshot.child("password").getValue().toString();


                    userName.setText(myName);
                    userNickName.setText(myNickName);
                    userPhoneNumber.setText(myPhoneNumber);
                    userLocalisation.setText(myLocalisation);
                    userPassword.setText(myPassword);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            accountAssociationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String myName = snapshot.child("userName").getValue().toString();
                    String myId = snapshot.child("id").getValue().toString();
                    String myPhoneNumber = snapshot.child("phoneNumber").getValue().toString();
                    String myLocalisation = snapshot.child("localisation").getValue().toString();
                    String myPassword = snapshot.child("password").getValue().toString();


                    userName.setText(myName);
                    associationId.setText(myId);
                    userPhoneNumber.setText(myPhoneNumber);
                    userLocalisation.setText(myLocalisation);
                    userPassword.setText(myPassword);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAccountInfo();
            }
        });


    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String usernickname = userNickName.getText().toString();
        String userphonenumber = userPhoneNumber.getText().toString();
        String userlocalisation = userLocalisation.getText().toString();
        String userpassword = userPassword.getText().toString();
        String assocationid = associationId.getText().toString();


        if (MainActivity.isSimpleUser){

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(AccountActivity.this, "svp verfier votre nom", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(usernickname)){
                Toast.makeText(AccountActivity.this, "svp verfier votre prénom", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(userphonenumber)){
                Toast.makeText(AccountActivity.this, "svp verfier votre numéro", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(userpassword)){
                Toast.makeText(AccountActivity.this, "svp verfier votre mot de passe", Toast.LENGTH_SHORT).show();
            }else {
                UpdateUserInfo(username , usernickname , userphonenumber , userlocalisation , userpassword);
            }

        }else {
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(AccountActivity.this, "svp verfier votre nom", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(assocationid)){
                Toast.makeText(AccountActivity.this, "svp verfier votre id", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(userphonenumber)){
                Toast.makeText(AccountActivity.this, "svp verfier votre numéro", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(userpassword)){
                Toast.makeText(AccountActivity.this, "svp verfier votre mot de passe", Toast.LENGTH_SHORT).show();
            }else {
                UpdateAssociationInfo(username , assocationid , userphonenumber , userlocalisation , userpassword);
            }
        }
    }

    private void UpdateAssociationInfo(String username, String assocationid, String userphonenumber, String userlocalisation, String userpassword) {
        HashMap userMap = new HashMap();
        userMap.put("userName",username);
        userMap.put("id",assocationid);
        userMap.put("phoneNumber",userphonenumber);
        userMap.put("localisation" , userlocalisation);
        userMap.put("password" , userpassword);



        accountAssociationRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    sendUserToHomeActivity();
                    Toast.makeText(AccountActivity.this, "vous avez changer votre info ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AccountActivity.this, "eurro occured : "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }




    private void UpdateUserInfo(String username, String usernickname, String userphonenumber, String userlocalisation, String userpassword) {

        HashMap userMap = new HashMap();
        userMap.put("userName",username);
        userMap.put("nickName",usernickname);
        userMap.put("phoneNumber",userphonenumber);
        userMap.put("localisation" , userlocalisation);
        userMap.put("password" , userpassword);

        accountUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    sendUserToHomeActivity();
                    Toast.makeText(AccountActivity.this, "vous avez changer votre info ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AccountActivity.this, "eurro occured : "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent(AccountActivity.this , HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}