package com.example.myapplication;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {



        private ArrayList<TaskModel> taskDataset;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView taskName,taskStatus,taskDate,taskDesc;

            LinearLayout container;
            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                taskName = (TextView) view.findViewById(R.id.taskName);
                taskStatus = (TextView) view.findViewById(R.id.taskStatus);
                container=(LinearLayout) view.findViewById(R.id.container);
                taskDate = (TextView) view.findViewById(R.id.taskDate);
                taskDesc = (TextView) view.findViewById(R.id.descText);
            }


        }

        /**
         * Initialize the dataset of the Adapter
         *
         * @param taskDataSet String[] containing the data to populate views to be used
         * by RecyclerView
         */
        public TaskAdapter(ArrayList<TaskModel> taskDataSet) {
            taskDataset = taskDataSet;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.task, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            FirebaseFirestore db;

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.taskName.setText(taskDataset.get(position).getTaskName());
            viewHolder.taskStatus.setText(taskDataset.get(position).getTaskStatus());
            viewHolder.taskDate.setText(taskDataset.get(position).getDate());
            viewHolder.taskDesc.setText(taskDataset.get(position).getDescription());

            String status=taskDataset.get(position).getTaskStatus();

                if(status.toLowerCase().equals("pending"))
                {
                    viewHolder.taskStatus.setBackgroundColor(Color.parseColor("#FFFF00"));

                } else if(status.toLowerCase().equals("complited"))
                {
                    viewHolder.taskStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                }else{

                    viewHolder.taskStatus.setBackgroundColor(Color.parseColor("#ff0000"));
                }

            db = FirebaseFirestore.getInstance();

            viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(viewHolder.container.getContext(), viewHolder.container);
                    popupMenu.inflate(R.menu.taskmenu);
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String date = sdf.format(new Date());
                            if(item.getItemId()==R.id.deleteMenu){
                                String name = db.collection("tasks").document(taskDataset.get(position).getTaskName()).getPath().replace("tasks/","");
                                String desc = db.collection("tasks").document(taskDataset.get(position).getDescription()).getPath().replace("tasks/","");
                                TaskModel taskModel= new TaskModel("",name,"DELETED", FirebaseAuth.getInstance().getUid(), date, desc);
                                db.collection("deleted").add(taskModel);
                                db.collection("tasks").document(taskDataset.get(position).getTaskId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(view.getContext(),"Task deleted",Toast.LENGTH_SHORT).show();
                                                viewHolder.taskStatus.setBackgroundColor(Color.parseColor("#ff0000"));
                                                viewHolder.taskStatus.setText("DELETED");
                                                //viewHolder.container.setVisibility(view.GONE);
                                            }
                                        });
                            }

                            if(item.getItemId()==R.id.Complete){

                                TaskModel complitedTask = taskDataset.get(position);
                                complitedTask.setTaskStatus("COMPLITED");
                                FirebaseFirestore.getInstance().collection("tasks").document(taskDataset.get(position).getTaskId())
                                        .set(complitedTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(view.getContext(),"Task completed",Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                viewHolder.taskStatus.setBackgroundColor(Color.parseColor("#00E000"));
                                viewHolder.taskStatus.setText("COMPLITED");
                            }



                            return false;
                        }


                    });


                    return false;
                }
            });


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return taskDataset != null ? taskDataset.size() : 0;
        }
    }


