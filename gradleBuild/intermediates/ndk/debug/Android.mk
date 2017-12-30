LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := android
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\arm64-v8a\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi-v7a\backup.old\libtensorflow_demo.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi-v7a\backup.old\libtensorflow_demo.so.backup2 \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi-v7a\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi-v7a\libpthread.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\armeabi-v7a\libtensorflow_demo.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\mips\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\mips64\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\x86\libopencv_java3.so \
	C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs\x86_64\libopencv_java3.so \

LOCAL_C_INCLUDES += C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jniLibs
LOCAL_C_INCLUDES += C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\src\main\jni
LOCAL_C_INCLUDES += C:\Users\Gil\AndroidStudioProjects\tensorflow-master\tensorflow\examples\android\build-types\debug\jni

include $(BUILD_SHARED_LIBRARY)
