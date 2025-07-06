<<<<<<< HEAD
<<<<<<< HEAD
# KhaddoBondhu

About
KhaddoBondhu 🍱 – Leftover Food Sharing App. A mobile application that connects people with surplus food (from homes, restaurants, or events) to those in need. Users can list or request nearby leftover food, helping reduce food waste and fight hunger in the community.
=======
# KhaddoBondhu
>>>>>>> 9119bef (Price position fixed)
=======
# KhaddoBondhu 🍱

A mobile application that connects people with surplus food (from homes, restaurants, or events) to those in need. Users can list or request nearby leftover food, helping reduce food waste and fight hunger in the community.

## Setup Instructions

### Prerequisites
- Android Studio
- Firebase Project
- Cloudinary Account

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/robiul-islam-ridoy/KhaddoBondhu.git
   cd KhaddoBondhu
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name: `com.example.khaddobondhu`
   - Download `google-services.json` and place it in the `app/` directory
   - **OR** copy `app/google-services.json.template` to `app/google-services.json` and fill in your Firebase project details
   - Enable Authentication, Firestore, and Storage in Firebase Console

3. **Configure Cloudinary**
   - Create a Cloudinary account at [Cloudinary](https://cloudinary.com/)
   - Copy `local.properties.template` to `local.properties`
   - Fill in your Cloudinary credentials:
   ```properties
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_api_key
   CLOUDINARY_API_SECRET=your_api_secret
   ```

4. **Build and Run**
   ```bash
   ./gradlew build
   ```

## Template Files

### For Team Members:
- Copy `local.properties.template` to `local.properties` and fill in your credentials
- Copy `app/google-services.json.template` to `app/google-services.json` and fill in your Firebase project details

### Template Structure:
```
📁 KhaddoBondhu/
├── 📄 local.properties.template          # Copy to local.properties
├── 📁 app/
│   ├── 📄 google-services.json.template  # Copy to google-services.json
│   └── 📄 google-services.json          # Your actual Firebase config (not committed)
└── 📄 local.properties                  # Your actual credentials (not committed)
```

## Security Notes

- Never commit `local.properties` or `google-services.json` to version control
- These files are already in `.gitignore`
- Use environment variables for sensitive data in production
- Share credentials privately with team members

## Features

- ✅ User Authentication (Email/Password + Anonymous)
- ✅ Food Post Management (Create, Edit, Delete)
- ✅ Image Upload with Cloudinary
- ✅ Real-time Database with Firestore
- ✅ User Profile Management
- ✅ Search and Filter Posts
- ✅ Post Statistics and Analytics
- ✅ Modern Material Design UI

## Tech Stack

- **Language:** Java
- **Backend:** Firebase (Auth, Firestore)
- **Image Storage:** Cloudinary
- **UI:** Material Design Components
- **Image Loading:** Glide
- **Maps:** Google Maps API

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

This project is licensed under the MIT License.
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
