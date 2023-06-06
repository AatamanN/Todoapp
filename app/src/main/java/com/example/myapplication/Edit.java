package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Edit extends AppCompatActivity  {




    FirebaseFirestore db;
    EditText TaskIn,TaskOut,Desc;
    String TAG="EditList";
    Button saveB;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);




        saveB=findViewById(R.id.SaveBtn);
        db = FirebaseFirestore.getInstance();
        saveB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TaskIn=findViewById(R.id.old);
                TaskOut=findViewById(R.id.newT);
                Desc=findViewById(R.id.DescEdit);
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



                                            if (TaskIn.getText().toString().trim().equals(taskModel.getTaskName())) {
                                                if (!TaskOut.getText().toString().trim().equals("")){
                                                    taskModel.setTaskName(TaskOut.getText().toString().trim());
                                                    Toast.makeText(v.getContext(),"Task '" + TaskIn.getText().toString().trim() + "' Successfully renamed",Toast.LENGTH_SHORT).show();
                                                }
                                                if (!Desc.getText().toString().trim().equals(null)){
                                                    taskModel.setDescription(Desc.getText().toString().trim());
                                                }
                                                Toast.makeText(v.getContext(),"Done",Toast.LENGTH_SHORT).show();
                                                FirebaseFirestore.getInstance().collection("tasks").document(taskModel.getTaskId()).delete();


                                                db.collection("tasks").add(taskModel);


                                            }


                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }

                                }
                            });
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                }

        });


    }

}