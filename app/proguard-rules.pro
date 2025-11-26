# --- KTOR SPECIFIC RULES ---
# Ktor uses Java Management classes for debugging on JVM.
# These don't exist on Android, so we tell R8 to ignore the warnings.
-dontwarn java.lang.management.**
-dontwarn java.lang.instrument.**

# Keep Ktor internal classes to prevent runtime crashes due to reflection
-keep class io.ktor.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# --- KOTLIN SERIALIZATION ---
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}

# --- DATA MODELS ---
# Keep your data classes so they aren't renamed (crucial for JSON parsing)
-keep class com.shuham.urbaneats.data.remote.dto.** { *; }
-keep class com.shuham.urbaneats.domain.model.** { *; }

# --- ROOM DATABASE ---
-keep class androidx.room.paging.** { *; }
-dontwarn androidx.room.paging.**

# --- COROUTINES ---
-dontwarn kotlinx.coroutines.**