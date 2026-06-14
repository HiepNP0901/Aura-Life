# Keep Retrofit interfaces
-keep,allowobfuscation interface com.drs.auralife.core.network.FilmAPI

# Keep network DTOs (Gson deserialization)
-keep,allowobfuscation class com.drs.auralife.core.network.model.** { *; }

# Keep Firebase models
-keep,allowobfuscation class com.drs.auralife.core.firebase.model.** { *; }

# Keep Room entities
-keep,allowobfuscation class com.drs.auralife.core.database.entity.** { *; }

# Keep Retrofit
-keep class retrofit2.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
