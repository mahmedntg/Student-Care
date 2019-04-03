package com.example.zainab.studentcare.utils;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseService extends FirebaseInstanceIdService {
    private static final String PREF_TOKEN = "student_token";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString(PREF_TOKEN, refreshedToken).apply();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

}