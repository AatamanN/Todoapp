package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;

public class Activity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user added a task. Update the UI accordingly.
                Update();
            }
        }
    }

    public void Search (String Text){
        int i;
        dataListAll=(ArrayList<TaskModel>)dataList.clone();

            dataList.clear();
            i=0;
            TaskModel T;
            while (i<=dataListAll.size()-1){
                T=dataListAll.get(i);
                if (T.getTaskName().equals(Text)){
                    dataList.add(T);
                    taskAdapter.notifyDataSetChanged();
                }
                i++;
            }
            /*for (TaskModel T : dataListAll){
                i = 0;
                findViewById(R.id.btnSignOut).setVisibility(View.GONE);
                while (T.getTaskName().length() >= Text.length()+i)
                    if (T.getTaskName().regionMatches(i,Text,0,Text.length())){
                        dataList.add(T);
                    }
            }*/


    }


    RecyclerView task;
    static ArrayList<TaskModel> dataListAll = new ArrayList<>();
    static ArrayList<TaskModel> dataList = new ArrayList<>();

    TaskAdapter taskAdapter;
    FirebaseFirestore db;
    String TAG = "Homepage";
    TextView UserName;
    ImageView userImage;
    SearchView search;

    public void Update(){

        task = findViewById(R.id.taskList);
        taskAdapter = new TaskAdapter(dataList);
        dataList.clear();
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        task.setLayoutManager(lManager);
        task.setAdapter(taskAdapter);
        db = FirebaseFirestore.getInstance();
        db.collection("tasks")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                TaskModel taskModel = document.toObject(TaskModel.class);
                                taskModel.setTaskId(document.getId());
                                dataList.add(taskModel);




                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        taskAdapter.notifyDataSetChanged();


                    }
                });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);





        UserName = findViewById(R.id.userName);
        userImage = findViewById(R.id.Profile);
        search = findViewById(R.id.searchview);
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userImage);
        UserName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Update();
        findViewById(R.id.editing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Activity.this, Edit.class),1);

            }
        });

        findViewById(R.id.newTask).setOnClickListener(new View.OnClickListener() {

            @Override
           public void onClick(View v) {
                startActivityForResult(new Intent(Activity.this, NewTask.class), 1);

            }
        });

        findViewById(R.id.historyBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Activity.this, History_menu.class), 1);

            }
        });

        findViewById(R.id.btnSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(Activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });


        SearchView simpleSearchView = (SearchView) findViewById(R.id.searchview);


        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange (String Text) {
                if (Text.equals("")) Update();
                else Search(Text);



                 return false ;
            }
        });
    }
}