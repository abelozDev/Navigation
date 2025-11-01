# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Ktor and OkHttp ProGuard rules
# These rules are for local library build

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