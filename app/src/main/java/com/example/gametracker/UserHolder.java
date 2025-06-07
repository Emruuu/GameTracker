package com.example.gametracker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserHolder extends RecyclerView.ViewHolder {
    TextView tvnick;
    ImageView ivavatar;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        tvnick = itemView.findViewById(R.id.tvNickSet);
        ivavatar = itemView.findViewById(R.id.ivAvatarSet);
    }
}