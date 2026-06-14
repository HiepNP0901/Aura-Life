# --- Retrofit ---
-keep,allowobfuscation interface com.drs.auralife.core.network.FilmAPI
-keep,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# --- Gson / Model deserialization ---
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeVisibleTypeAnnotations
-keep class com.drs.auralife.core.network.model.** { *; }
-keep class com.drs.auralife.core.firebase.model.** { *; }
-keep class com.drs.auralife.core.database.entity.** { *; }

# --- Hilt / Dagger ---
-keep class dagger.hilt.internal.** { *; }
-keep class dagger.hilt.android.internal.lifecycle.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class * extends dagger.hilt.android.internal.builders.ViewComponentBuilder { *; }
-keep class dagger.hilt.android.AndroidEntryPoint { *; }
-keep class dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends dagger.hilt.android.components.ViewModelComponent { *; }
-keep class javax.inject.** { *; }

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# --- R8 full mode ---
-keepclassmembernames class * {
    java.lang.Class class$;
    java.lang.Class class$(java.lang.String);
}
