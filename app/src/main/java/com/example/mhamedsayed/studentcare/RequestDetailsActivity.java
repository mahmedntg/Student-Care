package com.example.mhamedsayed.studentcare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mhamedsayed.studentcare.utils.CloserDialogTimerTask;
import com.example.mhamedsayed.studentcare.utils.RequestStatus;
import com.example.mhamedsayed.studentcare.utils.StudentRequest;
import com.example.mhamedsayed.studentcare.utils.User;
import com.example.mhamedsayed.studentcare.utils.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class RequestDetailsActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private TextView descriptionTV, deptAmountTV, studentNameTV, studentLevelTV, studentState, studentWorkTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private EditText amountEditText;
    private Button payButton, acceptButton, rejectButton;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private StudentRequest studentRequest;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        descriptionTV = (TextView) findViewById(R.id.descriptionTextView);
        deptAmountTV = (TextView) findViewById(R.id.deptAmountTextView);
        studentNameTV = (TextView) findViewById(R.id.studentNameTextView);
        studentLevelTV = (TextView) findViewById(R.id.studentLevelTextView);
        studentState = (TextView) findViewById(R.id.studentStateTextView);
        studentWorkTV = (TextView) findViewById(R.id.studentWorkTextView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        payButton = (Button) findViewById(R.id.payBTN);
        payButton.setOnClickListener(this);
        acceptButton = (Button) findViewById(R.id.acceptBTN);
        acceptButton.setOnClickListener(this);
        rejectButton = (Button) findViewById(R.id.rejectBTN);
        rejectButton.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        studentRequest = (StudentRequest) extras.get("request");
        descriptionTV.setText("Request Description: " + studentRequest.getDescription());
        deptAmountTV.setText("Dept Amount: " + studentRequest.getDeptAmount() + " KWD");
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("data not valid");
        alertDialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        database.getReference("users").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(this);
        intent = new Intent(this, DonorActivity.class);
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if (user.getType().equals(UserType.DONOR.getValue())) {
            payButton.setVisibility(View.VISIBLE);
            amountEditText.setVisibility(View.VISIBLE);
        }
        if (user.getType().equals(UserType.ADMIN.getValue())) {
            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
            studentNameTV.setVisibility(View.VISIBLE);
            studentNameTV.setText("Student Name: " + user.getName());
            studentLevelTV.setVisibility(View.VISIBLE);
            studentLevelTV.setText("Student Level: " + user.getLevel());
            studentState.setVisibility(View.VISIBLE);
            studentState.setText("Student State: " + user.getState());
            studentWorkTV.setVisibility(View.VISIBLE);
            studentWorkTV.setText("Student Work Status: " + user.getWork());

        }
        progressDialog.hide();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w("TodoApp", "getRequest:onCancelled", databaseError.toException());
        progressDialog.hide();
    }


    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        switch (b.getId()) {
            case R.id.payBTN:
                payAmount();
                break;

            case R.id.acceptBTN:
                updateRequestStatus(RequestStatus.ACCEPTED);
                break;
            case R.id.rejectBTN:
                updateRequestStatus(RequestStatus.REJECTED);
                break;
        }
    }

    private void updateRequestStatus(RequestStatus requestStatus) {
        progressDialog.show();
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("status", requestStatus.getValue());
        database.getReference("request").child(studentRequest.getKey()).updateChildren(updatedData);
        startActivity(new Intent(this, AdminActivity.class));
        progressDialog.hide();
    }

    private void payAmount() {

        String amount = amountEditText.getText().toString().trim();
        String message = getString(R.string.value_required_msg).trim();
        if (TextUtils.isEmpty(amount)) {
            message = MessageFormat.format(message, "Amount");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        }

        double payedAmount = Double.valueOf(amount);
        double deptAmount = Double.valueOf(studentRequest.getDeptAmount());

        if (payedAmount > deptAmount) {
            alertDialog.setMessage("Enter Valid Amount");
            alertDialog.show();
            return;
        }
        Map<String, Object> updatedData = new HashMap<>();
        double finalAmount = deptAmount - payedAmount;
        updatedData.put("deptAmount", String.valueOf(finalAmount));
        if (finalAmount == 0)
            updatedData.put("status", RequestStatus.COMPLETED.getValue());
        database.getReference("request").child(studentRequest.getKey()).updateChildren(updatedData);
        progressDialog.setMessage("Please wait, your transaction is processing");
        progressDialog.setTitle("Connecting");
        progressDialog.show();
        Timer timer = new Timer();
        timer.schedule(new CloserDialogTimerTask(progressDialog), 5000);
        alertDialog.setMessage("Your payment has been succeeded");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Payment Succeed");
        alertDialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                alertDialog.show();
            }
        });

    }
}
