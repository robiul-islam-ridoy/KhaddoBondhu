
package com.example.khaddobondhu.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.ui.post.PostDetailActivity;
import com.example.khaddobondhu.ui.image.ImagePreviewActivity;
import com.example.khaddobondhu.ui.image.ImageCarouselActivity;
import com.example.khaddobondhu.ui.view.ImageCollageView;
import com.example.khaddobondhu.ui.view.UserTypeBadgeView;
import com.example.khaddobondhu.utils.UserRoleUtils;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodPostAdapter extends RecyclerView.Adapter<FoodPostAdapter.ViewHolder> {
    private List<FoodPost> foodPosts;
    private Context context;
    private FirebaseService firebaseService;

    public FoodPostAdapter(Context context, List<FoodPost> foodPosts) {
        this.context = context;
        this.foodPosts = foodPosts;
        this.firebaseService = new FirebaseService();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodPost post = foodPosts.get(position);
        
        // Set title
        holder.titleTextView.setText(post.getTitle());
        
        // Set price
        String priceText = "";
        if ("DONATE".equals(post.getPostType()) || "REQUEST_DONATION".equals(post.getPostType())) {
            priceText = "Free";
        } else if ("SELL".equals(post.getPostType()) || "REQUEST_TO_BUY".equals(post.getPostType())) {
            if (post.getPrice() > 0) {
                priceText = "৳" + String.format("%.0f", post.getPrice());
            } else {
                priceText = "Free";
            }
        }
        
        if (!priceText.isEmpty()) {
            holder.priceTextView.setText(priceText);
            holder.priceTextView.setVisibility(View.VISIBLE);
        } else {
            holder.priceTextView.setVisibility(View.GONE);
        }
        
        // Set post type
        holder.postTypeTextView.setText(post.getPostType());

        // Set seller name - fetch dynamically from user table
        fetchUserNameAndSetDisplay(post.getUserId(), holder.sellerNameTextView);
        
        // Set user role badge - fetch from user table
        fetchUserTypeAndSetBadge(post.getUserId(), holder.userRoleBadgeView);

        // Set description
        holder.descriptionTextView.setText(post.getDescription());
        
        // Set quantity
        holder.quantityTextView.setText("• " + post.getFormattedQuantity());
        
        // Set time (created time)
        if (post.getCreatedAt() != null) {
            holder.timeLeftTextView.setText(getTimeAgo(post.getCreatedAt().toDate()));
        } else {
            holder.timeLeftTextView.setText("Just now");
        }
        
        // Set location
        holder.distanceTextView.setText(post.getPickupLocation());
        
        // Set expiry info
        if (post.getExpiryDate() != null) {
            String expiryText = "Expires: " + post.getFormattedExpiry();
            holder.expiryTextView.setText(expiryText);
            holder.expiryTextView.setVisibility(View.VISIBLE);
        } else {
            holder.expiryTextView.setVisibility(View.GONE);
        }
        
        // Load images using ImageCollageView
        holder.imageCollageView.setImages(post.getImageUrls(), post.getTitle());
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodPosts.size();
    }

    public void updatePosts(List<FoodPost> newPosts) {
        this.foodPosts = newPosts;
        notifyDataSetChanged();
    }

    private String getTimeAgo(Date date) {
        long timeInMillis = date.getTime();
        long now = System.currentTimeMillis();
        long diff = now - timeInMillis;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }
    
    private void fetchUserNameAndSetDisplay(String userId, TextView nameTextView) {
        if (userId == null || userId.isEmpty()) {
            // Set default name if no user ID
            nameTextView.setText("Unknown User");
            return;
        }
        
        firebaseService.getUserNameById(userId, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userName = task.getResult();
                    // Update UI on main thread
                    if (context != null) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            nameTextView.setText(userName);
                        });
                    }
                } else {
                    // Set default name on error
                    if (context != null) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            nameTextView.setText("Unknown User");
                        });
                    }
                }
            }
        });
    }

    private void fetchUserTypeAndSetBadge(String userId, UserTypeBadgeView badgeView) {
        if (userId == null || userId.isEmpty()) {
            // Set default badge if no user ID
            badgeView.setUserType("INDIVIDUAL");
            badgeView.setVisibility(View.VISIBLE);
            return;
        }
        
        firebaseService.getUserProfileData(userId, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                String userType = firebaseService.getCurrentUserType();
                final String finalUserType = (userType == null || userType.isEmpty()) ? "INDIVIDUAL" : userType;
                
                // Update UI on main thread
                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        badgeView.setUserType(finalUserType);
                        badgeView.setVisibility(View.VISIBLE);
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                // Set default badge on error
                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        badgeView.setUserType("INDIVIDUAL");
                        badgeView.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageCollageView imageCollageView;
        TextView titleTextView;
        TextView priceTextView;
        TextView postTypeTextView;
        TextView sellerNameTextView;
        TextView descriptionTextView;
        TextView quantityTextView;
        TextView timeLeftTextView;
        TextView distanceTextView;
        TextView expiryTextView;
        UserTypeBadgeView userRoleBadgeView;

        ViewHolder(View itemView) {
            super(itemView);
            imageCollageView = itemView.findViewById(R.id.imageCollageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            postTypeTextView = itemView.findViewById(R.id.postTypeTextView);
            sellerNameTextView = itemView.findViewById(R.id.sellerNameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            timeLeftTextView = itemView.findViewById(R.id.timeLeftTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            expiryTextView = itemView.findViewById(R.id.expiryTextView);
            userRoleBadgeView = itemView.findViewById(R.id.userRoleBadgeView);
        }
    }
} 