package com.example.khaddobondhu.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import java.util.ArrayList;
import java.util.List;

public class EditableImageCollageView extends FrameLayout {
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    private ImageView imageView7, imageView8, imageView9, imageView10;
    private View overlayView;
    private TextView overlayText;
    private LinearLayout twoImagesLayout, threeImagesLayout, fourImagesLayout;
    private List<String> imageUrls;
    private List<Uri> imageUris;
    private OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onImageRemove(int position, String imageUrl);
        void onNewImageRemove(int position, Uri imageUri);
    }

    public EditableImageCollageView(@NonNull Context context) {
        super(context);
        init();
    }

    public EditableImageCollageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditableImageCollageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_editable_image_collage, this, true);
        
        // Single image view
        imageView1 = findViewById(R.id.imageView1);
        
        // Two images layout
        twoImagesLayout = findViewById(R.id.twoImagesLayout);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        
        // Three images layout
        threeImagesLayout = findViewById(R.id.threeImagesLayout);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        
        // Four images layout
        fourImagesLayout = findViewById(R.id.fourImagesLayout);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageView10 = findViewById(R.id.imageView10);
        
        // Overlay
        overlayView = findViewById(R.id.overlayView);
        overlayText = findViewById(R.id.overlayText);
        
        // Initialize lists
        imageUrls = new ArrayList<>();
        imageUris = new ArrayList<>();
    }

    public void setImages(List<String> imageUrls, List<Uri> imageUris) {
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
        this.imageUris = imageUris != null ? new ArrayList<>(imageUris) : new ArrayList<>();
        
        int totalImages = this.imageUrls.size() + this.imageUris.size();
        
        if (totalImages == 0) {
            showSingleImage(R.drawable.placeholder_food);
            return;
        }
        
        if (totalImages == 1) {
            showSingleImage();
        } else if (totalImages == 2) {
            showTwoImages();
        } else if (totalImages == 3) {
            showThreeImages();
        } else {
            showFourImages();
        }
    }

    public void setImages(List<String> imageUrls) {
        setImages(imageUrls, new ArrayList<>());
    }

    public List<String> getImageUrls() {
        return new ArrayList<>(imageUrls);
    }

    public List<Uri> getImageUris() {
        return new ArrayList<>(imageUris);
    }

    public void setOnImageRemoveListener(OnImageRemoveListener listener) {
        this.removeListener = listener;
    }

    private void showSingleImage() {
        // Hide all layouts except single image
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        if (!imageUrls.isEmpty()) {
            // Show existing image
            Glide.with(this)
                    .load(imageUrls.get(0))
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(imageView1);
            setupRemoveButton(imageView1, 0, true, imageUrls.get(0));
        } else if (!imageUris.isEmpty()) {
            // Show new image
            Glide.with(this)
                    .load(imageUris.get(0))
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(imageView1);
            setupRemoveButton(imageView1, 0, false, imageUris.get(0));
        }
    }

    private void showSingleImage(int drawableRes) {
        // Hide all layouts except single image
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        imageView1.setImageResource(drawableRes);
        imageView1.setOnClickListener(null);
    }

    private void showTwoImages() {
        // Hide all layouts except two images
        twoImagesLayout.setVisibility(VISIBLE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        loadImageToView(imageView2, 0);
        loadImageToView(imageView3, 1);
    }

    private void showThreeImages() {
        // Hide all layouts except three images
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(VISIBLE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        loadImageToView(imageView4, 0);
        loadImageToView(imageView5, 1);
        loadImageToView(imageView6, 2);
    }

    private void showFourImages() {
        // Hide all layouts except four images
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(VISIBLE);
        overlayView.setVisibility(VISIBLE);
        
        int totalImages = imageUrls.size() + imageUris.size();
        overlayText.setText("+" + (totalImages - 4));
        
        loadImageToView(imageView7, 0);
        loadImageToView(imageView8, 1);
        loadImageToView(imageView9, 2);
        loadImageToView(imageView10, 3);
    }

    private void loadImageToView(ImageView imageView, int position) {
        if (position < imageUrls.size()) {
            // Load existing image
            Glide.with(this)
                    .load(imageUrls.get(position))
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(imageView);
            setupRemoveButton(imageView, position, true, imageUrls.get(position));
        } else {
            // Load new image
            int newImageIndex = position - imageUrls.size();
            if (newImageIndex < imageUris.size()) {
                Glide.with(this)
                        .load(imageUris.get(newImageIndex))
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .centerCrop()
                        .into(imageView);
                setupRemoveButton(imageView, newImageIndex, false, imageUris.get(newImageIndex));
            }
        }
    }

    private void setupRemoveButton(ImageView imageView, int position, boolean isExistingImage, Object imageData) {
        imageView.setOnClickListener(v -> {
            if (removeListener != null) {
                if (isExistingImage) {
                    removeListener.onImageRemove(position, (String) imageData);
                } else {
                    removeListener.onNewImageRemove(position, (Uri) imageData);
                }
            }
        });
    }
}
