package com.ElOuedUniv.maktaba

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom Application class required by Dagger Hilt.
 *
 * @HiltAndroidApp triggers Hilt's code generation, including a base class
 * for the application that serves as the application-level dependency container.
 *
 * This is the entry point for Hilt and MUST be declared in AndroidManifest.xml
 * using android:name=".MaktabaApplication"
 */
@HiltAndroidApp
class MaktabaApplication : Application()
