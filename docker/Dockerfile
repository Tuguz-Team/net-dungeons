FROM gradle

ENV SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-6858069_latest.zip" \
	ANDROID_HOME="/usr/local/android-sdk" \
	ANDROID_VERSION=30 \
	ANDROID_BUILD_TOOLS_VERSION=30.0.3

RUN mkdir "$ANDROID_HOME" .android \
    && cd "$ANDROID_HOME" \
	&& mkdir cmdline-tools \
	&& cd cmdline-tools \
    && curl -o sdk.zip $SDK_URL \
    && unzip sdk.zip \
    && rm sdk.zip \
	&& mv "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/tools" \
	&& mkdir "$ANDROID_HOME/licenses" || true \
    && echo "24333f8a63b6825ea9c5514f83c2829b004d1" > "$ANDROID_HOME/licenses/android-sdk-license" \
    && echo "84831b9409646a918e30573bab4c9c91346d8" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/cmdline-tools/tools/bin

RUN sdkmanager --update && yes | sdkmanager --licenses
RUN sdkmanager "build-tools;$ANDROID_BUILD_TOOLS_VERSION" \
    "platforms;android-$ANDROID_VERSION" \
    "platform-tools"
