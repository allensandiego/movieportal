# Movie TV People

A modern, high-performance Android application for exploring the world of cinema and television. Powered by [The Movie Database (TMDB)](https://www.themoviedb.org/) API, this app provides a comprehensive experience for movie enthusiasts and TV show fans alike.

## ✨ Features

- **🎬 Discover Movies & TV Shows:** Browse through popular, top-rated, upcoming, and now-playing titles.
- **👤 Person Profiles:** Explore detailed information about actors, directors, and crew members, including their filmography and biographies.
- **🔍 Detailed Information:** View high-quality posters, backdrops, cast & crew lists, user reviews, and video trailers.
- **📱 Media Viewer:** Full-screen support for viewing movie images and watching trailers directly within the app via YouTube integration.
- **🎨 Modern UI/UX:** Built entirely with Jetpack Compose using Material 3 design principles for a sleek and responsive user experience.
- **⚡ Fast & Efficient:** Optimized image loading and networking for a smooth browsing experience.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Architecture:** MVVM (Model-View-ViewModel) with Repository Pattern
- **API:** [TMDB API](https://developer.themoviedb.org/docs)

## 🚀 Getting Started

To get a local copy up and running, follow these steps:

### Prerequisites

- Android Studio Flamingo or newer
- JDK 11+
- A TMDB API Key (Get one [here](https://www.themoviedb.org/settings/api))

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/movie-tv-people.git
   ```
2. Open the project in Android Studio.
3. Locate `app/src/main/res/values/strings.xml`.
4. Replace the value of `tmdb_api_key` with your own API key:
   ```xml
   <string name="tmdb_api_key">YOUR_API_KEY_HERE</string>
   ```
5. Build and run the app on an emulator or a physical device.

## 📸 Screenshots

*(Add screenshots here once available)*

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [The Movie Database (TMDB)](https://www.themoviedb.org/) for the extensive data and API.
- [Google Android Developer Documentation](https://developer.android.com/) for best practices.
