package com.example.mhamedsayed.studentcare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.mhamedsayed.studentcare.utils.RequestStatus;
import com.example.mhamedsayed.studentcare.utils.StudentRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AddRequestActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private EditText nameEditText, descriptionEditText, deptAmountEditText;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        deptAmountEditText = findViewById(R.id.deptAmountEditText);
        progressDialog = new ProgressDialog(this);
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

    }

    public void addRequest(View view) {
        final String name = nameEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String deptAmount = deptAmountEditText.getText().toString().trim();
        String message = getString(R.string.value_required_msg).trim();
        StudentRequest studentRequest = new StudentRequest(name, description, RequestStatus.PENDING.getValue(), deptAmount);
        if (TextUtils.isEmpty(studentRequest.getName())) {
            message = MessageFormat.format(message, "Request Name");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        } else if (TextUtils.isEmpty(studentRequest.getDescription())) {
            message = MessageFormat.format(message, "Description");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        } else if (TextUtils.isEmpty(studentRequest.getDeptAmount())) {
            message = MessageFormat.format(message, "Dept Amount");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        }
        progressDialog.setMessage("Adding Request");
        progressDialog.show();
        String key = FirebaseDatabase.getInstance().getReference("request").push().getKey();
        studentRequest.setUserId(firebaseAuth.getCurrentUser().getUid());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, studentRequest);
        database.getReference("request").updateChildren(childUpdates);
        progressDialog.hide();
        startActivity(new Intent(this, StudentActivity.class));
    }
}
