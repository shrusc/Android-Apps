When the emulator is rotated to show the landscape view,it changes the device configuration.
Android destroys the current activity and creates a new one whenever the device configuration changes at runtime.
So the lifecycle methods displayed on the TextView in portrait view are no longer displayed and this
causes the inconsistency between the methods displayed on the textview and the logs.

To solve this inconsistency, we need to save the whole list of method names(what was being displayed on the textview in the portrait mode)
in the onSaveInstanceState method. This data needs to be saved in the Bundle object which is passed on as a
parameter to the onSaveInstanceState method. This data will be read back from the Bundle when onCreate is called.