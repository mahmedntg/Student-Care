package com.example.mhamedsayed.studentcare.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mhamedsayed.studentcare.R;
import com.example.mhamedsayed.studentcare.RequestDetailsActivity;
import com.example.mhamedsayed.studentcare.StudentActivity;

import java.util.List;

/**
 * Created by mhamedsayed on 3/8/2019.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    private List<StudentRequest> studentRequests;
    private Activity activity;
    private UserType userType;
    private static ClickListener clickListener;


    public RequestAdapter(List<StudentRequest> studentRequests, Activity activity, UserType userType) {
        this.studentRequests = studentRequests;
        this.activity = activity;
        this.userType = userType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentRequest studentRequest = studentRequests.get(position);
        holder.setName(studentRequest.getName());
        holder.setStatus(studentRequest.getStatus());
        holder.setDeleteTextView();
        holder.position = position;
    }


    @Override
    public int getItemCount() {
        return studentRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public int position;
        View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
        }

        public void setName(String name) {
            TextView textView = (TextView) mView.findViewById(R.id.nameTextView);
            textView.setText(name);
            textView.setOnClickListener(this);
        }

        public void setStatus(String status) {
            TextView textView = (TextView) mView.findViewById(R.id.statusTextView);
            textView.setText(status);
            textView.setOnClickListener(this);
        }

        public void setDeleteTextView() {
            TextView textView = (TextView) mView.findViewById(R.id.deleteTextView);
            if (!userType.equals(UserType.DONOR))
                textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.deleteTextView) {
                StudentActivity mainActivity = (StudentActivity) activity;
                mainActivity.deleteRequestItem(position, studentRequests.get(position).getKey());

            } else if (v.getId() == R.id.nameTextView || v.getId() == R.id.statusTextView) {
                Intent newIntent = new Intent(activity, RequestDetailsActivity.class);
                StudentRequest studentRequest = studentRequests.get(position);
                newIntent.putExtra("request", studentRequest);
                activity.startActivity(newIntent);
            }

        }

   /*     public void setDeleteTextView() {
            TextView textView = (TextView) mView.findViewById(R.id.deleteTextView);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.deleteTextView) {
                BudgetActivity mainActivity = (BudgetActivity) activity;
                mainActivity.deleteBudgetItem(position, budgetList.get(position).getKey());

            }*/
        //}
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RequestAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }
}
