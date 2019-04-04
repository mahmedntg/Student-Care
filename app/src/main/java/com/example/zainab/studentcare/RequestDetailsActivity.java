package com.example.zainab.studentcare;

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

import com.example.zainab.studentcare.utils.CloserDialogTimerTask;
import com.example.zainab.studentcare.utils.NotificationApp;
import com.example.zainab.studentcare.utils.NotificationCall;
import com.example.zainab.studentcare.utils.RequestStatus;
import com.example.zainab.studentcare.utils.StudentRequest;
import com.example.zainab.studentcare.utils.User;
import com.example.zainab.studentcare.utils.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import uk.co.onemandan.materialtextview.MaterialTextView;

public class RequestDetailsActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private MaterialTextView descriptionTV, deptAmountTV, studentNameTV, studentLevelTV, studentState, studentWorkTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private EditText amountEditText;
    private TextView payButton, acceptButton, rejectButton;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private StudentRequest studentRequest;
    private Intent intent;
    private User user;
    private String studentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        descriptionTV = findViewById(R.id.descriptionTextView);
        deptAmountTV = findViewById(R.id.deptAmountTextView);
        studentNameTV = findViewById(R.id.studentNameTextView);
        studentLevelTV = findViewById(R.id.studentLevelTextView);
        studentState = findViewById(R.id.studentStateTextView);
        studentWorkTV = findViewById(R.id.studentWorkTextView);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        payButton =  findViewById(R.id.payBTN);
        payButton.setOnClickListener(this);
        acceptButton = findViewById(R.id.acceptBTN);
        acceptButton.setOnClickListener(this);
        rejectButton = findViewById(R.id.rejectBTN);
        rejectButton.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        studentRequest = (StudentRequest) extras.get("request");
        descriptionTV.setContentText(studentRequest.getDescription(), null);
        deptAmountTV.setContentText(studentRequest.getDeptAmount() + " KWD", null);
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
        user = dataSnapshot.getValue(User.class);
        if (user.getType().equals(UserType.DONOR.getValue())) {
            payButton.setVisibility(View.VISIBLE);
            amountEditText.setVisibility(View.VISIBLE);
        }
        if (user.getType().equals(UserType.ADMIN.getValue())) {
            if (studentRequest.getStatus().equals(RequestStatus.PENDING.getValue())) {
                acceptButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }
            studentNameTV.setVisibility(View.VISIBLE);
            studentLevelTV.setVisibility(View.VISIBLE);
            studentState.setVisibility(View.VISIBLE);
            studentWorkTV.setVisibility(View.VISIBLE);

        }
        database.getReference("users").child(studentRequest.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                studentNameTV.setContentText(user.getName(), null);
                studentLevelTV.setContentText(user.getLevel(), null);
                studentState.setContentText(user.getState(), null);
                studentWorkTV.setContentText(user.getWork(), null);
                studentToken = user.getToken();
                progressDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
            }

        });

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w("TodoApp", "getRequest:onCancelled", databaseError.toException());
        progressDialog.hide();
    }


    @Override
    public void onClick(View v) {
        TextView b = (TextView) v;
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
                sendNotification();
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

    private void sendNotification() {
        NotificationApp app = new NotificationApp();
        app.sendNotification(studentToken, "Payment Succeed", "Donor " + user.getName() + " paid for you " + Double.parseDouble(amountEditText.getText().toString()) + ": KWD");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
