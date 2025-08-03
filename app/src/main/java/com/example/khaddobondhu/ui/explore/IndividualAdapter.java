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

public class IndividualAdapter extends RecyclerView.Adapter<IndividualAdapter.ViewHolder> {
    private List<User> individuals;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public IndividualAdapter(Context context, List<User> individuals) {
        this.context = context;
        this.individuals = individuals;
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
        User individual = individuals.get(position);
        
        // Set individual name
        holder.userNameTextView.setText(individual.getName());
        
        // Set role badge
        holder.userRoleBadgeTextView.setText(UserRoleUtils.getUserTypeDisplayName(individual.getUserType()));
        holder.userRoleBadgeTextView.setBackgroundResource(UserRoleUtils.getUserTypeBadgeDrawable(individual.getUserType()));
        
        // Set description
        if (individual.getDescription() != null && !individual.getDescription().isEmpty()) {
            holder.userDescriptionTextView.setText(individual.getDescription());
        } else {
            holder.userDescriptionTextView.setText("Individual sharing food with the community");
        }
        
        // Set stats
        holder.postsCountTextView.setText("📝 " + individual.getTotalPosts() + " posts");
        holder.ratingTextView.setText("⭐ " + String.format("%.1f", individual.getAverageRating()));
        
        // Load user image
        if (individual.getProfilePictureUrl() != null && !individual.getProfilePictureUrl().isEmpty()) {
            Glide.with(context)
                    .load(individual.getProfilePictureUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(holder.userImageView);
        } else {
            holder.userImageView.setImageResource(R.drawable.placeholder_food);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(individual);
            }
        });
    }

    @Override
    public int getItemCount() {
        return individuals.size();
    }

    public void updateUsers(List<User> newIndividuals) {
        this.individuals = newIndividuals;
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