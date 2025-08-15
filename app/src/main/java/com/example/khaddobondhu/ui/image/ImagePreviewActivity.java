package com.example.khaddobondhu.ui.image;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;

public class ImagePreviewActivity extends AppCompatActivity {
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 3.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imageView = findViewById(R.id.previewImageView);
        
        // Get image URL from intent
        String imageUrl = getIntent().getStringExtra("image_url");
        String imageTitle = getIntent().getStringExtra("image_title");
        
        if (imageTitle != null) {
            setTitle(imageTitle);
        }

        // Load image with Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(imageView);
        }

        // Setup scale gesture detector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Setup close button click listener
        findViewById(R.id.closeButton).setOnClickListener(v -> finish());

        // Setup click listener to close activity
        imageView.setOnClickListener(v -> finish());

        // Setup double tap to zoom
        imageView.setOnTouchListener(new View.OnTouchListener() {
            private long lastTouchTime = 0;
            private static final long DOUBLE_TAP_TIME_DELTA = 300;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long touchTime = System.currentTimeMillis();
                    if (touchTime - lastTouchTime < DOUBLE_TAP_TIME_DELTA) {
                        // Double tap detected
                        if (scaleFactor > 1.0f) {
                            // Reset zoom
                            scaleFactor = 1.0f;
                            imageView.setScaleX(scaleFactor);
                            imageView.setScaleY(scaleFactor);
                        } else {
                            // Zoom in
                            scaleFactor = 2.0f;
                            imageView.setScaleX(scaleFactor);
                            imageView.setScaleY(scaleFactor);
                        }
                        return true;
                    }
                    lastTouchTime = touchTime;
                }
                return true;
            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
            
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
