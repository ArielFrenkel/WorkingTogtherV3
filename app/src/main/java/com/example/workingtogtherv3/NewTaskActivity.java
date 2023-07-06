package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {
        Button BtAddTask;
        EditText EtTaskName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        BtAddTask = findViewById(R.id.BtAddTask);
        EtTaskName = findViewById(R.id.EtTaskName);
        BtAddTask.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == BtAddTask) {
            if (TextUtils.isEmpty(EtTaskName.getText().toString()))// check if user entered a name
            {
                Toast.makeText(this, "add name", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> newTask = new HashMap<>();

                db.collection("Tasks")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean exist = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (EtTaskName.getText().toString().equals(document.get("Task name").toString())) { // check if name exist in Tasks in database
                                            Toast.makeText(NewTaskActivity.this, "name exist", Toast.LENGTH_SHORT).show();
                                            exist = true;
                                            break;
                                        }
                                    }
                                    if (!exist) {
                                        newTask.put("Task name", EtTaskName.getText().toString());
                                        newTask.put("Task status",false);
                                        db.collection("Tasks").document(EtTaskName.getText().toString())
                                                .set(newTask)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //  Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        Toast.makeText(NewTaskActivity.this, "Firestore success", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(NewTaskActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Log.w(TAG, "Error adding document", e);
                                                        Toast.makeText(NewTaskActivity.this, "Firestore fail", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
                // startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        }
    }
}