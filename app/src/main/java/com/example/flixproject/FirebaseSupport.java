package com.example.flixproject;

import android.app.Activity;
import android.content.Intent;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

final class FirebaseSupport {
    private FirebaseSupport() {}

    static boolean requireConfigured(Activity activity) {
        if (!FirebaseApp.getApps(activity).isEmpty()) {
            return true;
        }
        activity.setContentView(R.layout.activity_setup_required);
        return false;
    }

    static boolean requireSignedIn(Activity activity) {
        if (activity.isFinishing() || !requireConfigured(activity)) {
            return false;
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return true;
        }
        Intent login = new Intent(activity, LoginForm.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(login);
        activity.finish();
        return false;
    }
}
