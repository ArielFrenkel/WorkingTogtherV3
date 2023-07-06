package com.example.workingtogtherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Button logOut, btGroups , btTasks
            ,btParticipants, btNewGroup, btNewTask;
    TextView tvListViewHeader;
    ArrayAdapter<String> adapter;
    ListView lvList;
    FirebaseFirestore db;
    List<String> temp,phones;
    LinearLayout headerTasks;
    String[] groupArray;
    String listType, chosenCell = "",previusType = "";
    static int pointer = -1;
    static ArrayList managerList;
    static String manager = null;
    static Boolean checkbox = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        manager = getSharedPreferences("user data",MODE_PRIVATE).getString("phone","");
        managerList = new ArrayList<>();

        logOut = findViewById(R.id.logOut);
        headerTasks = findViewById(R.id.headerTasks);
        btGroups = findViewById(R.id.btGroups);
        btTasks = findViewById(R.id.btTasks);
        tvListViewHeader = findViewById(R.id.tvListViewHeader);
        lvList = findViewById(R.id.lvList);
        btNewGroup = findViewById(R.id.btNewGroup);
        btParticipants = findViewById(R.id.btParticipants);
        btNewTask = findViewById(R.id.btNewTask);

        btParticipants.setOnClickListener(this);
        btTasks.setOnClickListener(this);
        btGroups.setOnClickListener(this);
        btNewGroup.setOnClickListener(this);
        btNewTask.setOnClickListener(this);

        temp = new ArrayList<String>();
        db = FirebaseFirestore.getInstance();
        listType = "Groups";
        checkbox = false;
        readData(new HomeActivity.FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                groupArray =  new String[list.size()];
                for (int i = 0 ; i < groupArray.length;i++)
                {
                    groupArray[i] = list.get(i);
                }
                setList(groupArray);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view == btGroups){
            checkbox = false;
            headerTasks.setVisibility(View.INVISIBLE);
            tvListViewHeader.setText("Groups");
            listType = "Groups";
            readData(new HomeActivity.FirestoreCallback() {
                @Override
                public void onCallback(List<String> list) {
                    groupArray =  new String[temp.size()];
                    for (int i = 0 ; i < groupArray.length;i++)
                    {
                        groupArray[i] = temp.get(i);
                    }
                    setList(groupArray);
                }
            });
        }
        if (view == btTasks)
        {
            checkbox = true;
            tvListViewHeader.setText("Tasks");
            headerTasks.setVisibility(View.VISIBLE);
            listType = "Tasks";
            previusType = "";
            readData(new HomeActivity.FirestoreCallback() {
                @Override
                public void onCallback(List<String> list) {
                    groupArray =  new String[temp.size()];
                    for (int i = 0 ; i < groupArray.length;i++)
                    {
                        groupArray[i] = temp.get(i);
                    }
                    setList(groupArray);
                }
            });
        }
        if (view == btParticipants)
        {
            headerTasks.setVisibility(View.INVISIBLE);
            checkbox = false;
            phones = new ArrayList<String>();
            tvListViewHeader.setText("Participants");
            listType = "Users";
            readData(new HomeActivity.FirestoreCallback() {
                @Override
                public void onCallback(List<String> list) {
                    groupArray =  new String[temp.size()];
                    for (int i = 0 ; i < groupArray.length;i++)
                    {
                        groupArray[i] = temp.get(i);
                    }
                    setList(groupArray);
                }
            });
        }
        if (view == btNewGroup)
        {
            Intent intent = new Intent(HomeActivity.this, NewGroupActivity.class);
            startActivity(intent);
        }
        if (view == btNewTask)
        {
            Intent intent = new Intent(HomeActivity.this,NewTaskActivity.class) ;
            intent.putExtra("group name","all users, DEV COMMAND");
            startActivity(intent);
        }
    }
    private void readData(HomeActivity.FirestoreCallback firestoreCallback) {
        temp.clear();
        managerList.clear();
        db.collection(listType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(listType.equals("Groups"))
                            {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   if (document.get("group name") != null) {
                                        String str = document.get("group name").toString();
                                        if (document.get("listTasks") != null)
                                            str += " " + document.get("listTasks").toString();
                                        temp.add(str);
                                        if (document.get("manager").toString().equals(manager))
                                        {
                                            managerList.add(temp.size()-1);
                                        }
                                    }
                                }
                            }
                            else if (listType.equals("Users")) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String str = document.get("First name").toString() + " " + document.get("Last name").toString();
                                    temp.add(str);
                                    String phoneNumber =  document.get("Phone number").toString();
                                    phones.add(phoneNumber);
                                }
                            }
                            else if (listType.equals("Tasks")) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String str = document.get("Task name").toString();
                                    String status;
                                    if ((Boolean) document.get("Task status"))
                                        status = "closed";
                                    else
                                        status = "open";
                                    str +=": " + status;
                                    temp.add(str);
                                    if (document.get("Manager of Task") != null) {
                                        if (document.get("Manager of Task").toString().equals(manager)) {
                                            managerList.add(temp.size() - 1);
                                        }
                                    }
                                }
                            }
                            firestoreCallback.onCallback(temp);
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "list failed", Toast.LENGTH_SHORT).show();
                            Exception e = task.getException();
                            String fixme = e.toString();
                        }
                    }
                });
    }
    private interface FirestoreCallback {
        void onCallback(List<String> list);
    }
    private void setList(String[] strArr) {

        if (checkbox) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,strArr);
            lvList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        else
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,strArr);
        lvList.setAdapter(adapter);
            lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(listType.equals("Groups") && !managerList.contains(i))
                    {
                        Toast.makeText(HomeActivity.this, "only group manager is allowed to assign task", Toast.LENGTH_SHORT).show();
                    }
                    else if (listType.equals("Groups") && managerList.contains(i))
                    {
                        pointer = i;
                        chosenCell = (String)((TextView) view).getText();
                        tvListViewHeader.setText("Tasks");
                        listType = "Tasks";
                        previusType = "Groups";
                        readData(new HomeActivity.FirestoreCallback() {
                            @Override
                            public void onCallback(List<String> list) {
                                groupArray =  new String[temp.size()];
                                for (int i = 0 ; i < groupArray.length;i++)
                                {
                                    groupArray[i] = temp.get(i);
                                }
                                setList(groupArray);
                            }
                        });
                    }
                    else if (listType.equals("Users"))
                    {
                        /*

                        pointer = i;
                        chosenCell = (String)((TextView) view).getText();
                        tvListViewHeader.setText("Tasks");
                        listType = "Tasks";
                        previusType = "Users";
                        readData(new HomeActivity.FirestoreCallback() {
                            @Override
                            public void onCallback(List<String> list) {
                                groupArray =  new String[temp.size()];
                                for (int i = 0 ; i < groupArray.length;i++)
                                {
                                    groupArray[i] = temp.get(i);
                                }
                                setList(groupArray);
                            }
                        });

                         */
                    }
                    else if (listType.equals("Tasks") && chosenCell != "" && pointer != -1 && previusType == "Groups")
                    {

                        if (chosenCell.contains("["))
                        {
                            chosenCell = chosenCell.substring(0,chosenCell.indexOf('[')-1);
                        }
                        DocumentReference taskRef = db.collection(previusType).document(chosenCell);
                        String task = (String)((TextView) view).getText();
                        String oldTask = task.toString();
                        if (task.contains(": open"))
                            task = task.substring(0,task.length()-6);
                        if (task.contains(": closed"))
                           task = task.substring(0,task.length()-8);
                        //DocumentReference taskOf = db.collection("Tasks").document(task);
                        // test !!!
                        DocumentReference taskOf = db.collection("Tasks").document(task);
                        taskOf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                SharedPreferences sh = getSharedPreferences("user data", MODE_PRIVATE);
                                String phone = sh.getString("phone","");
                                if(!documentSnapshot.getData().toString().contains("Manager of Task") || documentSnapshot.getData().toString().contains("Manager of Task:"+phone) )
                                {
                                    if (previusType.equals("Groups")) {
                                        taskOf.update("Manager of Task", sh.getString("phone",""));
                                    }
                                    taskRef.update("listTasks", FieldValue.arrayUnion(oldTask));
                                    Toast.makeText(HomeActivity.this, "task added to group: " + chosenCell, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(HomeActivity.this, "task has a different manager ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //if (previusType.equals("Groups")) {
                         //   SharedPreferences sh = getSharedPreferences("user data", MODE_PRIVATE);
                         //   taskOf.update("Manager of Task", sh.getString("phone",""));
                        //}
                        //Toast.makeText(HomeActivity.this, "task added to group: " + chosenCell, Toast.LENGTH_SHORT).show();
                    }
                    else if (managerList.contains(i))
                    {
                        // enter if here so only manger connected to task can change status
                        //sh.getString("phone","");
                        db = FirebaseFirestore.getInstance();
                        String task = (String)((TextView) view).getText().toString();
                        if (task.contains(": open")) {
                            String nTask = task.substring(0, task.length() - 6) + ": closed";
                            String oldTask = task;
                            String emptyTask = task.substring(0, task.length() - 6);
                            db.collection("Groups")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                db.collection("Groups") // change task status in Groups
                                                        .whereArrayContains("listTasks", oldTask)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        DocumentReference groupsRef = db.collection("Groups").document((String) document.get("group name"));
                                                                        groupsRef.update("listTasks", FieldValue.arrayRemove(oldTask));
                                                                        groupsRef.update("listTasks", FieldValue.arrayUnion(nTask));
                                                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                                                    }
                                                                } else {
                                                                    Log.d("findMe!", "Error getting documents: ", task.getException());
                                                                }
                                                            }
                                                        });

                                                DocumentReference taskRef = db.collection("Tasks").document(emptyTask); //change task status in Tasks
                                                taskRef.update("Task status", true) // update task to closed
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                               // Toast.makeText(HomeActivity.this, "task updated", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(HomeActivity.this, "task set to closed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                String b = e.toString();
                                                                Toast.makeText(HomeActivity.this, "update task fail", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                               //firestoreCallback.onCallback(temp);
                                            }
                                            else {
                                                Toast.makeText(HomeActivity.this, "tasklist update fail", Toast.LENGTH_SHORT).show();
                                                Exception e = task.getException();
                                                String fixme = e.toString();
                                            }
                                        }
                                    });

                        }
                        else if (task.contains(": closed"))
                        {
                            Toast.makeText(HomeActivity.this, "task is already closed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(HomeActivity.this, "only manger of the task can change status", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        lvList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                if (listType.equals("Groups"))
                {
                    Intent intent = new Intent(HomeActivity.this,GroupActivity.class);
                    String groupName = (String)((TextView) view).getText();
                    intent.putExtra("group name",groupName);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}