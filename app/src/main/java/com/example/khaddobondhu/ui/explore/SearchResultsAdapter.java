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
import com.example.khaddobondhu.utils.UserRoleUtils;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<User> searchResults;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public SearchResultsAdapter(Context context, List<User> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = searchResults.get(position);
        
        // Set user name
        holder.userNameTextView.setText(user.getName());
        
        // Set role badge
        holder.userRoleBadgeTextView.setText(UserRoleUtils.getUserTypeDisplayName(user.getUserType()));
        holder.userRoleBadgeTextView.setBackgroundResource(UserRoleUtils.getUserTypeBadgeDrawable(user.getUserType()));
        
        // Set description
        if (user.getDescription() != null && !user.getDescription().isEmpty()) {
            holder.userDescriptionTextView.setText(user.getDescription());
        } else {
            String defaultDescription = getDefaultDescription(user.getUserType());
            holder.userDescriptionTextView.setText(defaultDescription);
        }
        
        // Set stats
        holder.postsCountTextView.setText("📝 " + user.getTotalPosts() + " posts");
        holder.ratingTextView.setText("⭐ " + String.format("%.1f", user.getAverageRating()));
        
        // Load user image
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            String imageUrl = user.getProfilePictureUrl();
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
                intent.putExtra("image_title", user.getName() + "'s Profile Picture");
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
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateSearchResults(List<User> newResults) {
        this.searchResults = newResults;
        notifyDataSetChanged();
    }

    private String getDefaultDescription(String userType) {
        if (userType == null) {
            return "Individual sharing food with the community";
        }
        
        switch (userType) {
            case "RESTAURANT":
                return "Restaurant donating surplus food to the community";
            case "NGO":
                return "NGO helping distribute food to those in need";
            case "INDIVIDUAL":
            default:
                return "Individual sharing food with the community";
        }
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