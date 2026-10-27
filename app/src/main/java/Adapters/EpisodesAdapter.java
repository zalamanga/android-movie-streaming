package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import Models.EpisodesDataModel;
import com.example.flixproject.PlayerActivity;
import com.example.flixproject.R;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.MyViewHolder> {

    private final List<EpisodesDataModel> models;

    public EpisodesAdapter(List<EpisodesDataModel> models) {
        this.models = models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EpisodesDataModel episode = models.get(position);
        final String title = episode.getEpart();
        final String videoUrl = episode.getEvid();
        holder.episodeName.setText(title);
        Glide.with(holder.itemView).load(episode.getEthumb()).into(holder.episodeImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("vid", videoUrl);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView episodeImage;
        TextView episodeName;


        public MyViewHolder(View itemView) {
            super(itemView);
            episodeImage = itemView.findViewById(R.id.episodeThumb);
            episodeName = itemView.findViewById(R.id.episodeName);
        }
    }
}
