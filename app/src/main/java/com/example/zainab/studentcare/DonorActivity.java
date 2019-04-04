package com.example.zainab.studentcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.example.zainab.studentcare.utils.RequestAdapter;
import com.example.zainab.studentcare.utils.RequestStatus;
import com.example.zainab.studentcare.utils.StudentRequest;
import com.example.zainab.studentcare.utils.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DonorActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private RequestAdapter mAdapter;
    private FirebaseDatabase database;
    List<StudentRequest> studentRequests;
    private StudentRequest studentRequest;
    private ProgressDialog progressDialog;
    private TextView emptyView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        emptyView = (TextView) findViewById(R.id.empty_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        studentRequests = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RequestAdapter(studentRequests, this, UserType.DONOR);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference("request").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        studentRequests.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            studentRequest = data.getValue(StudentRequest.class);
                            if (studentRequest.getStatus().equals(RequestStatus.ACCEPTED.getValue())) {
                                studentRequest.setKey(data.getKey());
                                studentRequests.add(studentRequest);
                            }
                        }
                        final LayoutAnimationController controller =
                                AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fall_down);

                        recyclerView.setLayoutAnimation(controller);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();
                        if (studentRequests.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        progressDialog.hide();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getRequest:onCancelled", databaseError.toException());
                        progressDialog.hide();
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutItemMenu:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
