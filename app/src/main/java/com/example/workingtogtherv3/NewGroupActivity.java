package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGroupActivity extends AppCompatActivity {
    Button btCreateGroup;
    EditText EtGroupName;
    ListView lvListUsers;
    ArrayAdapter<String> adapter;
    List<String> temp;
    FirebaseFirestore db;
    String[] usersArray;
    boolean[] checked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        btCreateGroup = findViewById(R.id.btCreateGroup);
        EtGroupName = findViewById(R.id.EtGroupName);
        lvListUsers = findViewById(R.id.lvListUsers);
        temp = new ArrayList<String>();
        db = FirebaseFirestore.getInstance();

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                usersArray =  new String[temp.size()];
                checked = new boolean[temp.size()];
                for (int i = 0 ; i < usersArray.length;i++)
                {
                    usersArray[i] = temp.get(i);
                    checked[i] = false;
                }
                setList(usersArray);
            }
        });

        btCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EtGroupName.getText().toString() == "")
                {
                    Toast.makeText(NewGroupActivity.this, "give group a name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> group = new HashMap<>();

                    db.collection("Groups")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean exist = false;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Log.d(TAG, document.getId() + " => " + document.getData());
                                            if (EtGroupName.getText().toString().equals(document.get("group name").toString()))
                                            {
                                                Toast.makeText(NewGroupActivity.this, "Group exist", Toast.LENGTH_SHORT).show();
                                                exist = true;
                                                break;
                                            }
                                        }
                                        boolean hasUsers = false;
                                        for (boolean value : checked) { // check if group have users
                                            if (value) {
                                                hasUsers = true;
                                            }
                                        }
                                        if (!exist && !EtGroupName.getText().toString().equals("") && hasUsers)
                                        {
                                            group.put("group name",EtGroupName.getText().toString());
                                            SharedPreferences sh = getSharedPreferences("user data", MODE_PRIVATE);
                                            group.put("manager",sh.getString("phone",""));
                                            int counter = 0;
                                            for (int i = 0 ; i < usersArray.length ; i++)
                                            {
                                                if(checked[i] == true) {
                                                    group.put("user " + new Integer(counter), usersArray[i].toString());
                                                    counter++;
                                                }
                                            }
                                            db.collection("Groups").document(EtGroupName.getText().toString())
                                                    .set(group)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //  Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                            Toast.makeText(NewGroupActivity.this, "Firestore success", Toast.LENGTH_SHORT).show();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(NewGroupActivity.this, "Firestore fail", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            Intent intent = new Intent(NewGroupActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        } else if (exist)
                                        {
                                            Toast.makeText(NewGroupActivity.this, "Group name is taken", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (EtGroupName.getText().toString().equals("")) {
                                            Toast.makeText(NewGroupActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (!hasUsers) {
                                            Toast.makeText(NewGroupActivity.this, "add users to group", Toast.LENGTH_SHORT).show();
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
    private void readData(FirestoreCallback firestoreCallback) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String str = document.get("First name").toString() + " " + document.get("Last name").toString();
                                temp.add(str);
                            }
                            firestoreCallback.onCallback(temp);
                        }
                        else {
                            Toast.makeText(NewGroupActivity.this, "names failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private interface FirestoreCallback {
        void onCallback(List<String> list);
    }
    private void setList(String[] strArr) {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,strArr);
        lvListUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvListUsers.setAdapter(adapter);
        lvListUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String items = (String) adapterView.getItemAtPosition(i);
                checked[i] = !checked[i];
            }
        });
    }
}