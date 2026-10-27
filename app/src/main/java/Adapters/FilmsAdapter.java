package Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import Models.FilmDataModel;
import com.example.flixproject.FilmsActivity;
import com.example.flixproject.R;

import java.util.List;

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.MyViewHolder> {

    private final List<FilmDataModel> dataModels;

    public FilmsAdapter(List<FilmDataModel> dataModels) {
        this.dataModels = dataModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false); //call movie card layout
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FilmDataModel film = dataModels.get(position);
        final String title = film.getFtitle();
        final String cover = film.getFcover();
        final String thumbnail = film.getFthumb();
        final String description = film.getFdis();
        final String cast = film.getFcast();
        final String link = film.getFlink();
        holder.textView.setText(title);
        Glide.with(holder.imageView.getContext()).load(thumbnail).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendDataToFeaturesActivity = new Intent(holder.itemView.getContext(), FilmsActivity.class);
                sendDataToFeaturesActivity.putExtra("title", title);
                sendDataToFeaturesActivity.putExtra("cover", cover);
                sendDataToFeaturesActivity.putExtra("thumb", thumbnail);
                sendDataToFeaturesActivity.putExtra("dis", description);
                sendDataToFeaturesActivity.putExtra("cast", cast);
                sendDataToFeaturesActivity.putExtra("link", link);

                // transition animation
                ActivityOptionsCompat optionsCompat =  ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)holder.itemView.getContext(),holder.imageView,
                                "imageMain"); // transition names all xml files
                holder.itemView.getContext().startActivity(sendDataToFeaturesActivity,optionsCompat.toBundle());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardImg);
            textView = itemView.findViewById(R.id.movieTitle);
        }
    }
}
