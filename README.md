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
  - This [page](https://code.google.com/p/maven-android-plugin/issues/detail?id=395) has
  some guide for OSX users.

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
  - install SDK version 19, 15, 7
  
* Install Google Play Services into your Maven repo
  - https://github.com/JakeWharton/gms-mvn-install
  - This step requires Maven 3.1.1, so switched your version temporarily.
  - run the script with 7 appended to indicate that version should be installed.

    ./gms-mvn-install.sh 7

Build
-----
You can build using Maven:

    $ mvn clean install

Run
---
Deploy to an Android virtual device (AVD):

    $ mvn android:deploy
