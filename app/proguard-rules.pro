# Keep interface
-keep interface com.drs.auralife.data.FilmAPI

# Keep the classes of Model
-keep class com.drs.auralife.data.model.** { *; }
-keep class com.drs.auralife.data.firebase.library.Library

# Keep the classes of Retrofit
-keep class retrofit2.** { *; }

# Keep the classes of Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep the classes of Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
