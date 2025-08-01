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

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<User> restaurants;
    private Context context;

    public RestaurantAdapter(Context context, List<User> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_explore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User restaurant = restaurants.get(position);
        
        // Set restaurant name
        holder.userNameTextView.setText(restaurant.getName());
        
        // Set role badge
        holder.userRoleBadgeTextView.setText(UserRoleUtils.getUserTypeDisplayName(restaurant.getUserType()));
        holder.userRoleBadgeTextView.setBackgroundResource(UserRoleUtils.getUserTypeBadgeDrawable(restaurant.getUserType()));
        
        // Set description
        if (restaurant.getDescription() != null && !restaurant.getDescription().isEmpty()) {
            holder.userDescriptionTextView.setText(restaurant.getDescription());
        } else {
            holder.userDescriptionTextView.setText("Restaurant sharing surplus food with the community");
        }
        
        // Set stats
        holder.postsCountTextView.setText("📝 " + restaurant.getTotalPosts() + " posts");
        holder.ratingTextView.setText("⭐ " + String.format("%.1f", restaurant.getAverageRating()));
        
        // Load user image
        if (restaurant.getProfilePictureUrl() != null && !restaurant.getProfilePictureUrl().isEmpty()) {
            Glide.with(context)
                    .load(restaurant.getProfilePictureUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(holder.userImageView);
        } else {
            holder.userImageView.setImageResource(R.drawable.placeholder_food);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // TODO: Navigate to restaurant profile or posts
            // For now, just show a toast
            android.widget.Toast.makeText(context, "Viewing " + restaurant.getName(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateUsers(List<User> newRestaurants) {
        this.restaurants = newRestaurants;
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