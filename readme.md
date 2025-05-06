# AuraLife

## Building
1. Install Android Studio and the Android NDK.
[Download Android Studio](https://developer.android.com/studio)
[Learn about the Android SDK](https://developer.android.com/studio/releases/sdk-tools)
2. Run `./gradlew sync` from within the `AuraLife/` directory.  
3. Edit the `local.properties` file in `AuraLife/` to point to your Android SDK and NDK directories. The file should look like this:
    ```
    sdk.dir=your-sdk-directory
    ```
4. Build the APK using Android Studio or Gradle.
