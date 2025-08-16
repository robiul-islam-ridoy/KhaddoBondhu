package com.example.khaddobondhu.utils;

import android.content.Context;
import com.example.khaddobondhu.Config;
import com.example.khaddobondhu.R;

public class UserRoleUtils {
    
    /**
     * Get the display name for a user type
     */
    public static String getUserTypeDisplayName(String userType) {
        switch (userType) {
            case Config.USER_TYPE_INDIVIDUAL:
                return Config.USER_TYPE_INDIVIDUAL_DISPLAY;
            case Config.USER_TYPE_RESTAURANT:
                return Config.USER_TYPE_RESTAURANT_DISPLAY;
            case Config.USER_TYPE_NGO:
                return Config.USER_TYPE_NGO_DISPLAY;
            default:
                return Config.USER_TYPE_INDIVIDUAL_DISPLAY;
        }
    }
    
    /**
     * Get the description for a user type
     */
    public static String getUserTypeDescription(String userType) {
        switch (userType) {
            case Config.USER_TYPE_INDIVIDUAL:
                return Config.USER_TYPE_INDIVIDUAL_DESC;
            case Config.USER_TYPE_RESTAURANT:
                return Config.USER_TYPE_RESTAURANT_DESC;
            case Config.USER_TYPE_NGO:
                return Config.USER_TYPE_NGO_DESC;
            default:
                return Config.USER_TYPE_INDIVIDUAL_DESC;
        }
    }
    
    /**
     * Get the badge drawable resource for a user type
     */
    public static int getUserTypeBadgeDrawable(String userType) {
        switch (userType) {
            case Config.USER_TYPE_INDIVIDUAL:
                return R.drawable.badge_individual;
            case Config.USER_TYPE_RESTAURANT:
                return R.drawable.badge_restaurant;
            case Config.USER_TYPE_NGO:
                return R.drawable.badge_ngo;
            default:
                return R.drawable.badge_individual;
        }
    }
    
    /**
     * Get the icon drawable resource for a user type
     */
    public static int getUserTypeIconDrawable(String userType) {
        switch (userType) {
            case Config.USER_TYPE_INDIVIDUAL:
                return R.drawable.ic_user_individual;
            case Config.USER_TYPE_RESTAURANT:
                return R.drawable.ic_user_restaurant;
            case Config.USER_TYPE_NGO:
                return R.drawable.ic_user_ngo;
            default:
                return R.drawable.ic_user_individual;
        }
    }
    
    /**
     * Check if user type is valid
     */
    public static boolean isValidUserType(String userType) {
        return userType != null && (
            userType.equals(Config.USER_TYPE_INDIVIDUAL) ||
            userType.equals(Config.USER_TYPE_RESTAURANT) ||
            userType.equals(Config.USER_TYPE_NGO)
        );
    }
    
    /**
     * Get all available user types
     */
    public static String[] getAllUserTypes() {
        return new String[]{
            Config.USER_TYPE_INDIVIDUAL,
            Config.USER_TYPE_RESTAURANT,
            Config.USER_TYPE_NGO
        };
    }
    
    /**
     * Get all available user type display names
     */
    public static String[] getAllUserTypeDisplayNames() {
        return new String[]{
            Config.USER_TYPE_INDIVIDUAL_DISPLAY,
            Config.USER_TYPE_RESTAURANT_DISPLAY,
            Config.USER_TYPE_NGO_DISPLAY
        };
    }
} 