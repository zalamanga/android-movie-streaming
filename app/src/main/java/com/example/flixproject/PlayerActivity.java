package com.example.flixproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AuthenticatedActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private Uri videoUri;
    private long position;
    private boolean playWhenReady = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FirebaseSupport.requireSignedIn(this)) { return; }
        String url = getIntent().getStringExtra("vid");
        if (url == null || url.trim().isEmpty()) { unavailable(); return; }
        videoUri = Uri.parse(url);
        if (!("https".equalsIgnoreCase(videoUri.getScheme()) || "http".equalsIgnoreCase(videoUri.getScheme()))
                || videoUri.getHost() == null) { unavailable(); return; }
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.videoPlayer);
        Toolbar toolbar = findViewById(R.id.playerToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String title = getIntent().getStringExtra("title");
            getSupportActionBar().setTitle(title == null ? getString(R.string.app_name) : title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong("position");
            playWhenReady = savedInstanceState.getBoolean("playWhenReady", true);
        }
    }

    private void unavailable() {
        Toast.makeText(this, R.string.media_unavailable, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasActiveSession() || videoUri == null || playerView == null) { return; }
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                playerView.setKeepScreenOn(isPlaying);
            }
        });
        player.setMediaItem(MediaItem.fromUri(videoUri));
        player.seekTo(position);
        player.prepare();
        player.setPlayWhenReady(playWhenReady);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putLong("position", player == null ? position : player.getCurrentPosition());
        state.putBoolean("playWhenReady", player == null ? playWhenReady : player.getPlayWhenReady());
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onStop() {
        if (player != null) {
            position = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            playerView.setPlayer(null);
            player.release();
            player = null;
            playerView.setKeepScreenOn(false);
        }
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
