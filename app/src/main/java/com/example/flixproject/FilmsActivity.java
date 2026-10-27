package com.example.flixproject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.CastAdapter;
import Models.CastDataModel;
import Models.FilmDataModel;
import Models.FilmSearchResult;

public class FilmsActivity extends AuthenticatedActivity {

    private List<CastDataModel> castDataModels;
    private CastAdapter castAdapter;

    private RecyclerView castRecyclerView;

    private ImageView thumb;
    private ImageView cover;
    private TextView title;
    private TextView dis;
    private FloatingActionButton playBtn;

    private String movieTitle;
    private String movieDis;
    private String movieThumb;
    private String movieCover;
    private String movieCast;
    private String movieLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FirebaseSupport.requireSignedIn(this)) {
            return;
        }
        setContentView(R.layout.activity_films);

        thumb = findViewById(R.id.fThumb);
        cover = findViewById(R.id.coverImg);
        title = findViewById(R.id.fTitle);
        dis = findViewById(R.id.fDis);
        playBtn = findViewById(R.id.featuresPlayBtn);
        castRecyclerView = findViewById(R.id.castRecycleView);

        movieTitle = getIntent().getStringExtra("title");
        movieDis = getIntent().getStringExtra("dis");
        movieThumb = getIntent().getStringExtra("thumb");
        movieCover = getIntent().getStringExtra("cover");
        movieCast = getIntent().getStringExtra("cast");
        movieLink = getIntent().getStringExtra("link");

        playBtn.setEnabled(false);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FirebaseSupport.requireSignedIn(FilmsActivity.this)) { return; }
                if (!FilmSearchResult.isValidKey(movieLink)) {
                    Toast.makeText(FilmsActivity.this, R.string.media_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
                playBtn.setEnabled(false);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.child("link").child(movieLink).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (isFinishing() || isDestroyed()
                                || !FirebaseSupport.requireSignedIn(FilmsActivity.this)) { return; }
                        playBtn.setEnabled(true);
                        String vidUrl = snapshot.getValue(String.class);
                        if (android.text.TextUtils.isEmpty(vidUrl) || isFinishing() || isDestroyed()) {
                            Toast.makeText(FilmsActivity.this, R.string.media_unavailable, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(FilmsActivity.this, PlayerActivity.class);
                        intent.putExtra("vid", vidUrl);
                        intent.putExtra("title", movieTitle);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        if (isFinishing() || isDestroyed()) { return; }
                        playBtn.setEnabled(true);
                        Toast.makeText(FilmsActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        String filmId = getIntent().getStringExtra("filmId");
        if (filmId == null) {
            renderDetail();
        } else {
            loadSelectedRecord(filmId);
        }
    }

    private void loadSelectedRecord(String filmId) {
        if (!FilmSearchResult.isValidKey(filmId)) { showMissingRecord(); return; }
        FirebaseDatabase.getInstance().getReference("films").child(filmId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (isFinishing() || isDestroyed()
                                || !FirebaseSupport.requireSignedIn(FilmsActivity.this)) { return; }
                        FilmDataModel film = snapshot.getValue(FilmDataModel.class);
                        if (film == null) { showMissingRecord(); return; }
                        movieTitle = film.getFtitle();
                        movieDis = film.getFdis();
                        movieThumb = film.getFthumb();
                        movieCover = film.getFcover();
                        movieCast = film.getFcast();
                        movieLink = film.getFlink();
                        renderDetail();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        if (!isFinishing() && !isDestroyed()
                                && FirebaseSupport.requireSignedIn(FilmsActivity.this)) { showMissingRecord(); }
                    }
                });
    }

    private void showMissingRecord() {
        title.setText(R.string.data_unavailable);
        dis.setText("");
        playBtn.setEnabled(false);
        Toast.makeText(this, R.string.data_unavailable, Toast.LENGTH_SHORT).show();
    }

    private void renderDetail() {
        Glide.with(this).load(movieThumb).into(thumb);
        Glide.with(this).load(movieCover).into(cover);
        thumb.setAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_animation));
        cover.setAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_animation));
        title.setText(movieTitle);
        dis.setText(movieDis);
        playBtn.setEnabled(FilmSearchResult.isValidKey(movieLink));
        loadCast();
    }

    private void loadCast() {
        if (!FilmSearchResult.isValidKey(movieCast)) { return; }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference castRef = database.getReference();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        castRecyclerView.setLayoutManager(layoutManager);

        castDataModels = new ArrayList<>();
        castAdapter = new CastAdapter(castDataModels);
        castRecyclerView.setAdapter(castAdapter);

        castRef.child("cast").child(movieCast).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isFinishing() || isDestroyed()
                        || !FirebaseSupport.requireSignedIn(FilmsActivity.this)) { return; }
                castDataModels.clear();
                for (DataSnapshot content : snapshot.getChildren()) { // firebase children gets form cast
                    CastDataModel castDataModel = content.getValue(CastDataModel.class);
                    if (castDataModel != null) { castDataModels.add(castDataModel); }
                }
                castAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
