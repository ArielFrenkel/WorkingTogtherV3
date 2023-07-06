package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button BtSginUp, BtCheckIn;
    EditText EtFirstName, EtLastName,EtPhone ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EtPhone = findViewById(R.id.EtPhone);
        EtFirstName = findViewById(R.id.EtFirstName);
        EtLastName = findViewById(R.id.EtLastName);
        BtSginUp = findViewById(R.id.BtSginUp);
        BtCheckIn = findViewById(R.id.BtCheckIn);

        BtSginUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EtFirstName.getText().toString().matches("") ||
                        EtLastName.getText().toString().matches("") ||
                        EtPhone.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "please fill all rows", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();

                    db.collection("Users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean exist = false;
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            if (EtPhone.getText().toString().equals(document.get("Phone number").toString())) {
                                                Toast.makeText(MainActivity.this, "Number exist", Toast.LENGTH_SHORT).show();
                                                exist = true;
                                                break;
                                            }
                                        }
                                        if (!exist) {
                                            user.put("Phone number", EtPhone.getText().toString());
                                            user.put("First name", EtFirstName.getText().toString());
                                            user.put("Last name", EtLastName.getText().toString());
                                            db.collection("Users").document(EtPhone.getText().toString())
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //  Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            Toast.makeText(MainActivity.this, "Firestore success", Toast.LENGTH_SHORT).show();
                                                            SharedPreferences sharedPreferences = getSharedPreferences("user data", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("phone", EtPhone.getText().toString());
                                                            editor.putString("first name", EtFirstName.getText().toString());
                                                            editor.putString("last name",EtLastName.getText().toString());
                                                            editor.apply();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            //Log.w(TAG, "Error adding document", e);
                                                            Toast.makeText(MainActivity.this, "Firestore fail", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        }
                                    } else {
                                        // Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        });
        BtCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EtFirstName.getText().toString().matches("") ||
                        EtLastName.getText().toString().matches("") ||
                        EtPhone.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "please fill all rows", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();

                    db.collection("Users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean userExist = false;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (EtPhone.getText().toString().equals(document.get("Phone number").toString())
                                            && EtFirstName.getText().toString().equals(document.get("First name").toString())
                                            && EtLastName.getText().toString().equals(document.get("Last name").toString()))
                                            {
                                                SharedPreferences sharedPreferences = getSharedPreferences("user data", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("phone", document.get("Phone number").toString());
                                                editor.putString("first name", document.get("First name").toString());
                                                editor.putString("last name", document.get("Last name").toString());
                                                editor.apply();
                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                userExist = true;
                                                break;
                                            }
                                        }
                                        if (!userExist)
                                        {
                                            Toast.makeText(MainActivity.this, "user does not exist", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }
@Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sh = getSharedPreferences("user data", MODE_PRIVATE);
        if (!sh.getString("phone","").equals("") && !sh.getString("first name","").equals("")
                && !sh.getString("last name","").equals(""))
        {
            EtFirstName.setText(sh.getString("first name",""));
            EtLastName.setText(sh.getString("last name",""));
            EtPhone.setText(sh.getString("phone",""));
        }
    }
}


