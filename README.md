# KhaddoBondhu 🍱

A mobile application that connects people with surplus food (from homes, restaurants, or events) to those in need. Users can list or request nearby leftover food, helping reduce food waste and fight hunger in the community.

## 🌟 Project Overview

KhaddoBondhu is an Android application built with Java that serves as a bridge between food donors and recipients. The app features a modern Material Design interface with real-time search capabilities, user role management, seamless food sharing functionality, and comprehensive user profile viewing.

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

## Recent Updates (v2.2)

### 🎨 UI/UX Theme Transformation
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
