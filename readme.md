# Online film application - Aura Life

## Pages
### Start
- Splash: Wait 2 seconds then switch to onboarding if you have not pressed the button to start here, otherwise switch to main
- Onboarding: 3 pages, the last page has a button to switch to main
### Main
- Main: Bottom navigation bar, viewpage2, drawer
- Drawer: Displays the user's avatar and email at the top, followed by the login/logout button (depending on login status) and the exit button
### Home
- HomeFragment: The top is the App Bar containing the search button and user avatar (opening Drawer), followed by ViewPager2 to show 3 banners, the bottom is RecyclerView showing a new movie list (loading more when pulled to the end)
### Library
- LibraryFragment: Displays a RecyclerView showing a list of user libraries, long click to show an edit library dialog, click to open LibraryActivity
- LibraryDetailsActivity: Displays a RecyclerView showing a list of films in a library, long click to show an edit film dialog, click to open FilmDetailsActivity
### Explore
- ExploreFragment: Display ScrollView showing list of movies by genre, click name genre to open ExploreDetailsActivity, click film to open FilmDetailsActivity
- ExploreDetailsActivity: Displays a RecyclerView showing a list of movies by genre, click film to open FilmDetailsActivity
### History
- HistoryFragment: Displays a RecyclerView showing a list of films in a history, long click to show an delete film dialog, click to open FilmDetailsActivity
### Film
- FilmDetailsActivity: Film information, trailer view button (opening Youtube), play film button (opens PlayFilmActivity) and add film to library button
- PlayFilmActivity: Plays film using M3U8 link with Media3 Exoplayer, retains the playback status when rotating the screen using onSaveInstanceState and onRestoreInstanceState
### Authentication
- LoginActivity: Allows users to log in using Firebase Authentication (Email and Password)
- RegisterActivity: Allows users to register using Firebase Authentication, sends an email confirmation, and navigates to the login page after successful registration
