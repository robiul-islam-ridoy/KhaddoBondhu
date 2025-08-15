package com.example.khaddobondhu.ui.image;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import java.util.List;

public class ImageCarouselActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView imageCounter;
    private ImageCarouselAdapter adapter;
    private List<String> imageUrls;
    private int initialPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_carousel);

        viewPager = findViewById(R.id.viewPager);
        imageCounter = findViewById(R.id.imageCounter);

        // Get data from intent
        imageUrls = getIntent().getStringArrayListExtra("image_urls");
        initialPosition = getIntent().getIntExtra("initial_position", 0);
        String title = getIntent().getStringExtra("title");

        if (title != null) {
            setTitle(title);
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            setupViewPager();
            updateImageCounter(initialPosition + 1);
        }

        // Setup close button
        findViewById(R.id.closeButton).setOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        adapter = new ImageCarouselAdapter(imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initialPosition, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateImageCounter(position + 1);
            }
        });
    }

    private void updateImageCounter(int currentPosition) {
        imageCounter.setText(currentPosition + " / " + imageUrls.size());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
