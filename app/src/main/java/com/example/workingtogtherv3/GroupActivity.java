package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lvList;
    Button btAddTask;
    ArrayAdapter<String> adapter;
    List<String> temp;
    FirebaseFirestore db;
    String[] usersArray ;
    String jason;
    TextView tvGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        tvGroupName = findViewById(R.id.tvGroupName);
        lvList = findViewById(R.id.lvList);
        btAddTask = findViewById(R.id.BtAddTask);

        btAddTask.setOnClickListener(this);
        Intent intent = getIntent();
        String str = intent.getStringExtra("group name");
        if (str.contains("["))
            str = str.substring(0,str.indexOf("[")-1); // fix naming groups !!!
        tvGroupName.setText(str);
        temp = new ArrayList<String>();
        db = FirebaseFirestore.getInstance();
        readData(new GroupActivity.FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                usersArray =  new String[0];
                for (String text : list)
                {
                    if (text.length() >= 2) {
                        String[] newarr = new String[usersArray.length + 1];
                        for (int i = 0; i < usersArray.length; i++)
                            newarr[i] = usersArray[i];
                        newarr[newarr.length - 1] = text.substring(3,text.indexOf(','));
                        usersArray = newarr;
                    }
                }
                setList(usersArray);
            }
        });
    }
    private void readData(GroupActivity.FirestoreCallback firestoreCallback) {
        temp.clear();
        DocumentReference docRef = db.collection("Groups").document(tvGroupName.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(GroupActivity.this, "show group" + document.getData(), Toast.LENGTH_SHORT).show();
                        jason = document.getData().toString();
                        temp = Arrays.asList(jason.split("user"));
                        firestoreCallback.onCallback(temp);
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(GroupActivity.this, "list failed", Toast.LENGTH_SHORT).show();
                    Exception e = task.getException();
                    String fixme = e.toString();
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view == btAddTask)
        {
            Intent intent = new Intent(GroupActivity.this,NewTaskActivity.class);
            startActivity(intent);
        }
    }
    private interface FirestoreCallback {
        void onCallback(List<String> list);
    }
    private void setList(String[] strArr) {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,strArr);

        lvList.setAdapter(adapter);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}