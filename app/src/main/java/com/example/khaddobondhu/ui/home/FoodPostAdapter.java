package com.example.khaddobondhu.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
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
    }

    @NonNull
    @Override
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
    }

    @Override
    public int getItemCount() {
        return foodPosts.size();
    }

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
        }
    }
} 