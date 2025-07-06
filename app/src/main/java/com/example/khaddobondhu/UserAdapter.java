package com.example.khaddobondhu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
<<<<<<< HEAD

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

=======
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.model.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserClickListener listener;

>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

<<<<<<< HEAD
    private ArrayList<User> users;
    private OnUserClickListener listener;

    public UserAdapter(ArrayList<User> users, OnUserClickListener listener) {
        this.users = users;
=======
    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
<<<<<<< HEAD
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
=======
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
<<<<<<< HEAD
        User user = users.get(position);
        holder.username.setText(user.getName());
        holder.profileImage.setImageResource(user.getProfileImageResId());

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
=======
        User user = userList.get(position);
        holder.bind(user);
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }

    @Override
    public int getItemCount() {
<<<<<<< HEAD
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView profileImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textViewUsername);
            profileImage = itemView.findViewById(R.id.imageViewProfile);
=======
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView userName, userEmail, userStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            profileImage = itemView.findViewById(R.id.imageViewProfile);
            userName = itemView.findViewById(R.id.textViewUserName);
            userEmail = itemView.findViewById(R.id.textViewUserEmail);
            userStatus = itemView.findViewById(R.id.textViewUserStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(userList.get(position));
                }
            });
        }

        public void bind(User user) {
            userName.setText(user.getName());
            
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                userEmail.setText(user.getEmail());
                userEmail.setVisibility(View.VISIBLE);
            } else {
                userEmail.setVisibility(View.GONE);
            }

            // Load profile image
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_person);
            }

            // Set user status
            if (user.getLastActive() != null) {
                long lastActiveTime = user.getLastActive().toDate().getTime();
                long currentTime = System.currentTimeMillis();
                long timeDifference = currentTime - lastActiveTime;
                
                if (timeDifference < 5 * 60 * 1000) { // 5 minutes
                    userStatus.setText("Online");
                    userStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                } else if (timeDifference < 60 * 60 * 1000) { // 1 hour
                    userStatus.setText("Recently active");
                    userStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                } else {
                    userStatus.setText("Offline");
                    userStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                }
            } else {
                userStatus.setText("Unknown");
                userStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
        }
    }
}
