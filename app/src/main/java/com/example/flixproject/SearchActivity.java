package com.example.flixproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import Adapters.SearchAdapter;
import Models.FilmDataModel;
import Models.FilmSearchResult;

public class SearchActivity extends AuthenticatedActivity {
    private final List<FilmSearchResult> results = new ArrayList<>();
    private SearchAdapter adapter;
    private DatabaseReference films;
    private int queryVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FirebaseSupport.requireSignedIn(this)) { return; }
        setContentView(R.layout.activity_search);
        films = FirebaseDatabase.getInstance().getReference("films");
        RecyclerView list = findViewById(R.id.searchRV);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new SearchAdapter(results);
        list.setAdapter(adapter);
        EditText input = findViewById(R.id.searchTxt);
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable text) { search(text.toString()); }
        });
    }

    private void search(String query) {
        int version = ++queryVersion;
        results.clear();
        adapter.notifyDataSetChanged();
        if (query.trim().isEmpty()) { return; }
        films.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (version != queryVersion || isFinishing() || isDestroyed()
                        || !FirebaseSupport.requireSignedIn(SearchActivity.this)) { return; }
                List<FilmSearchResult> rows = new ArrayList<>();
                for (DataSnapshot row : snapshot.getChildren()) {
                    FilmDataModel film = row.getValue(FilmDataModel.class);
                    if (film != null && FilmSearchResult.isValidKey(row.getKey())) {
                        rows.add(new FilmSearchResult(row.getKey(), film));
                    }
                }
                results.clear();
                results.addAll(MovieSearch.find(rows, query, 15));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (version == queryVersion && !isFinishing() && !isDestroyed()) {
                    Toast.makeText(SearchActivity.this, R.string.data_unavailable, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        queryVersion++;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
