#/****************************************************************************
#*   Cartoonifier, for Android.
#*****************************************************************************
#*   by Shervin Emami, 5th Dec 2012 (shervin.emami@gmail.com)
#*   http://www.shervinemami.info/
#*****************************************************************************
#*   Ch1 of the book "Mastering OpenCV with Practical Computer Vision Projects"
#*   Copyright Packt Publishing 2012.
#*   http://www.packtpub.com/cool-projects-with-opencv/book
#****************************************************************************/


LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on

# Path to OpenCV.mk file, which is generated when you build OpenCV for Android.
# include C:\OpenCV\android\build\OpenCV.mk
# include ~/OpenCV/android/build/OpenCV.mk
include ../OpenCV-2.4.9-android-sdk/sdk/native/jni/OpenCV.mk


LOCAL_MODULE    := markerar
LOCAL_LDLIBS +=  -llog -ldl


include $(BUILD_SHARED_LIBRARY)
