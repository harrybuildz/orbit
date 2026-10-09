# Keep kotlinx-serialization generated serializers for network DTOs.
-keepclassmembers class com.orbit.data.network.** {
    *** Companion;
}
-keepclasseswithmembers class com.orbit.data.network.** {
    kotlinx.serialization.KSerializer serializer(...);
}
