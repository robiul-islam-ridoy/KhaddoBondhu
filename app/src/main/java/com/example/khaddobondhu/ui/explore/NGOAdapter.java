package com.example.khaddobondhu.ui.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.ui.image.ImagePreviewActivity;
import com.example.khaddobondhu.ui.view.UserTypeBadgeView;
import com.example.khaddobondhu.utils.UserRoleUtils;
import java.util.List;

public class NGOAdapter extends RecyclerView.Adapter<NGOAdapter.ViewHolder> {
    private List<User> ngos;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public NGOAdapter(Context context, List<User> ngos) {
        this.context = context;
        this.ngos = ngos;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
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
        holder.userRoleBadgeView.setUserType(ngo.getUserType());
        
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
            String imageUrl = ngo.getProfilePictureUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(holder.userImageView);
            
            // Add click listener for image preview
            holder.userImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra("image_url", imageUrl);
                intent.putExtra("image_title", ngo.getName() + "'s Profile Picture");
                context.startActivity(intent);
            });
        } else {
            holder.userImageView.setImageResource(R.drawable.placeholder_food);
            // Remove click listener if no image
            holder.userImageView.setOnClickListener(null);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(ngo);
            }
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
        UserTypeBadgeView userRoleBadgeView;
        TextView userDescriptionTextView;
        TextView postsCountTextView;
        TextView ratingTextView;

        ViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userRoleBadgeView = itemView.findViewById(R.id.userRoleBadgeView);
            userDescriptionTextView = itemView.findViewById(R.id.userDescriptionTextView);
            postsCountTextView = itemView.findViewById(R.id.postsCountTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
} 