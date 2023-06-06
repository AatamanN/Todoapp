package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class History_menu extends AppCompatActivity {
    RecyclerView taskDel;

    static ArrayList<TaskModel> dataList = new ArrayList<>();

    TaskAdapter taskAdapter;
    FirebaseFirestore db;
    String TAG = "Homepage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_menu);
        taskDel = findViewById(R.id.taskList);
        taskAdapter = new TaskAdapter(dataList);
        dataList.clear();
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        taskDel.setLayoutManager(lManager);
        taskDel.setAdapter(taskAdapter);
        db = FirebaseFirestore.getInstance();
        db.collection("deleted")
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

}
