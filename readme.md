# Online film application - Aura Life

## Pages
- Splash: Wait 2 seconds then switch to onboarding if you have not pressed the button to start here, otherwise switch to main
- Onboarding: 3 pages, the last page has a button to switch to main
- Main: bottom navigation bar, viewpage2, drawer
- HomeFragment: The top is the App Bar containing the search button and user avatar (opening Drawer), followed by ViewPager2 to show 3 banners, the bottom is RecyclerView showing a new movie list (loading more when pulled to the end)
- FilmDetailsActivity: Movie information and trailer view button (opening Youtube) and movie button (open PlayFilmActivity)
- PlayFilmActivity: Run Link Film M3U8 with Media3 Exoplayer, retain the status of the page when rotating the screen with onsaveInstancestate, onrestoreinstancestate
- Drawer: Above is the avatar and email of the user, below is the login button or the logout button (if logged in) and the exit button
- LoginActivity: Log in with Firebase (Email and Password)
- RegisterActivity: Register by Firebase, send an email confirmation of registration, bring the email school to the login page after successful registration