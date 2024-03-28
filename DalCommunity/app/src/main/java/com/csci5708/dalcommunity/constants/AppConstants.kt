package com.csci5708.dalcommunity.constants

class AppConstants {
    companion object {
        // Common shared preferences key used by the application
        const val APP_SHARED_PREFERENCES = "sharedPref"
        // Key for accessing the boolean value of whether user is signed in the shared preferences
        const val SP_IS_SIGNED_IN_KEY = "isSignedIn"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}