package com.example.khaddobondhu.ui.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.utils.UserRoleUtils;
import java.util.List;

public class NGOAdapter extends RecyclerView.Adapter<NGOAdapter.ViewHolder> {
    private List<User> ngos;
    private Context context;

    public NGOAdapter(Context context, List<User> ngos) {
        this.context = context;
        this.ngos = ngos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_explore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User ngo = ngos.get(position);
        
        // Set NGO name
        holder.userNameTextView.setText(ngo.getName());
        
        // Set role badge
        holder.userRoleBadgeTextView.setText(UserRoleUtils.getUserTypeDisplayName(ngo.getUserType()));
        holder.userRoleBadgeTextView.setBackgroundResource(UserRoleUtils.getUserTypeBadgeDrawable(ngo.getUserType()));
        
        // Set description
        if (ngo.getDescription() != null && !ngo.getDescription().isEmpty()) {
            holder.userDescriptionTextView.setText(ngo.getDescription());
        } else {
            holder.userDescriptionTextView.setText("NGO helping distribute food to those in need");
        }
        
        // Set stats
        holder.postsCountTextView.setText("📝 " + ngo.getTotalPosts() + " posts");
        holder.ratingTextView.setText("⭐ " + String.format("%.1f", ngo.getAverageRating()));
        
        // Load user image
        if (ngo.getProfilePictureUrl() != null && !ngo.getProfilePictureUrl().isEmpty()) {
            Glide.with(context)
                    .load(ngo.getProfilePictureUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(holder.userImageView);
        } else {
            holder.userImageView.setImageResource(R.drawable.placeholder_food);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // TODO: Navigate to NGO profile or posts
            // For now, just show a toast
            android.widget.Toast.makeText(context, "Viewing " + ngo.getName(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return ngos.size();
    }

    public void updateUsers(List<User> newNgos) {
        this.ngos = newNgos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;
        TextView userRoleBadgeTextView;
        TextView userDescriptionTextView;
        TextView postsCountTextView;
        TextView ratingTextView;

        ViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userRoleBadgeTextView = itemView.findViewById(R.id.userRoleBadgeTextView);
            userDescriptionTextView = itemView.findViewById(R.id.userDescriptionTextView);
            postsCountTextView = itemView.findViewById(R.id.postsCountTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
} 