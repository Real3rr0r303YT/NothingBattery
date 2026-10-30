# NothingBattery has no reflection-based code; default R8 rules are sufficient.
# Strip debug logging from release builds.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}
