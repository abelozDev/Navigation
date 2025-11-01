# Ktor and OkHttp ProGuard rules
# These rules are consumed by apps that use this library

# Keep Ktor classes
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep OkHttp classes (required by Ktor)
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep OkHttp internal classes that are needed by Ktor SSE
-keep class okhttp3.internal.** { *; }
-keep interface okhttp3.internal.** { *; }

# Suppress warnings for optional OkHttp dependencies
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Keep annotations for data classes used with Ktor
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
