
package com.example.khaddobondhu.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.Message;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.utils.SecurityUtils;
import com.example.khaddobondhu.utils.ErrorHandler;
import com.example.khaddobondhu.utils.CacheManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseService {
    private static final String TAG = "FirebaseService";
    
    // Firebase instances
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    
    // Collection references
    private CollectionReference usersRef;
    private CollectionReference postsRef;
    private CollectionReference messagesRef;
    
    // Current user
    private FirebaseUser currentUser;
    
    private Context context;
    private CloudinaryService cloudinaryService;
    
    private List<FoodPost> foodPosts = new ArrayList<>();
    private List<FoodPost> userPosts = new ArrayList<>();
    
    // Search and Filter methods
    private List<FoodPost> searchResults = new ArrayList<>();
    private List<FoodPost> filteredResults = new ArrayList<>();
    
    public interface Callback {
        void onSuccess();
        void onError(String error);
    }
    
    public FirebaseService() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Initialize collection references
        usersRef = db.collection("users");
        postsRef = db.collection("food_posts");
        messagesRef = db.collection("messages");
        
        // Get current user
        currentUser = auth.getCurrentUser();
    }
    
    public FirebaseService(Context context) {
        this();
        this.context = context;
        this.cloudinaryService = new CloudinaryService(context);
    }
    
    // Authentication methods
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    public void testUserAuthentication(Callback callback) {
        if (getCurrentUser() == null) {
            callback.onError("No authenticated user");
            return;
        }
        
        Log.d(TAG, "Testing user authentication:");
        Log.d(TAG, "User ID: " + getCurrentUser().getUid());
        Log.d(TAG, "User Email: " + getCurrentUser().getEmail());
        Log.d(TAG, "User Display Name: " + getCurrentUser().getDisplayName());
        Log.d(TAG, "User is Email Verified: " + getCurrentUser().isEmailVerified());
        
        // Test if user can read from users collection
        usersRef.document(getCurrentUser().getUid()).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User can read their own profile");
                    callback.onSuccess();
                } else {
                    Log.e(TAG, "User cannot read their own profile: " + task.getException().getMessage());
                    callback.onError("Cannot read user profile: " + task.getException().getMessage());
                }
            });
    }
    
    public void signOut() {
        auth.signOut();
        currentUser = null;
    }
    
    // User management methods
    public void createUserProfile(User user, OnCompleteListener<Void> listener) {
        if (getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user");
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phoneNumber", user.getPhoneNumber());
        userData.put("userType", user.getUserType()); // Add user type to database
        userData.put("description", user.getDescription());
        userData.put("totalPosts", user.getTotalPosts());
        userData.put("totalDonations", user.getTotalDonations());
        userData.put("totalReceived", user.getTotalReceived());
        userData.put("rating", user.getRating());
        userData.put("createdAt", com.google.firebase.Timestamp.now());

        db.collection("users").document(user.getId())
            .set(userData)
            .addOnCompleteListener(listener);
    }
    
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void getUserNameById(String userId, OnCompleteListener<String> listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        listener.onComplete(Tasks.forResult(name));
                    } else {
                        listener.onComplete(Tasks.forResult(null));
                    }

                    Log.d("FIREBASE", "Fetching user: " + userId);
                    Log.d("FIREBASE", "Document exists: " + task.getResult().exists());
                    Log.d("FIREBASE", "User name = " + task.getResult().getString("name"));

                });

    }

    public void getUserProfile(String userId, Callback callback) {
        if (db == null) {
            callback.onError("Firestore not initialized");
            return;
        }

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        callback.onSuccess();
                    } else {
                        String error = "Failed to load user profile";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        callback.onError(error);
                    }
                });
    }
    
    public void updateUserProfile(String name, String phone, String bio, String profilePictureUrl, Callback callback) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onError("User not authenticated");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phoneNumber", phone);
        updates.put("description", bio);
        if (profilePictureUrl != null) {
            updates.put("profilePictureUrl", profilePictureUrl);
        }
        updates.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users").document(firebaseUser.getUid())
            .update(updates)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update local cache
                    currentUserName = name;
                    currentUserPhone = phone;
                    currentUserBio = bio;
                    if (profilePictureUrl != null) {
                        currentUserProfilePictureUrl = profilePictureUrl;
                    }
                    callback.onSuccess();
                } else {
                    callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                }
            });
    }
    
    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        usersRef.get().addOnCompleteListener(listener);
    }
    
    // Food post methods
    public void createFoodPost(FoodPost foodPost, OnCompleteListener<DocumentReference> listener) {
        if (getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user");
            return;
        }

        Log.d(TAG, "Creating food post for user: " + getCurrentUser().getUid());
        Log.d(TAG, "User email: " + getCurrentUser().getEmail());

        // Security validation
        SecurityUtils.ValidationResult validation = SecurityUtils.validateFoodPost(foodPost);
        if (!validation.isValid()) {
            Log.e(TAG, "Food post validation failed: " + validation.getMessage());
            if (context != null) {
                ErrorHandler.handleValidationError("Food Post", validation.getMessage(), context);
            }
            return;
        }

        // Sanitize inputs
        foodPost.setTitle(SecurityUtils.sanitizeInput(foodPost.getTitle()));
        foodPost.setDescription(SecurityUtils.sanitizeInput(foodPost.getDescription()));
        foodPost.setPickupLocation(SecurityUtils.sanitizeInput(foodPost.getPickupLocation()));

        Map<String, Object> postData = new HashMap<>();
        postData.put("userId", foodPost.getUserId());
        postData.put("userName", foodPost.getUserName());
        postData.put("title", foodPost.getTitle());
        postData.put("description", foodPost.getDescription());
        postData.put("postType", foodPost.getPostType());
        postData.put("price", foodPost.getPrice());
        postData.put("quantity", foodPost.getQuantity());
        postData.put("quantityUnit", foodPost.getQuantityUnit());
        postData.put("foodType", foodPost.getFoodType());
        postData.put("pickupLocation", foodPost.getPickupLocation());
        postData.put("imageUrls", foodPost.getImageUrls());
        postData.put("createdAt", com.google.firebase.Timestamp.now());
        postData.put("isActive", true);
        
        // Add expiry date if it exists
        if (foodPost.getExpiryDate() != null) {
            postData.put("expiryDate", foodPost.getExpiryDate());
        }

        Log.d(TAG, "Attempting to create post with data: " + postData.toString());

        db.collection("food_posts")
            .add(postData)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Post created successfully with ID: " + task.getResult().getId());
                } else {
                    Log.e(TAG, "Failed to create post: " + task.getException().getMessage());
                    Log.e(TAG, "Exception type: " + task.getException().getClass().getSimpleName());
                }
                listener.onComplete(task);
            });
    }
    
    public void getFoodPosts(OnCompleteListener<QuerySnapshot> listener) {
        getFoodPostsPaginated(0, 20, listener);
    }
    
    public void getFoodPostsPaginated(int page, int limit, OnCompleteListener<QuerySnapshot> listener) {
        Query query = postsRef.whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Cache the results
                if (context != null) {
                    CacheManager cacheManager = new CacheManager(context);
                    List<FoodPost> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        FoodPost post = doc.toObject(FoodPost.class);
                        if (post != null) {
                            post.setId(doc.getId());
                            posts.add(post);
                            cacheManager.cacheFoodPost(doc.getId(), post);
                        }
                    }
                    cacheManager.setLastUpdateTime(System.currentTimeMillis());
                }
            }
            listener.onComplete(task);
        });
    }
    
    public void getFoodPostsByUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        postsRef.whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }
    
    public void getFoodPostById(String postId, OnCompleteListener<DocumentSnapshot> listener) {
        postsRef.document(postId).get().addOnCompleteListener(listener);
    }
    
    public void updateFoodPost(String postId, String title, String description, String postType, 
                              double price, int quantity, String quantityUnit, String foodType, 
                              String location, com.google.firebase.Timestamp expiryDate, java.util.List<String> imageUrls, Callback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("description", description);
        updates.put("postType", postType);
        updates.put("price", price);
        updates.put("quantity", quantity);
        updates.put("quantityUnit", quantityUnit);
        updates.put("foodType", foodType);
        updates.put("pickupLocation", location);
        if (expiryDate != null) {
            updates.put("expiryDate", expiryDate);
        }
        if (imageUrls != null && !imageUrls.isEmpty()) {
            updates.put("imageUrls", imageUrls);
        }
        updates.put("updatedAt", com.google.firebase.Timestamp.now());
        
        db.collection("food_posts").document(postId)
            .update(updates)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to update post: " + task.getException().getMessage());
                }
            });
    }
    
    public void deleteFoodPost(String postId, OnCompleteListener<Void> listener) {
        db.collection("food_posts").document(postId)
                .delete()
                .addOnCompleteListener(listener);
    }
    
    // Messaging methods
    public void sendMessage(Message message, OnCompleteListener<DocumentReference> listener) {
        if (getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user");
            return;
        }

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", message.getSenderId());
        messageData.put("receiverId", message.getReceiverId());
        messageData.put("content", message.getContent());
        messageData.put("chatId", message.getChatId());
        messageData.put("timestamp", com.google.firebase.Timestamp.now());
        messageData.put("isRead", false);

        db.collection("messages")
            .add(messageData)
            .addOnCompleteListener(listener);
    }
    
    public void getMessages(String chatId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener(listener);
    }
    
    public void markMessageAsRead(String messageId, OnCompleteListener<Void> listener) {
        messagesRef.document(messageId)
                .update("isRead", true)
                .addOnCompleteListener(listener);
    }
    
    // Search and filter methods
    public void searchFoodPosts(String query, Callback callback) {
        if (query == null || query.trim().isEmpty()) {
            callback.onError("Search query cannot be empty");
            return;
        }
        
        String searchQuery = query.toLowerCase().trim();
        
        db.collection("food_posts")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    searchResults.clear();
                                            for (DocumentSnapshot doc : task.getResult()) {
                            FoodPost post = doc.toObject(FoodPost.class);
                            if (post != null) {
                                post.setId(doc.getId());
                                // Search in title, description, and location
                                if (post.getTitle().toLowerCase().contains(searchQuery) ||
                                    post.getDescription().toLowerCase().contains(searchQuery) ||
                                    post.getPickupLocation().toLowerCase().contains(searchQuery)) {
                                    searchResults.add(post);
                                }
                            }
                        }
                    callback.onSuccess();
                } else {
                    callback.onError("Search failed: " + task.getException().getMessage());
                }
            });
    }
    
    public List<FoodPost> getSearchResults() {
        return searchResults;
    }

    public void filterFoodPosts(String postType, String foodType, int minPrice, int maxPrice, String location, Callback callback) {
        db.collection("food_posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        filteredResults.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            FoodPost post = doc.toObject(FoodPost.class);
                            if (post != null) {
                                post.setId(doc.getId());

                                boolean matchesFilter = true;

                                // Post Type Filter
                                if (!"All Types".equals(postType) && !post.getPostType().equalsIgnoreCase(postType)) {
                                    matchesFilter = false;
                                }

                                // Food Type Filter
                                if (!"All Foods".equals(foodType) && !post.getFoodType().equalsIgnoreCase(foodType)) {
                                    matchesFilter = false;
                                }

                                // Price Range Filter
                                if (post.getPrice() < minPrice || post.getPrice() > maxPrice) {
                                    matchesFilter = false;
                                }

                                // Location Filter
                                if (location != null && !location.trim().isEmpty()) {
                                    if (!post.getPickupLocation().toLowerCase().contains(location.toLowerCase().trim())) {
                                        matchesFilter = false;
                                    }
                                }

                                if (matchesFilter) {
                                    filteredResults.add(post);
                                }
                            }
                        }
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to fetch posts: " + task.getException().getMessage());
                    }
                });
    }


    public List<FoodPost> getFilteredResults() {
        return filteredResults;
    }
    
    public void getFoodPostsByType(String foodType, OnCompleteListener<QuerySnapshot> listener) {
        postsRef.whereEqualTo("status", "ACTIVE")
                .whereEqualTo("foodType", foodType)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }
    
    public void getFoodPostsByPostType(String postType, OnCompleteListener<QuerySnapshot> listener) {
        postsRef.whereEqualTo("status", "ACTIVE")
                .whereEqualTo("postType", postType)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }
    
    // Real-time listeners
    public void addFoodPostsListener(OnCompleteListener<QuerySnapshot> listener) {
        db.collection("food_posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.e(TAG, "Error listening for food posts", error);
                    return;
                }
                if (value != null) {
                    // Create a simple success callback
                    listener.onComplete(new Task<QuerySnapshot>() {
                        @Override
                        public boolean isComplete() { return true; }
                        @Override
                        public boolean isSuccessful() { return true; }
                        @Override
                        public boolean isCanceled() { return false; }
                        @Override
                        public QuerySnapshot getResult() { return value; }
                        @Override
                        public Exception getException() { return null; }
                        @Override
                        public Task<QuerySnapshot> addOnSuccessListener(OnSuccessListener<? super QuerySnapshot> listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnFailureListener(OnFailureListener listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnCompleteListener(OnCompleteListener<QuerySnapshot> listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super QuerySnapshot> listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<QuerySnapshot> listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super QuerySnapshot> listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override
                        public Task<QuerySnapshot> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<QuerySnapshot> listener) { return this; }
                        @Override
                        public <X extends Throwable> QuerySnapshot getResult(Class<X> aClass) throws X { return value; }
                    });
                }
            });
    }
    
    public ListenerRegistration addMessagesListener(String chatId, com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return messagesRef.whereEqualTo("chatId", chatId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }
    
    // Utility methods
    public void incrementPostViews(String postId) {
        if (postId == null || postId.isEmpty()) return;
        
        db.collection("food_posts").document(postId)
            .update("views", FieldValue.increment(1))
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Failed to increment views for post: " + postId);
                }
            });
    }
    
    public void incrementPostRequests(String postId) {
        if (postId == null || postId.isEmpty()) return;
        
        db.collection("food_posts").document(postId)
            .update("requests", FieldValue.increment(1))
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Failed to increment requests for post: " + postId);
                }
            });
    }
    
    public void updateUserLastActive() {
        if (currentUser != null) {
            usersRef.document(currentUser.getUid())
                    .update("lastActive", new Date());
        }
    }

    public void getUserPosts(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("food_posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener(listener);
    }

    public void getUserChats(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection("messages")
            .whereEqualTo("chatId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(listener);
    }

    public void getAllFoodPosts(Callback callback) {
        if (db == null) {
            callback.onError("Firestore not initialized");
            return;
        }

        db.collection("food_posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        foodPosts.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            FoodPost post = document.toObject(FoodPost.class);
                            if (post != null && post.isActive()) { // Filter active posts in app
                                post.setId(document.getId());
                                foodPosts.add(post);
                            }
                        }
                        callback.onSuccess();
                    } else {
                        String error = "Failed to load posts";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        callback.onError(error);
                    }
                });
    }

    public List<FoodPost> getFoodPosts() {
        return new ArrayList<>(foodPosts);
    }

    public void getUserFoodPosts(String userId, Callback callback) {
        if (db == null) {
            callback.onError("Firestore not initialized");
            return;
        }

        db.collection("food_posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        userPosts.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            FoodPost post = document.toObject(FoodPost.class);
                            if (post != null) {
                                post.setId(document.getId());
                                userPosts.add(post);
                            }
                        }
                        // Sort by creation time (newest first) in the app
                        userPosts.sort((p1, p2) -> {
                            if (p1.getCreatedAt() == null) return 1;
                            if (p2.getCreatedAt() == null) return -1;
                            return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                        });
                        callback.onSuccess();
                    } else {
                        String error = "Failed to load user posts";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        callback.onError(error);
                    }
                });
    }

    public List<FoodPost> getUserPosts() {
        return new ArrayList<>(userPosts);
    }

    // Get user profile data
    public void getUserProfileData(String userId, Callback callback) {
        db.collection("users").document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    // Store user data for later use
                    currentUserName = doc.getString("name");
                    currentUserEmail = doc.getString("email");
                    currentUserPhone = doc.getString("phoneNumber");
                    currentUserBio = doc.getString("description");
                    currentUserProfilePictureUrl = doc.getString("profilePictureUrl");
                    currentUserType = doc.getString("userType");
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to load user profile");
                }
            });
    }
    
    // User data storage
    private String currentUserName;
    private String currentUserEmail;
    private String currentUserPhone;
    private String currentUserBio;
    private String currentUserProfilePictureUrl;
    private String currentUserType;
    
    public String getCurrentUserName() {
        return currentUserName != null ? currentUserName : "User";
    }
    
    public String getCurrentUserEmail() {
        return currentUserEmail != null ? currentUserEmail : "No email";
    }
    
    public String getCurrentUserPhone() {
        return currentUserPhone != null ? currentUserPhone : "No phone";
    }
    
    public String getCurrentUserBio() {
        return currentUserBio != null ? currentUserBio : "No bio";
    }

    public String getCurrentUserProfilePictureUrl() {
        return currentUserProfilePictureUrl;
    }

    public String getCurrentUserType() {
        return currentUserType != null ? currentUserType : "INDIVIDUAL";
    }

    // Get post statistics
    public void getPostStatistics(String postId, Callback callback) {
        if (postId == null || postId.isEmpty()) {
            callback.onError("Invalid post ID");
            return;
        }
        
        db.collection("food_posts").document(postId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    Long views = doc.getLong("views");
                    Long requests = doc.getLong("requests");
                    
                    // Store statistics for later use
                    currentPostViews = views != null ? views.intValue() : 0;
                    currentPostRequests = requests != null ? requests.intValue() : 0;
                    
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to load post statistics");
                }
            });
    }
    
    private int currentPostViews = 0;
    private int currentPostRequests = 0;
    
    public int getCurrentPostViews() {
        return currentPostViews;
    }
    
    public int getCurrentPostRequests() {
        return currentPostRequests;
    }

    // Interface for user fetch callbacks
    public interface OnUserFetchListener {
        void onSuccess(User user);
        void onError(Exception e);
    }

    // Interface for posts fetch callbacks
    public interface OnPostsFetchListener {
        void onSuccess(List<FoodPost> posts);
        void onError(Exception e);
    }

    // Get user by ID
    public void getUserById(String userId, OnUserFetchListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onError(new IllegalArgumentException("User ID cannot be null or empty"));
            return;
        }

        usersRef.document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot doc = task.getResult();
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setId(doc.getId());
                            listener.onSuccess(user);
                        } else {
                            listener.onError(new Exception("Failed to parse user data"));
                        }
                    } else {
                        String error = "User not found";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        listener.onError(new Exception(error));
                    }
                });
    }

    // Get posts by user ID
    public void getPostsByUserId(String userId, OnPostsFetchListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onError(new IllegalArgumentException("User ID cannot be null or empty"));
            return;
        }

        Log.d(TAG, "Fetching posts for user ID: " + userId);
        
        // Try different field names that might be used for user ID
        postsRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<FoodPost> posts = new ArrayList<>();
                        Log.d(TAG, "Query successful, found " + task.getResult().size() + " documents for userId: " + userId);
                        
                        for (DocumentSnapshot doc : task.getResult()) {
                            Log.d(TAG, "Processing document: " + doc.getId());
                            Log.d(TAG, "Document userId field: " + doc.getString("userId"));
                            
                            FoodPost post = doc.toObject(FoodPost.class);
                            if (post != null) {
                                post.setId(doc.getId());
                                posts.add(post);
                                Log.d(TAG, "Successfully added post: " + post.getTitle() + " (ID: " + doc.getId() + ")");
                            } else {
                                Log.w(TAG, "Failed to parse post document: " + doc.getId());
                            }
                        }
                        Log.d(TAG, "Successfully parsed " + posts.size() + " posts");
                        listener.onSuccess(posts);
                    } else {
                        Log.w(TAG, "First query failed, trying alternative field names");
                        // Try alternative field names
                        tryAlternativeUserFields(userId, listener);
                    }
                });
    }

    private void tryAlternativeUserFields(String userId, OnPostsFetchListener listener) {
        // Try with different field names
        postsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<FoodPost> posts = new ArrayList<>();
                        Log.d(TAG, "Alternative query successful, checking " + task.getResult().size() + " documents");
                        
                        for (DocumentSnapshot doc : task.getResult()) {
                            String docUserId = doc.getString("userId");
                            String docCreatorId = doc.getString("creatorId");
                            String docAuthorId = doc.getString("authorId");
                            
                            Log.d(TAG, "Document " + doc.getId() + " - userId: " + docUserId + ", creatorId: " + docCreatorId + ", authorId: " + docAuthorId);
                            
                            if (userId.equals(docUserId) || userId.equals(docCreatorId) || userId.equals(docAuthorId)) {
                                FoodPost post = doc.toObject(FoodPost.class);
                                if (post != null) {
                                    post.setId(doc.getId());
                                    posts.add(post);
                                    Log.d(TAG, "Found matching post: " + post.getTitle());
                                }
                            }
                        }
                        Log.d(TAG, "Found " + posts.size() + " posts with alternative field matching");
                        listener.onSuccess(posts);
                    } else {
                        String error = "Failed to load user posts";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        Log.e(TAG, error);
                        listener.onError(new Exception(error));
                    }
                });
    }

} 