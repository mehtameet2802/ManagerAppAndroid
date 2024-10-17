# Manager App

## Overview

**Manager App** is an Android application designed to help retailers manage their inventory efficiently. This app provides features for seamless addition, updating, and searching of items, as well as processing transactions and accessing transaction history in PDF format.

## Features

- **User Authentication**

  - Retailers can sign up and log in to manage their inventory
  - Secure access to personal inventory data
  - Password reset functionality

- **Inventory Management**

  - Add, update, and search for items in the inventory with ease
  - Real-time inventory tracking
  - Low stock alerts
  - File import/export capabilities

- **Transaction Processing**

  - Efficiently process transactions for sales and purchases

- **Transaction History**
  - Access transaction history and download it in PDF format
  - Detailed sales reports
  - Custom date range filtering
  - Export capabilities

## Technical Stack

### Core Technologies

- **Programming Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose

### Data Management

- **Data Handling**: Flows, Coroutines
- **Backend**: Firebase
- **Local Database**: RoomDB
- **Dependency Injection**: Dagger Hilt

## Installation

1. Clone the repository:

```bash
git clone https://github.com/mehtameet2802/ManagerAppAndroid.git
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

## Requirements

- Android Studio Arctic Fox or later
- Minimum SDK: Android 21 (Lollipop)
- Target SDK: Android 34
- Kotlin version: 1.8.0 or later

## Configuration

1. Create a Firebase project and add your `google-services.json` file to the app directory
2. Enable Authentication and Firestore in your Firebase console
3. Update the necessary API keys in `local.properties`

## Acknowledgments

- Firebase team for the excellent backend services
- Android Jetpack team for amazing development components
