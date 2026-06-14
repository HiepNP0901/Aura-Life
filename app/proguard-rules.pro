# --- Retrofit ---
# Keep service interface (created via dynamic proxy)
-keep,allowobfuscation interface com.drs.auralife.core.network.FilmAPI
# Keep methods with Retrofit annotations
-keep,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}

# --- Gson / Model deserialization ---
-keepattributes Signature, RuntimeVisibleAnnotations
-keep,allowobfuscation class com.drs.auralife.core.network.model.** { *; }
-keep,allowobfuscation class com.drs.auralife.core.firebase.model.** { *; }
-keep,allowobfuscation class com.drs.auralife.core.database.entity.** { *; }

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# --- R8 full mode helpers ---
-keepclassmembernames class * {
    java.lang.Class class$;
    java.lang.Class class$(java.lang.String);
}
