# AGE MTX Scanner

An Android application for scanning and connecting to AGE MTX measurement devices via WiFi to retrieve device information and configuration details.

## Features

- **WiFi Device Scanning**: Scan for AGE MTX devices broadcasting WiFi with customizable regex patterns
- **Device Connection**: Connect to AGE MTX devices using WPA2 security
- **Device Information Retrieval**: Fetch comprehensive device specifications and configuration
- **Real-time Status**: Display signal strength, frequency, and connection status
- **User-friendly Interface**: Simple, intuitive UI for device discovery and information display

## Screenshots

*Main Scanning Screen* | *Device Information Screen*
:-------------------------:|:-------------------------:
![Main Screen](screenshots/main.png) | ![Device Info](screenshots/device_info.png)

## Prerequisites

- **Android Device**: Android 13+ (API level 33 or higher)
- **Hardware**: Device must support WiFi and location services
- **AGE MTX Devices**: Physical AGE MTX measurement devices broadcasting WiFi
- **Android Studio**: Latest version recommended for development

## Installation

### From Source

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd age_mtx_app
   ```

2. Open in Android Studio:
   - File → Open → Select project directory
   - Wait for Gradle sync to complete

3. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```

4. Install on device:
   ```bash
   ./gradlew installDebug
   ```

### Direct APK Installation

1. Download the latest APK from [Releases](#)
2. Enable "Install from unknown sources" on your Android device
3. Install the APK file

## Usage

### Scanning for Devices

1. Launch the AGE MTX Scanner app
2. Grant required permissions when prompted:
   - Location access (required for WiFi scanning)
   - WiFi management permissions
3. The app will automatically start scanning for devices matching the pattern `age_dev_N.*`
4. View discovered devices in the list with signal strength indicators

### Connecting to a Device

1. Tap on a discovered device from the list
2. The app will:
   - Connect to the device's WiFi network
   - Establish socket connection to `192.168.4.1:22`
   - Retrieve device information
3. View detailed device specifications on the information screen

### Customizing Scan Patterns

1. In the main screen, modify the regex pattern in the input field
2. Default pattern: `age_dev_N.*` (matches AGE MTX device SSIDs)
3. Press "Scan" to apply the new pattern

## Device Requirements

### AGE MTX Devices
- Must broadcast WiFi with SSID pattern matching `age_dev_N*`
- Must be configured with WPA2 passphrase: `"password_age"`
- Must be accessible at IP: `192.168.4.1` on port 22

### Android Device
- Android 13+ (API level 33)
- WiFi capability
- Location services enabled
- Sufficient storage space

## Permissions

The app requires the following permissions:

- `ACCESS_FINE_LOCATION` - For WiFi scanning
- `ACCESS_WIFI_STATE` - To check WiFi status
- `CHANGE_WIFI_STATE` - To connect to device networks
- `ACCESS_COARSE_LOCATION` - For approximate location
- `NEARBY_WIFI_DEVICES` - For WiFi device discovery
- `INTERNET` - For network communication
- `ACCESS_NETWORK_STATE` - To monitor network connectivity
- `CHANGE_NETWORK_STATE` - To modify network settings
- `WRITE_SETTINGS` - To modify system settings

## Project Structure

```
age_mtx_app/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/org/demidrol/age_mtx/
│   │   │   ├── MainActivity.java          # Main scanning activity
│   │   │   ├── DeviceInfoActivity.java    # Device details activity
│   │   │   ├── WifiDevInfo.java           # Device info data model
│   │   │   └── NetworkDeviceItem.java     # Network device data model
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml      # Main UI layout
│   │   │   │   └── activity_device_info.xml # Device info UI layout
│   │   │   └── values/
│   │   │       └── strings.xml            # String resources
│   │   └── AndroidManifest.xml            # App manifest
│   └── build.gradle                       # App module build config
├── build.gradle                          # Root project build config
├── settings.gradle                       # Project settings
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties     # Gradle 9.1.0
├── gradlew                              # Gradle wrapper scripts
├── gradlew.bat
├── local.properties                      # Local SDK configuration
└── video/
    └── age_mtx.mp4                       # Demo/instructional video
```

## Technical Details

### Architecture
- **Language**: Java
- **Minimum SDK**: API 33 (Android 13)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34
- **Gradle**: Version 9.1.0
- **Android Gradle Plugin**: 8.2.0

### Components
- **MainActivity**: Handles WiFi scanning, device discovery, and user interface
- **DeviceInfoActivity**: Displays detailed device information
- **WifiDevInfo**: Data model for parsed device information
- **NetworkDeviceItem**: Data model for discovered WiFi networks

### Communication Protocol
1. **WiFi Connection**: Connect to device WiFi using WPA2 passphrase
2. **Socket Connection**: Establish TCP socket to `192.168.4.1:22`
3. **Binary Request**: Send specific binary request to device
4. **Response Parsing**: Parse binary response into structured device information

## Building from Source

### Requirements
- Android Studio Flamingo or later
- Android SDK with API level 34
- Java 11 JDK
- Gradle 9.1.0

### Build Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run lint checks
./gradlew lint
```

## Troubleshooting

### Common Issues

1. **No devices found**
   - Ensure AGE MTX devices are powered on and broadcasting WiFi
   - Verify device SSID matches the scan pattern
   - Check that location permissions are granted
   - Ensure WiFi is enabled on Android device

2. **Connection failed**
   - Verify device uses WPA2 passphrase `"password_age"`
   - Check device is accessible at `192.168.4.1:22`
   - Ensure no firewall is blocking connections

3. **Permission errors**
   - Grant all requested permissions when prompted
   - Enable location services
   - Check app permissions in device settings

4. **Build errors**
   - Ensure Android SDK is properly configured
   - Verify Java 11 JDK is installed
   - Check internet connection for Gradle dependencies

### Debugging

Enable debug logging by checking the Android Logcat output with tag "AGE_MTX". Common log tags:
- `MainActivity`: Scanning and UI events
- `WifiDevInfo`: Device information parsing
- `Network`: Connection and socket operations

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Android coding conventions
- Add comments for complex logic
- Test on physical devices with AGE MTX hardware
- Update documentation for new features

## License

This project is proprietary software. All rights reserved.

## Support

For technical support or feature requests:
- Create an issue in the repository
- Contact the development team
- Refer to the demo video in `/video/age_mtx.mp4`

## Version History

- **v1.0** (Current)
  - Initial release
  - Basic WiFi scanning and device connection
  - Device information display
  - Customizable scan patterns

## Acknowledgments

- AGE MTX device specifications and communication protocol
- Android WiFi and network APIs
- Development and testing teams
