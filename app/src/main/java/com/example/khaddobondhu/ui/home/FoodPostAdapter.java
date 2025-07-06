package com.example.khaddobondhu.ui.home;

<<<<<<< HEAD
import android.view.LayoutInflater;
import android.view.ViewGroup;
=======
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
<<<<<<< HEAD
import com.example.khaddobondhu.databinding.ItemFoodPostBinding;
import com.example.khaddobondhu.model.FoodPost;
import java.util.ArrayList;
import java.util.List;

public class FoodPostAdapter extends RecyclerView.Adapter<FoodPostAdapter.FoodPostViewHolder> {
    private List<FoodPost> foodPosts = new ArrayList<>();
    private final OnFoodPostClickListener listener;

    public interface OnFoodPostClickListener {
        void onFoodPostClick(FoodPost foodPost);
    }

    public FoodPostAdapter(OnFoodPostClickListener listener) {
        this.listener = listener;
=======
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
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }

    @NonNull
    @Override
<<<<<<< HEAD
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodPostBinding binding = ItemFoodPostBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
        );
        return new FoodPostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, int position) {
        holder.bind(foodPosts.get(position));
=======
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
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }

    @Override
    public int getItemCount() {
        return foodPosts.size();
    }

<<<<<<< HEAD
    public void setFoodPosts(List<FoodPost> foodPosts) {
        this.foodPosts = foodPosts;
        notifyDataSetChanged();
    }

    class FoodPostViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodPostBinding binding;

        FoodPostViewHolder(ItemFoodPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FoodPost foodPost) {
            binding.titleTextView.setText(foodPost.getTitle());
            binding.descriptionTextView.setText(foodPost.getDescription());
            binding.postTypeTextView.setText(foodPost.getPostType());
            binding.distanceTextView.setText(foodPost.getDistance());
            binding.timeLeftTextView.setText(foodPost.getTimeLeft());
            binding.quantityTextView.setText("• " + foodPost.getQuantity() + " servings");

            // Set price or hide if it's a donation
            if (foodPost.getPostType().equals("DONATE") || foodPost.getPostType().equals("REQUEST_DONATION")) {
                binding.priceTextView.setVisibility(android.view.View.GONE);
            } else {
                binding.priceTextView.setVisibility(android.view.View.VISIBLE);
                binding.priceTextView.setText("৳" + foodPost.getPrice());
            }

            // Load image using Glide
            Glide.with(binding.getRoot())
                .load(foodPost.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(binding.imageView);

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFoodPostClick(foodPost);
                }
            });
=======
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
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
        }
    }
} 