# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-verbose

# Retain some boilerplate attributes that are used across various libraries.
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes LineNumberTable
-keepattributes LocalVariableTable
-keepattributes LocalVariableTypeTable
-keepattributes Signature
-keepattributes SourceFile

# For using GSON
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# For Room Database
-keep class com.apps.motivasiapp.model.** { *; }
-keep class com.apps.motivasiapp.data.** { *; }

# Keep all Kotlin classes for reflection
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# AndroidX / Jetpack
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }

# Application specific classes
-keep class com.apps.motivasiapp.** { *; }
-keepclassmembers class com.apps.motivasiapp.** {
    *;
}

# Keep all enum members
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes
-keep class com.apps.motivasiapp.model.** {
    *;
}

# Preserve line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
