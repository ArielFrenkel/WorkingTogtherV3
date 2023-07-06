package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChangeTaskStatusActivity extends AppCompatActivity {
    ListView lvTasks;
    static String manager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_task_status);

        lvTasks = findViewById(R.id.lvTasks);
        manager = getSharedPreferences("user data",MODE_PRIVATE).getString("phone","");


        FirebaseFirestore db = FirebaseFirestore.getInstance();// initialize firestore instance

        db.collection("Tasks")
                .whereEqualTo("Manager of Task",manager)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Toast.makeText(ChangeTaskStatusActivity.this, "error showing tasks", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}