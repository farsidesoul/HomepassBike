# HomepassBike

The project should be built with Gradle and loaded onto an Android device.  

The app will initially load the sole Activity which hosts a fragment that loads the full screen map.  

A single service is setup to call the data directly from the Melbourne Data site each time the app is loaded.
After the initial locations are retrieved, the will be saved to SharedPreferences for quick access whenever the user opens the app.
These locations are updated whenever the fragment is recreated and a Toast is shown to inform the user as such.

If the locations fail to download the first time, the app will try again another 4 times.  If the last attempt fails,
a toast is displayed to the user.

