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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodPostAdapter extends RecyclerView.Adapter<FoodPostAdapter.ViewHolder> {
    private List<FoodPost> foodPosts;
    private Context context;

    public FoodPostAdapter(Context context, List<FoodPost> foodPosts) {
        this.context = context;
        this.foodPosts = foodPosts;
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
        
        // Load image
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrls().get(0))
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_food);
        }
        
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView priceTextView;
        TextView postTypeTextView;
        TextView descriptionTextView;
        TextView quantityTextView;
        TextView timeLeftTextView;
        TextView distanceTextView;
        TextView expiryTextView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            postTypeTextView = itemView.findViewById(R.id.postTypeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            timeLeftTextView = itemView.findViewById(R.id.timeLeftTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            expiryTextView = itemView.findViewById(R.id.expiryTextView);
        }
    }
} 