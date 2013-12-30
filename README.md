guide-android-vandy-mobile
==========================

VandyMobile's Guide app for Android

Build Notes
-----------
* Maven 3.0.5
  - Please download the right version from http://maven.apache.org/download.cgi
  - version 3.1.1 does not work right now. The default version provided in the Ubuntu
  repo also does not work the last time I tried.
  - Follow the direction at the bottom of the page to change the default Maven
  version used. Check that it is working with `mvn -v`

* Android SDK
  - make sure `ANDROID_HOME` is set to the path to your SDK folder
  - for example, in ~/.bashrc add

```bash  
export ANDROID_HOME=/home/{path_to_the_folder}/sdk
export ANDROID_SDK_HOME=/home/{path_to_the_folder}/sdk
export ANDROID_SDK_ROOT=/home/{path_to_the_folder}/sdk
export ANDROID_SDK=/home/{path_to_the_folder}/sdk
```

  - ^^^Don't ask why. Android is always crazy.
  
* Install Google Play Services into your Maven repo
  - https://github.com/JakeWharton/gms-mvn-install

Build
-----
You can build using Maven:

    $ mvn clean package

This will compile the project and generate an APK. The generated APK is
signed with the Android debug certificate. To generate a zip-aligned APK
that is signed with an actual certificate, use:

    $ mvn clean package -Prelease

The configuration for which certificate to use is in pom.xml.

Run
---
Deploy to an Android virtual device (AVD):

    $ mvn android:deploy
