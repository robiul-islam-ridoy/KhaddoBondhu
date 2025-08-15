package com.example.khaddobondhu.ui.view;

import android.content.Context;
import android.content.Intent;
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
import com.example.khaddobondhu.ui.image.ImageCarouselActivity;
import com.example.khaddobondhu.ui.image.ImagePreviewActivity;
import java.util.ArrayList;
import java.util.List;

public class ImageCollageView extends FrameLayout {
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    private ImageView imageView7, imageView8, imageView9, imageView10;
    private View overlayView;
    private TextView overlayText;
    private LinearLayout twoImagesLayout, threeImagesLayout, fourImagesLayout;
    private List<String> imageUrls;
    private String title;

    public ImageCollageView(@NonNull Context context) {
        super(context);
        init();
    }

    public ImageCollageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCollageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_image_collage, this, true);
        
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
        
        setOnClickListener(v -> handleImageClick());
    }

    public void setImages(List<String> imageUrls, String title) {
        this.imageUrls = imageUrls;
        this.title = title;
        
        if (imageUrls == null || imageUrls.isEmpty()) {
            // No images - show placeholder
            showSingleImage(R.drawable.placeholder_food);
            return;
        }
        
        int imageCount = imageUrls.size();
        
        if (imageCount == 1) {
            // Single image
            showSingleImage(imageUrls.get(0));
        } else if (imageCount == 2) {
            // Two images side by side
            showTwoImages(imageUrls.get(0), imageUrls.get(1));
        } else if (imageCount == 3) {
            // Three images: 2 on top, 1 on bottom
            showThreeImages(imageUrls.get(0), imageUrls.get(1), imageUrls.get(2));
        } else {
            // Four images: 2x2 grid
            showFourImages(imageUrls.get(0), imageUrls.get(1), imageUrls.get(2), imageUrls.get(3));
        }
    }

    private void showSingleImage(String imageUrl) {
        // Hide all layouts except single image
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView1);
    }

    private void showSingleImage(int drawableRes) {
        // Hide all layouts except single image
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        imageView1.setImageResource(drawableRes);
    }

    private void showTwoImages(String imageUrl1, String imageUrl2) {
        // Hide all layouts except two images
        twoImagesLayout.setVisibility(VISIBLE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        Glide.with(this)
                .load(imageUrl1)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView2);
                
        Glide.with(this)
                .load(imageUrl2)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView3);
    }

    private void showThreeImages(String imageUrl1, String imageUrl2, String imageUrl3) {
        // Hide all layouts except three images
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(VISIBLE);
        fourImagesLayout.setVisibility(GONE);
        overlayView.setVisibility(GONE);
        
        Glide.with(this)
                .load(imageUrl1)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView4);
                
        Glide.with(this)
                .load(imageUrl2)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView5);
                
        Glide.with(this)
                .load(imageUrl3)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView6);
    }

    private void showFourImages(String imageUrl1, String imageUrl2, String imageUrl3, String imageUrl4) {
        // Hide all layouts except four images
        twoImagesLayout.setVisibility(GONE);
        threeImagesLayout.setVisibility(GONE);
        fourImagesLayout.setVisibility(VISIBLE);
        overlayView.setVisibility(VISIBLE);
        overlayText.setText("+1");
        
        Glide.with(this)
                .load(imageUrl1)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView7);
                
        Glide.with(this)
                .load(imageUrl2)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView8);
                
        Glide.with(this)
                .load(imageUrl3)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView9);
                
        Glide.with(this)
                .load(imageUrl4)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(imageView10);
    }

    private void handleImageClick() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        
        if (imageUrls.size() == 1) {
            // Single image - use ImagePreviewActivity
            Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
            intent.putExtra("image_url", imageUrls.get(0));
            intent.putExtra("image_title", title);
            getContext().startActivity(intent);
        } else {
            // Multiple images - use ImageCarouselActivity
            Intent intent = new Intent(getContext(), ImageCarouselActivity.class);
            intent.putStringArrayListExtra("image_urls", new ArrayList<>(imageUrls));
            intent.putExtra("initial_position", 0);
            intent.putExtra("title", title);
            getContext().startActivity(intent);
        }
    }
}
