# *Focus!*
CSCI-310 Class Project

### Getting set up:

Clone the repository, and open up the latest version of Android Studio.
We are targeting Android **SDK 23**, which is Android Marshmallow. 
Additionally, we will be using a Nexus 6 virtual device for testing.

### Working on feature branches:
Master is a **protected branch**, meaning it's physically disallowed from pushing to it.
In order to work on a branch, go into your terminal and type: `git checkout -B <your-branch-name`
When you push, you'll get an error saying that there is no upstream branch. Just copy and paste what it says to create one.
Once your feature branch is done, create a pull request on Github and add reviewers.
Merging more frequently and with smaller incremental updates is usually better.

### ORM stuff:
All of the models are in the models package, with the objects in `objects/` and mappers in `mappers`.
There's also a `DBHelper` class which just abstracts out a lot of the querying needed to retrieve stuff.
It's static so you can just call its utility functions anywhere.

## Deployment
### Environment Set-Up
#### Android Studio
Download Android Studio (https://developer.android.com/studio/index.html)
#### Project Code Import
##### Download
Download the project code or clone the github repository (if provided access)
##### Import
Open Android Studio and import the project code
### Emulation
Focus! can be run on both an emulator and an actual device. However, emulators cannot receive notifications so it is suggested to run 
Focus! on an actual device.
#### Device
##### Enable Developer Mode
+ Stock Android: Settings > About phone > Build Number (Tap 7 times)
+	Samsung Galaxy S5: Settings > About device > Build Number (Tap 7 times)
+	LG G3: Settings > About Phone > Software Information > Build Number (Tap 7 times)
+	HTC One (M8): Settings > About > Software Information> More > Build Number (Tap 7 times)
##### Permissions
+ Accept the popup that asks for debugging permissions.
+ Settings > Debugger Mode > Allow USB Debugging > Accept Pop-up Message
### Run
Press the green play button to run Focus! on your selected device
#### First Run
+	If this is the first time running the app or the permissions have been reset, you will be asked to give Notification Access or 
permission to BlockedNotificationListener. This allows our app to “listen” for the notifications you are receiving and block the 
notifications you don’t want to receive. Toggle the permissions switch to “on”. In the pop-up that appears, click “Ok” or “Allow.” Focus 
will not work unless you allow this permission. Press back after you give BlockedNotificationListener permission.
+	If this is the first time running the app or the permissions have been reset, you will be asked to give Accessibility access to 
WindowChangeDetectingService. You may need to scroll down to see this under the section “Services.” Click on the service and toggle the 
switch to “On”. Press “Ok” or “Allow” in the pop-up that appears. Press the back button until you see Focus!
### Common Errors & Fixes
#### Notifications Are Not Being Blocked
Uninstall the app and restart the phone. This is a common problem with using the NotificationListenerServices that hasn’t yet been 
resolved by Android.

