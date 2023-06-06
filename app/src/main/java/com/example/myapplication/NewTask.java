package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NewTask extends AppCompatActivity {

    EditText TaskIn,DescIn;
    Button saveB;
    String TAG="ToDo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        saveB=findViewById(R.id.SaveBtn);
        TaskIn=findViewById(R.id.TaskInput);
        DescIn=findViewById(R.id.descIn);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskName=TaskIn.getText().toString().trim();
                String Desc=DescIn.getText().toString().trim();

                if(taskName!=null)
                {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf.format(new Date());
                    TaskModel taskModel= new TaskModel("",taskName,"PENDING", FirebaseAuth.getInstance().getUid(), date, Desc);
                    db.collection("tasks").add(taskModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());


                                    Toast.makeText(v.getContext(),"Task '" + taskModel.getTaskName() + "' Successfully Saved",Toast.LENGTH_SHORT).show();





                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                }

                Intent resultIntent = new Intent();

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}