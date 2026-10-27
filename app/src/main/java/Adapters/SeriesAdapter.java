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
import com.example.flixproject.FeaturesActivity;
import com.example.flixproject.R;
import Models.SeriesDataModel;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.MyViewHolder> {

    private final List<SeriesDataModel> modelList;

    //modelList constructor
    public SeriesAdapter(List<SeriesDataModel> modelList) {
        this.modelList = modelList;
    }

    //Implement methods for SeriesAdapter
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false); // call the layout
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SeriesDataModel series = modelList.get(position);
        final String title = series.getStitle();
        final String episodes = series.getSepisodes();
        final String cover = series.getScover();
        final String thumbnail = series.getSthumb();
        final String description = series.getSdis();
        final String cast = series.getScast();
        final String link = series.getSlink();
        holder.title.setText(title);
        Glide.with(holder.cardImageView.getContext()).load(thumbnail).into(holder.cardImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendDataToFeaturesActivity = new Intent(holder.itemView.getContext(), FeaturesActivity.class);

                sendDataToFeaturesActivity.putExtra("title", title);
                sendDataToFeaturesActivity.putExtra("parts", episodes);
                sendDataToFeaturesActivity.putExtra("cover", cover);
                sendDataToFeaturesActivity.putExtra("thumb", thumbnail);
                sendDataToFeaturesActivity.putExtra("dis", description);
                sendDataToFeaturesActivity.putExtra("cast", cast);
                sendDataToFeaturesActivity.putExtra("link", link);

                // transition animation
                ActivityOptionsCompat optionsCompat =  ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)holder.itemView.getContext(),holder.cardImageView,
                                "imageMain"); // transition names all xml files
                holder.itemView.getContext().startActivity(sendDataToFeaturesActivity,optionsCompat.toBundle());

    }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    //create ViewHolder Constructor
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImageView;
        TextView title;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movieTitle);
            cardImageView = itemView.findViewById(R.id.cardImg);
        }
    }
}

