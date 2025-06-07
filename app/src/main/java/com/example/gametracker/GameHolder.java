package com.example.gametracker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GameHolder extends RecyclerView.ViewHolder {

    TextView tvData;
    TextView tvTitle;
    TextView tvPlatform;
    TextView tvGenre;
    ImageView ivBg;

    public GameHolder(@NonNull View itemView) {
        super(itemView);
        tvData =  itemView.findViewById(R.id.tvDateSet);
        tvTitle = itemView.findViewById(R.id.tvTitleSet);
        tvPlatform = itemView.findViewById(R.id.tvPlatformSet);
        tvGenre = itemView.findViewById(R.id.tvGenreSet);
        ivBg = itemView.findViewById(R.id.ivBackground);
    }
}
