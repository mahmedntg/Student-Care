package com.example.zainab.studentcare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.zainab.studentcare.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText, ageEditText, levelEditText, emailEditText, passwordEditText;
    private RadioGroup stateRG, typeRG, workRG;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
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
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        levelEditText = (EditText) findViewById(R.id.levelEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        stateRG = (RadioGroup) findViewById(R.id.stateRadioGroup);
        typeRG = (RadioGroup) findViewById(R.id.typeRadioGroup);
        workRG = (RadioGroup) findViewById(R.id.workRadioGroup);
        ((RadioButton) findViewById(R.id.singleRadio)).setChecked(true);
        ((RadioButton) findViewById(R.id.studentRadio)).setChecked(true);
        ((RadioButton) findViewById(R.id.workRadio)).setChecked(true);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    public void registerUser(View view) {
        final String name = nameEditText.getText().toString().trim();
        final String age = ageEditText.getText().toString().trim();
        final String level = levelEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        int stateId = stateRG.getCheckedRadioButtonId();
        int typeId = typeRG.getCheckedRadioButtonId();
        int workId = workRG.getCheckedRadioButtonId();
        final String state = ((RadioButton) findViewById(stateId)).getText().toString();
        final String type = ((RadioButton) findViewById(typeId)).getText().toString();
        final String work = ((RadioButton) findViewById(workId)).getText().toString();
        String message = getString(R.string.value_required_msg).trim();
        final User user = new User(name, age, level, email, password, state, type, work);
        if (TextUtils.isEmpty(user.getName())) {
            message = MessageFormat.format(message, "Name");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        } else if (TextUtils.isEmpty(user.getAge())) {
            message = MessageFormat.format(message, "Age");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        } else if (TextUtils.isEmpty(user.getEmail())) {
            message = MessageFormat.format(message, "email");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        } else if (TextUtils.isEmpty(user.getPassword())) {
            message = MessageFormat.format(message, "Password");
            alertDialog.setMessage(message);
            alertDialog.show();
            return;
        }

        progressDialog.setMessage("Registering user");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = databaseReference.child(userId);
                    user.setPassword(null);
                    ref.setValue(user);
              /*      ref.child("name").setValue(name);
                    ref.child("age").setValue(age);
                    ref.child("level").setValue(level);
                    ref.child("status").setValue(status);
                    ref.child("type").setValue(type);
                    ref.child("work").setValue(work);
                    ref.child("email").setValue(email);*/

                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    alertDialog.setMessage(task.getException().getMessage());
                    alertDialog.show();
                }
                progressDialog.dismiss();
            }
        });
    }

}
