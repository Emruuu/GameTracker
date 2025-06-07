package com.example.gametracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
    private List<String> avatarFileNames;
    private StorageReference storageRef;
    private final Context context;
    private int selectedPosition = -1;

    public AvatarAdapter(List<String> avatarFileNames, StorageReference storageRef, Context context) {
        this.avatarFileNames = avatarFileNames;
        this.storageRef = storageRef;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String avatarFileName = avatarFileNames.get(position);

        holder.avatarNameTextView.setText(avatarFileName);
        Glide.with(context)
                .load(storageRef.child("avatars/" + avatarFileName))
                .into(holder.avatarImageView);
        holder.itemView.setSelected(selectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return avatarFileNames.size();
    }

    public void updateData(List<String> avatarFileNames, StorageReference storageRef) {
        this.avatarFileNames = avatarFileNames;
        this.storageRef = storageRef;
        notifyDataSetChanged();
    }

    public String getSelectedAvatarFileName() {
        int position = selectedPosition;
        if (position >= 0 && position < avatarFileNames.size()) {
            return avatarFileNames.get(position);
        }
        return null;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView avatarNameTextView;
        public final ImageView avatarImageView;

        public ViewHolder(View view) {
            super(view);
            avatarNameTextView = view.findViewById(R.id.tvAvatarRecord);
            avatarImageView = view.findViewById(R.id.ivAvatarRecord);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedPosition(getAdapterPosition());
                }
            });
        }

    }

}
