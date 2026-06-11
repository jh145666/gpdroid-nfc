# Add project specific ProGuard rules here.
# Keep GlobalPlatform related classes
-keep class net.sourceforge.gpj.** { *; }
-keep class org.simalliance.openmobileapi.** { *; }
# Keep smartcardio classes
-keep class javax.smartcardio.** { *; }
# Keep AID and registry classes
-keepclassmembers class at.fhooe.usmile.gpjshell.** { *; }
