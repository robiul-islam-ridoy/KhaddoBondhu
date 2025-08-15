# KhaddoBondhu 🍱

A mobile application that connects people with surplus food (from homes, restaurants, or events) to those in need. Users can list or request nearby leftover food, helping reduce food waste and fight hunger in the community.

## 🌟 Project Overview

KhaddoBondhu is an Android application built with Java that serves as a bridge between food donors and recipients. The app features a modern Material Design interface with real-time search capabilities, user role management, seamless food sharing functionality, comprehensive user profile viewing, and beautiful image collage displays.

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Java Development Kit (JDK) 8 or higher
- Firebase Project
- Cloudinary Account
- Google Maps API Key

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/robiul-islam-ridoy/KhaddoBondhu.git
   cd KhaddoBondhu
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication, Firestore, and Storage in Firebase Console
   - Configure Authentication methods (Email/Password, Anonymous)

3. **Configure Cloudinary**
   - Create a Cloudinary account at [Cloudinary](https://cloudinary.com/)
   - Copy `local.properties.template` to `local.properties`
   - Fill in your Cloudinary credentials:
   ```properties
   sdk.dir=C\:\\Android_sdk
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_api_key
   CLOUDINARY_API_SECRET=your_api_secret
   MAPS_API_KEY=your_google_maps_api_key
   ```

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## Security Notes

- Never commit `local.properties` or `google-services.json` to version control
- These files are already in `.gitignore`
- Use environment variables for sensitive data in production
- Implement proper input validation and sanitization

## Features

### Core Features
- ✅ **User Authentication**: Email/Password + Anonymous authentication
- ✅ **Food Post Management**: Create, edit, and delete food posts
- ✅ **Image Upload**: Cloudinary integration for efficient image storage
- ✅ **Real-time Database**: Firestore for live data synchronization
- ✅ **User Profile Management**: Comprehensive user profiles with statistics
- ✅ **Post Statistics**: Analytics and insights for food posts
- ✅ **Modern UI**: Material Design Components throughout the app

### 🆕 Enhanced Search & Discovery (v2.0)
- ✅ **Advanced User Search**: Search through all users (Individuals, Restaurants, NGOs)
- ✅ **Real-time Search**: Instant search results as you type (after 2 characters)
- ✅ **Smart Filtering**: Multi-choice filter by user types (Individual, Restaurant, NGO)
- ✅ **Large Card Display**: Beautiful large cards for search results with user details
- ✅ **Smooth Scrolling**: Optimized scrolling for both search results and default content
- ✅ **Pull-to-Refresh**: Refresh content by pulling down (only in content area)
- ✅ **Search Results Count**: Shows number of results found with search query
- ✅ **Empty State Handling**: Proper messaging when no results are found

### 🆕 User Profile View System (v2.1)
- ✅ **Read-Only Profile View**: View any user's profile without editing capabilities
- ✅ **Dynamic Posts Loading**: Load and display user's actual posts from Firebase
- ✅ **Circular Profile Pictures**: Perfect circular profile images with proper styling
- ✅ **User Type Badges**: Display user type (Individual, Restaurant, NGO) with colored badges
- ✅ **Dynamic Statistics**: Real-time stats showing posts count, total views, and ratings
- ✅ **Clickable Posts**: View detailed information of any post by clicking on it
- ✅ **View-Only Interface**: No edit or delete buttons - purely for viewing
- ✅ **Responsive Design**: Matches the main profile page design exactly
- ✅ **Comprehensive User Info**: Display name, email, phone, bio, and user type

### 🆕 Beautiful Image Collage System (v3.0)
- ✅ **Dynamic Image Collages**: Automatic collage layout based on number of images (1, 2, 3, 4+)
- ✅ **Smart Layout Management**:
  - **1 image**: Full-size single image display
  - **2 images**: Side-by-side layout with margins
  - **3 images**: 2 on top, 1 on bottom with proper spacing
  - **4+ images**: 2x2 grid with overlay showing additional count
- ✅ **Universal Implementation**: Image collages work everywhere food posts are displayed
- ✅ **Click-to-View**: Tap any collage to open full image preview or carousel
- ✅ **Performance Optimized**: Efficient image loading with Glide and proper memory management
- ✅ **Visual Polish**: Small margins between images for professional appearance
- ✅ **Responsive Design**: Collages adapt to different screen sizes and orientations

### 🆕 Editable Image Collage System (v3.1)
- ✅ **Post Edit Enhancement**: Editable image collages in post edit screen
- ✅ **Individual Image Removal**: Click any image to remove with confirmation dialog
- ✅ **Add New Images**: Floating action button (+) to add more images (max 4)
- ✅ **Mixed Image Support**: Handle both existing URLs and new URIs simultaneously
- ✅ **Immediate Preview**: New images appear instantly in collage when selected
- ✅ **Smart Save System**: Properly manages image uploads, deletions, and updates
- ✅ **Visual Feedback**: Red X buttons on images for clear removal indication
- ✅ **Confirmation Dialogs**: Prevents accidental image deletion
- ✅ **Proper Index Management**: Correct handling of image positions and removals

### 🆕 Streamlined Logout System (v3.2)
- ✅ **Removed 3-Dot Menu**: Cleaned up top toolbar by removing 3-dot menu from entire app
- ✅ **Profile-Based Logout**: Logout button added to user profile page next to Edit Profile button
- ✅ **Logout Icon**: Professional logout icon with consistent Material Design styling
- ✅ **Confirmation Dialog**: Prevents accidental logout with confirmation prompt
- ✅ **Clean Navigation**: Users navigate to Profile tab → tap Logout button → confirm → logout
- ✅ **Consistent UI**: Logout button matches Edit Profile button design and layout

### User Interface Improvements
- ✅ **Slim Search Bar**: Modern, compact search interface with rounded corners
- ✅ **Icon Visibility**: Clear filter and search icons with proper tinting
- ✅ **Responsive Layout**: Proper space allocation between search and content
- ✅ **Performance Optimized**: Enhanced RecyclerView configuration for smooth scrolling
- ✅ **Layout Restructuring**: FrameLayout implementation for proper content switching
- ✅ **Profile Picture Styling**: Circular images with proper borders and backgrounds

## Tech Stack

- **Language:** Java
- **Backend:** Firebase (Authentication, Firestore, Storage)
- **Image Storage:** Cloudinary
- **UI Framework:** Material Design Components
- **Image Loading:** Glide
- **Maps:** Google Maps API
- **Search:** Custom search implementation with real-time filtering
- **Build System:** Gradle

## Recent Updates (v3.2)

### 🚪 Streamlined Logout System (v3.2)
1. **3-Dot Menu Removal**: 
   - Removed `onCreateOptionsMenu()` and `onOptionsItemSelected()` methods from MainActivity
   - Eliminated menu inflation and handling from ProfileFragment
   - Deleted unused menu files (`main_menu.xml`, `profile_menu.xml`)
   - Cleaned up unused imports (Menu, MenuInflater, MenuItem)

2. **Profile-Based Logout Implementation**: 
   - Added logout button to ProfileFragment layout next to Edit Profile button
   - Implemented side-by-side button layout with equal weight and proper margins
   - Used existing `ic_logout.xml` drawable for consistent iconography
   - Added click listener for logout functionality with confirmation dialog

3. **Enhanced User Experience**: 
   - **Clean Interface**: Removed cluttered 3-dot menu from top toolbar
   - **Intuitive Navigation**: Users naturally go to Profile tab for account actions
   - **Visual Consistency**: Logout button matches Edit Profile button styling
   - **Safety First**: Confirmation dialog prevents accidental logout
   - **Professional Design**: Material Design buttons with proper spacing and icons

4. **Technical Improvements**: 
   - **Code Cleanup**: Removed unused menu-related code and resources
   - **Memory Optimization**: Eliminated unnecessary menu inflation overhead
   - **Maintainability**: Simplified navigation structure and user flow
   - **Consistency**: Unified logout experience across the entire application

### 🖼️ Editable Image Collage System (v3.1)
1. **EditableImageCollageView Implementation**: 
   - Created `EditableImageCollageView` extending FrameLayout for editable collage functionality
   - Enhanced with `OnImageRemoveListener` interface for both existing and new image removal
   - Supports mixed image types (URLs for existing, URIs for new images)
   - Proper index management for accurate image removal

2. **Post Edit Enhancement**: 
   - Updated `EditPostActivity` to use `EditableImageCollageView` instead of single ImageView
   - Implemented separate lists for `currentImageUrls`, `newImageUris`, and `imagesToDelete`
   - Added floating action button (+) for adding new images with 4-image limit
   - Enhanced UI with "Tap images to remove • Tap + to add more" instruction text

3. **Smart Image Management**: 
   - **Existing Images**: Loaded from Firebase and displayed as collages
   - **New Images**: Selected from gallery and immediately previewed in collage
   - **Removal System**: Individual image removal with confirmation dialogs
   - **Mixed Display**: Both existing and new images shown together seamlessly

4. **Enhanced User Experience**: 
   - Immediate preview when new images are selected
   - Red X buttons on images for clear visual feedback
   - Confirmation dialogs prevent accidental deletions
   - Proper navigation - stays in edit mode when removing images
   - Re-selection capability after removing wrong images

5. **Robust Save System**: 
   - **Delete Phase**: Removes deleted images from Cloudinary
   - **Upload Phase**: Uploads new images to Cloudinary
   - **Update Phase**: Updates post data with final combined image list
   - **Error Handling**: Proper error management and user feedback

6. **Visual Enhancements**: 
   - Created `view_editable_image_collage.xml` with remove button overlays
   - Added `circle_red_background.xml` for red circular remove buttons
   - Created `ic_add.xml` and `ic_close.xml` vector drawables
   - Updated `activity_edit_post.xml` with new collage view and improved layout

### 🖼️ Beautiful Image Collage System (v3.0)
1. **Custom ImageCollageView**: Created a reusable custom view for dynamic image collage layouts
2. **Smart Layout Detection**: Automatically detects number of images and applies appropriate layout
3. **Universal Integration**: Implemented across all food post displays:
   - Main feed (FoodPostAdapter)
   - Post detail view (PostDetailActivity)
   - User profile posts (UserPostAdapter)
4. **Enhanced User Experience**:
   - Click any collage to view images in full screen
   - Single images open in ImagePreviewActivity
   - Multiple images open in ImageCarouselActivity
5. **Visual Improvements**:
   - Small 2dp margins between images for clean separation
   - Proper overlay for 4+ images showing additional count
   - Consistent styling across all collage layouts
6. **Performance & Reliability**:
   - Replaced complex GridLayout with reliable LinearLayout approach
   - Fixed crash issues with proper layout management
   - Optimized image loading with Glide integration

### 🖼️ Comprehensive Image Preview System (v2.9)
1. **Universal Image Preview**: Complete image preview functionality across the entire app
2. **Fixed Close Button**: Working X button in image preview for proper navigation
3. **Original Size Display**: Images show at their natural size with centerInside scale type
4. **Explore Page Integration**: All profile pictures in explore page now previewable
5. **User Profile View Enhancement**: Profile pictures in UserProfileViewActivity now clickable
6. **Consistent Experience**: Same preview system used everywhere for uniformity

### 📸 Profile Photo Preview & Enhancement (v2.6)
1. **Immediate Photo Preview**: Profile photo preview appears instantly when user selects a new image
2. **Enhanced Visual Design**: Added 3dp padding for cleaner profile picture appearance
3. **Reliable Preview System**: Direct ImageView reference approach for consistent functionality
4. **Memory Management**: Proper cleanup when dialog is dismissed to prevent memory leaks
5. **Improved User Experience**: Instant visual feedback with professional circular crop

### 🔍 Explore Page Name-Only Search (v2.4)
1. **Name-Based Search**: Explore page now searches only by user names by default
2. **Advanced Filtering**: When user types are selected, search combines name + user type filtering
3. **Simplified Search Logic**: Removed search in description, email, and user type fields
4. **Enhanced User Experience**: Cleaner, more focused search results
5. **Intelligent Filtering**: User type filters work in combination with name search

### 🔍 Smart Search & Filter System (v2.3)
1. **Context-Aware Filtering**: Dynamic filter options based on post type selection
2. **Manual Search Control**: Search triggered only on Enter key or search icon click (no auto-search)
3. **Clean Home Interface**: Removed toolbar search icon, kept main search bar on home page
4. **Intelligent Filter Hiding**: Automatically hides irrelevant filter options based on post type
5. **Enhanced User Experience**: Streamlined navigation with category-based post browsing

### 🎨 UI/UX Theme Transformation (v2.2)
1. **Beautiful Gradient Toolbars**: Implemented stunning aqua/blue gradient backgrounds across all app screens
2. **Static App Branding**: Replaced dynamic page titles with consistent "KhaddoBondhu" branding
3. **Centered Logo Design**: Modern, centered app name with professional typography
4. **Enhanced Bottom Navigation**: Gradient-colored selected items for better visual feedback
5. **Consistent Design Language**: Unified gradient theme matching login/signup page aesthetics

### User Profile View Implementation (v2.1)
1. **New Activity**: `UserProfileViewActivity` for read-only profile viewing
2. **Enhanced Firebase Service**: Added methods for fetching user data and posts by user ID
3. **Dynamic Content Loading**: Real-time loading of user posts and statistics
4. **Circular Image Implementation**: Perfect circular profile pictures with proper styling
5. **View-Only Interface**: Removed edit/delete functionality for external profile viewing

### Key Technical Improvements

#### 🖼️ Beautiful Image Collage System (v3.0)
1. **Custom View Implementation**:
   - Created `ImageCollageView` extending FrameLayout for reusable collage functionality
   - Implemented dynamic layout switching based on image count
   - Used LinearLayout approach for reliable performance and crash prevention

2. **Smart Layout Management**:
   - **Single Image**: Full-size display with proper scaling
   - **Two Images**: Side-by-side layout with 2dp margins for clean separation
   - **Three Images**: 2 on top, 1 on bottom layout with proper spacing
   - **Four+ Images**: 2x2 grid with overlay showing additional image count

3. **Universal Integration**:
   - Updated `FoodPostAdapter` to use ImageCollageView instead of single ImageView
   - Modified `PostDetailActivity` for collage display in detail view
   - Enhanced `UserPostAdapter` for profile post collages
   - Updated all relevant layout files (`item_food_post.xml`, `activity_post_detail.xml`, `item_user_post.xml`)

4. **Enhanced User Interaction**:
   - Click listeners for opening image previews or carousels
   - Single images open in `ImagePreviewActivity` for full-screen viewing
   - Multiple images open in `ImageCarouselActivity` for swipeable gallery
   - Proper title and image URL passing for context

5. **Performance & Reliability**:
   - Fixed crash issues by replacing complex GridLayout with LinearLayout approach
   - Optimized image loading with Glide integration and proper error handling
   - Memory-efficient layout management with proper view visibility control
   - Responsive design that adapts to different screen sizes

#### 🖼️ Comprehensive Image Preview System (v2.9)
1. **Universal Image Preview Implementation**:
   - Added image preview functionality to all explore page adapters (SearchResultsAdapter, RestaurantAdapter, NGOAdapter, IndividualAdapter)
   - Enhanced UserProfileViewActivity with image preview capability
   - Fixed close button functionality in ImagePreviewActivity
   - Changed image scale type to centerInside for original size display

2. **Explore Page Integration**:
   - All user profile pictures in explore page are now clickable for preview
   - Smart handling: only adds click listeners when images exist
   - Proper titles showing user name + "Profile Picture" for context
   - Consistent preview experience across all user types (Restaurant, NGO, Individual)

3. **User Profile View Enhancement**:
   - Profile pictures in UserProfileViewActivity now support image preview
   - Added ImagePreviewActivity import and click listener implementation
   - Proper error handling when no profile picture is available
   - Maintains read-only functionality while adding preview capability

4. **Image Preview Activity Improvements**:
   - Fixed close button (X) click listener for proper navigation
   - Changed scaleType from fitCenter to centerInside for original size display
   - Maintains zoom and pan functionality with proper gesture handling
   - Consistent black background with white close button for professional appearance

#### 📸 Profile Photo Preview & Enhancement (v2.6)
1. **Direct ImageView Reference Implementation**:
   - Store `currentDialogImageView` reference when dialog is created
   - Direct update approach eliminates complex view finding methods
   - Reliable preview functionality with immediate visual feedback

2. **Enhanced Visual Design**:
   - Added 3dp padding to profile picture ImageView in edit dialog
   - Improved visual appearance with subtle spacing
   - Maintains professional circular crop styling

3. **Memory Management & Performance**:
   - Dialog dismiss listener clears ImageView reference
   - Prevents memory leaks and stale references
   - Optimized preview update performance

4. **User Experience Improvements**:
   - Instant preview when user selects a new profile photo
   - No need to save first to see how the photo looks
   - Professional and intuitive editing workflow

#### 🔍 Explore Page Name-Only Search (v2.4)
1. **Simplified Search Implementation**:
   - Modified `matchesSearch()` method to search only by user names
   - Removed search in description, email, and user type fields
   - Cleaner, more focused search results

2. **Advanced Filtering Logic**:
   - When no user type is selected: Search only by name across all users
   - When user type is selected: Filter by user type AND search by name within filtered set
   - Combined filtering through `matchesSearch()` + `matchesFilter()` methods

3. **Enhanced User Experience**:
   - Search hint already shows "Search Here by name"
   - Results count shows appropriate filtering information
   - Maintains existing filter dialog functionality

#### 🔍 Smart Search & Filter System (v2.3)
1. **Context-Aware Filter Implementation**:
   - Dynamic filter options based on selected post category (All Posts, Sell Posts, Donation Posts, Request Posts)
   - Automatic hiding of irrelevant filter options (post type filter hidden for specific categories)
   - Price range filter hidden for donation and request posts (free items)
   - Filter labels properly hidden when corresponding inputs are hidden

2. **Manual Search Control**:
   - Removed automatic search on text change
   - Search triggered only on Enter key press or search icon click
   - Improved user control over search timing and performance

3. **Clean Interface Design**:
   - Removed search icon from toolbar (top bar)
   - Kept main search bar on home page for user convenience
   - Streamlined navigation with category-based post browsing

#### 🎨 UI/UX Theme Enhancements (v2.2)
1. **Gradient Toolbar Implementation**:
   - Created `toolbar_gradient_background.xml` with beautiful aqua/blue gradient
   - Applied gradient backgrounds to all activity toolbars (Main, PostDetail, EditPost, Chat, UserProfileView)
   - Updated color scheme to match gradient theme throughout the app

2. **Static App Branding**:
   - Removed dynamic page titles and implemented consistent "KhaddoBondhu" branding
   - Created `ToolbarTitleStyle` with modern typography (22sp, bold, centered)
   - Eliminated duplicate app names in toolbars

3. **Enhanced Navigation**:
   - Updated bottom navigation with gradient-colored selected items
   - Created `BottomNavigationStyle` and `bottom_nav_color.xml` for consistent theming
   - Improved visual feedback for active navigation items

#### Layout & Performance Improvements (v2.1)
1. **Layout Restructuring**:
   - Used FrameLayout to properly manage search results vs default content
   - Eliminated space competition between different content areas

2. **Enhanced Scrolling**:
   - Fixed scrolling issues and optimized RecyclerView performance
   - Added `setHasFixedSize(false)` and `setItemViewCacheSize(20)`

3. **Real-time Search**:
   - Implemented TextWatcher for instant search results
   - Triggers search after 2 characters for optimal performance

4. **Filter System**:
   - Multi-choice dialog for filtering by user types
   - Supports Individual, Restaurant, and NGO filtering

5. **UI/UX Enhancements**:
   - Slim search bar with proper icon visibility
   - Smooth transitions between search and default content
   - Proper empty state handling

6. **Profile View System**:
   - Circular profile pictures with proper background styling
   - Dynamic posts loading with comprehensive error handling
   - Real-time statistics calculation from actual post data
   - Seamless navigation between explore and profile views

The application has been thoroughly tested for:
- Search functionality with various query types
- Filter system with multiple user types
- Scrolling performance on different screen sizes
- Pull-to-refresh functionality
- UI responsiveness and layout stability
- User profile viewing and post interaction
- Circular image rendering and styling
- Gradient toolbar rendering across all activities
- Static app branding consistency
- Bottom navigation theming and interactions
- Color scheme consistency throughout the app
- Context-aware filtering based on post categories
- Manual search control and performance
- Filter label visibility management
- Category-based post browsing and navigation
- Name-only search functionality on explore page
- Advanced filtering with user type selection
- Combined search and filter logic validation
- Profile photo preview functionality in edit dialog
- ImageView reference management and memory cleanup
- Visual design consistency with padding and styling
- Immediate preview response and user feedback
- **Universal image preview functionality across all app sections**
- **Close button functionality in image preview**
- **Original size image display with proper scaling**
- **Explore page profile picture preview functionality**
- **UserProfileViewActivity image preview integration**
- **Consistent image preview experience across all user types**
- **Proper error handling for missing images**
- **Image preview navigation and gesture handling**
- **Dynamic image collage layouts for multiple images**
- **Collage click functionality for image preview/carousel**
- **Performance optimization and crash prevention**
- **Visual polish with proper margins and spacing**
- **Universal collage implementation across all food post displays**
- **Editable image collage functionality in post edit screen**
- **Individual image removal with proper index management**
- **Mixed image type support (URLs and URIs)**
- **Immediate preview for newly selected images**
- **Confirmation dialogs for image removal**
- **Proper navigation handling during image removal**
- **Re-selection capability after removing images**
- **Robust save system with proper image management**
- **Visual feedback with red X buttons on images**
- **Floating action button for adding new images**
- **Maximum image limit enforcement (4 images)**
- **3-dot menu removal from top toolbar**
- **Profile-based logout button implementation**
- **Logout confirmation dialog functionality**
- **Clean navigation flow for logout process**
- **Consistent button styling and layout**

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Test thoroughly
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Use meaningful commit messages
- Test on multiple Android versions
- Ensure proper error handling
- Document new features

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Mahbob Alam** - Lead Developer, Project Maintainer
- **Robiul Islam Ridoy** - Lead Developer, Project Maintainer
- **Shafayet Islam** - Developer, Project Planner

## 🙏 Acknowledgments

- Firebase for backend services
- Cloudinary for image storage
- Material Design for UI components
- The open-source community for various libraries

---

**Made with ❤️ for reducing food waste and helping communities**
