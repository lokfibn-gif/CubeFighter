# ProGuard Configuration for Cube Fighter
# LibGDX Release Build Optimization

#=========================================
# LibGDX Specific Rules
#=========================================

# Keep all LibGDX classes
-keep class com.badlogic.** { *; }
-keepclassmembers class com.badlogic.** { *; }

# Keep GDX runtime classes
-keep class com.badlogic.gdx.** { *; }
-keepclassmembers class com.badlogic.gdx.** { *; }

# Keep Box2D classes (if used)
-keep class com.badlogic.gdx.physics.box2d.** { *; }
-keepclassmembers class com.badlogic.gdx.physics.box2d.** { *; }

# Keep Scene2D UI classes
-keep class com.badlogic.gdx.scenes.scene2d.** { *; }
-keepclassmembers class com.badlogic.gdx.scenes.scene2d.** { *; }

# Keep JSON serialization classes
-keep class com.badlogic.gdx.utils.Json** { *; }
-keepclassmembers class com.badlogic.gdx.utils.Json** { *; }

# Keep reflection classes (LibGDX uses reflection)
-keep class com.badlogic.gdx.utils.reflect.** { *; }
-keepclassmembers class com.badlogic.gdx.utils.reflect.** { *; }
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

#=========================================
# Game Classes - Keep All Game Logic
#=========================================

# Keep main game package
-keep class com.cubefighter.** { *; }
-keepclassmembers class com.cubefighter.** { *; }

# Keep all entity classes
-keep class com.cubefighter.entities.** { *; }
-keepclassmembers class com.cubefighter.entities.** { *; }

# Keep all screen classes
-keep class com.cubefighter.screens.** { *; }
-keepclassmembers class com.cubefighter.screens.** { *; }

# Keep all system classes
-keep class com.cubefighter.systems.** { *; }
-keepclassmembers class com.cubefighter.systems.** { *; }

#=========================================
# Android Support / AndroidX
#=========================================

-keep class android.support.** { *; }
-keep class androidx.** { *; }
-keepclassmembers class androidx.** { *; }

#=========================================
# General Optimization Settings
#=========================================

# Keep debug information for crash reports
-keepattributes SourceFile,LineNumberTable

# Keep annotations
-keepattributes *Annotation*

# Keep generic signatures for reflection
-keepattributes Signature

# Keep exception handling
-keepattributes Exceptions

# Keep inner classes
-keepattributes InnerClasses

# Keep enclosing method for inner classes
-keepattributes EnclosingMethod

#=========================================
# Optimization Flags
#=========================================

# Enable optimization
-optimize !code/simplification/arithmetic,!field/*,!class/merging/*

# Remove unused code
-dontshrink

# Remove unused resources (handled by Android build)
-android

# Allow optimization of synchronized blocks
-optimizationpasses 5

#=========================================
# Native Methods
#=========================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

#=========================================
# Enum Handling
#=========================================

-keepclassmembers enum com.cubefighter.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#=========================================
# Parcelable Implementation
#=========================================

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

#=========================================
# Serializable Classes
#=========================================

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#=========================================
# Logging
#=========================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

#=========================================
# Third Party Libraries
#=========================================

# Keep classes with @Keep annotation
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Supplemental rules for specific libraries can be added below