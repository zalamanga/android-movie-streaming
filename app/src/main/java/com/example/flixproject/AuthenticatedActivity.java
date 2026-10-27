package com.example.flixproject;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

abstract class AuthenticatedActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private boolean sessionAvailable;

    @Override
    protected void onStart() {
        super.onStart();
        sessionAvailable = FirebaseSupport.requireSignedIn(this);
        if (!sessionAvailable) { return; }
        auth = FirebaseAuth.getInstance();
        authListener = current -> {
            if (current.getCurrentUser() == null) {
                sessionAvailable = false;
                FirebaseSupport.requireSignedIn(this);
            }
        };
        auth.addAuthStateListener(authListener);
    }

    protected final boolean hasActiveSession() {
        return sessionAvailable && !isFinishing();
    }

    @Override
    protected void onStop() {
        sessionAvailable = false;
        if (auth != null && authListener != null) {
            auth.removeAuthStateListener(authListener);
            authListener = null;
            auth = null;
        }
        super.onStop();
    }
}
