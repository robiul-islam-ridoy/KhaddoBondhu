package com.example.khaddobondhu.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.utils.UserRoleUtils;

public class UserTypeBadgeView extends LinearLayout {
    private ImageView iconImageView;
    private String userType;
    private PopupWindow tooltipPopup;
    private TextView tooltipTextView;

    public UserTypeBadgeView(@NonNull Context context) {
        super(context);
        init();
    }

    public UserTypeBadgeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserTypeBadgeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        
        // Create icon image view
        iconImageView = new ImageView(getContext());
        iconImageView.setLayoutParams(new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ));
        iconImageView.setPadding(2, 2, 2, 2);
        
        addView(iconImageView);
        
        // Setup click listener for tooltip
        setOnClickListener(v -> showTooltip());
        
        // Setup long click listener for tooltip
        setOnLongClickListener(v -> {
            showTooltip();
            return true;
        });
    }

    public void setUserType(String userType) {
        this.userType = userType;
        updateIcon();
    }

    private void updateIcon() {
        if (userType == null) {
            userType = "INDIVIDUAL";
        }
        
        int iconResId = UserRoleUtils.getUserTypeIconDrawable(userType);
        Drawable icon = ContextCompat.getDrawable(getContext(), iconResId);
        iconImageView.setImageDrawable(icon);
    }

    private void showTooltip() {
        if (tooltipPopup != null && tooltipPopup.isShowing()) {
            tooltipPopup.dismiss();
            return;
        }

        // Create tooltip text
        String tooltipText = UserRoleUtils.getUserTypeDisplayName(userType);
        
        // Create tooltip view
        tooltipTextView = new TextView(getContext());
        tooltipTextView.setText(tooltipText);
        tooltipTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        tooltipTextView.setBackgroundResource(R.drawable.tooltip_background);
        tooltipTextView.setPadding(16, 8, 16, 8);
        tooltipTextView.setTextSize(12);
        
        // Create popup window
        tooltipPopup = new PopupWindow(
            tooltipTextView,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        tooltipPopup.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.color.transparent));
        tooltipPopup.setElevation(8);
        tooltipPopup.setOutsideTouchable(true);
        
        // Show tooltip above the badge
        int[] location = new int[2];
        getLocationOnScreen(location);
        
        tooltipPopup.showAsDropDown(this, 0, -tooltipTextView.getHeight() - 8);
        
        // Auto-dismiss after 2 seconds
        postDelayed(() -> {
            if (tooltipPopup != null && tooltipPopup.isShowing()) {
                tooltipPopup.dismiss();
            }
        }, 2000);
    }

    public String getUserType() {
        return userType;
    }
}
