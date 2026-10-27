package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.flixproject.R;
import com.example.flixproject.FilmsActivity;
import java.util.List;
import Models.FilmDataModel;
import Models.FilmSearchResult;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final List<FilmSearchResult> films;

    public SearchAdapter(List<FilmSearchResult> films) {
        this.films = films;
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;

        SearchViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.searchMovThub);
            title = view.findViewById(R.id.searchMovTitle);
        }
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_cards, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        FilmSearchResult result = films.get(position);
        FilmDataModel film = result.getFilm();
        holder.title.setText(film.getFtitle());
        Glide.with(holder.image).load(film.getFthumb()).into(holder.image);
        holder.itemView.setOnClickListener(view -> {
            Intent detail = new Intent(view.getContext(), FilmsActivity.class);
            detail.putExtra("filmId", result.getKey());
            view.getContext().startActivity(detail);
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }
}
