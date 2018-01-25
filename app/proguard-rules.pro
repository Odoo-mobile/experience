# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/dpr/eclipse-adt/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-dontobfuscate
-keep class * extends com.odoo.core.orm.OModel{
    public *;
}
# fixes java.util.MissingResourceException: Can't find resource
# for bundle 'org.ocpsoft.prettytime.i18n.Resources_en_US'
-keep class org.ocpsoft.prettytime.i18n.**

# Searchview v4
-keep class android.support.v7.widget.SearchView { *; }
