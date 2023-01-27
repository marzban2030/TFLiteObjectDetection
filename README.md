# TFLiteObjectDetection
This is most simple, accurate and up to date Android TFlite project with fewest components all over the web which detects objects in live camera view. Every developer can understand and develope it easily.

This project consists of only two Kotlin files: `CameraActivity.kt` and `Detector.kt` which have few lines of codes.

# Quick build 
Run below commands in Colab, In this case you don't need any PC with installed Android Studio IDE:
```
!wget https://dl.google.com/android/repository/commandlinetools-linux-9123335_latest.zip
!mkdir -p sdk
!unzip commandlinetools-linux-9123335_latest.zip -d sdk
!yes | ./sdk/cmdline-tools/bin/sdkmanager --sdk_root=/content/sdk "tools"
!git clone https://github.com/marzban2030/TFLiteObjectDetection
!chmod -c 755 /content/TFLiteObjectDetection/gradlew
!export ANDROID_HOME=/content/sdk && cd /content/TFLiteObjectDetection && ./gradlew assembleDebug
```
