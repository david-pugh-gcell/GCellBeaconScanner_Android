# GCellBeaconScanner Android
Example Android Studio Project using the GCellBeaconScan aar Library to detect nearby iBeacon devices.

This project is an example Android Studio project that uses the GCellBeaconScanner aar Library to easily set up and detect proximity to nearby iBeacon devices. 

For more information about iBeacons, potential applications, the Framework and other software support such as platforms please contact us at [GCell ibeacon.solar](http://www.ibeacon.solar).

Latest version: v1_01 (11 August 2016)

The *gcellbeaconscanlibrary* module provides all the tools you need to start scanning for iBeacon devices in Android with minimal code. The library allows the developer to scan for nearby iBeacon devices in two ways:

1. Return a list of all nearby iBeacon devices, regardless of their UUID.
2. Return only information on iBeacon devices from pre-determined Beacon Regions. You can monitor and range these regions, in a method very similar to that used in iOS. 
 
There are 4 classes, the one that you will interact most with is the GCellBeaconScanManager
This class handles the Bluetooth scans and returns any scanned devices in range that have an appropriate advertising packet. E.g., once initialized and running it returns callbacks based on what beacons are ranged. This are returned every 1 second in the form of an array list. 

Beacons are flushed from the list if they haven’t been seen for x seconds, as defined by setBeaconAutoRefreshRate, this defaults to 10s to match iOS. During this interval the RSSI of any beacon that may have just gone out of range is set to 0 and proximity unknown.

### Java Documentation & Overview
For Java documentation please see the repository [docs](https://htmlpreview.github.io/?https://raw.githubusercontent.com/david-pugh-gcell/GCellBeaconScanner_Android/master/GCellBeaconDocs/index.html).

###Compatibility
The library is designed and tested to work with Android 4.3 (API Level 18) onwards. This API introduces built-in platform support for Bluetooth Low Energy to scan and discover devices. 

# Using the Library

##Adding the Library to your Project

Use the module import wizard (File | New Module | Import .JAR or .AAR package) which will automatically add the .aar as a library module in your project. 

Then add the module as a dependency to the app - Go to File>Project Settings (Ctrl+Shift+Alt+S), under 'Modules' in the left hand window select 'app'. On the dependencies tab, click the green + symbol in the top right hand corner and add Module Dependency and select the library.


Alternatively you can import the aar manually using the following steps:

1. Download and Copy the **gcellbeaconscanlibrary-release-vX.aar** file into the libs folder in your Android Studio Project.
2. Within your /build.gradle file add the following entry to allow the app to see the library locally

````xml
  repositories {
        flatDir{
            dirs 'libs'
        }
    }
````

3. and add the aar as a dependency in the /app/build.gradle

````xml
dependencies {
    compile(name:'gcellbeaconscanlibrary-release-v1-01', ext: 'aar')
}
````


##Setting Permissions
In order to detect beacons your app will need to have manifest permission to access to Bluetooth and your location. To enable these permissions add the following entries to the AndroidManifest.xml file in your app. 

````xml
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
````
The library will automatically check for user permissions and the status of Bluetooth Low Energy (BLE) on the device and is compatible with Marshmallow. 

## Import the class definitions

````java
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconManagerScanEvents;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconRegion;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconScanManager;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCelliBeacon;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellUuid;
````
The Framework also utiklises Lists and ArrayLists

````java
import java.util.ArrayList;
import java.util.List;
''''


## Implement GCellBeaconManagerScanEvents interface and Implement methods
````java
 public class MainActivity extends Activity implements GCellBeaconManagerScanEvents{
````

````java
	 // region Handle BeaconScanManager Events

	 // This event means the scan manager has updated the ranged beacon list
	 public void onGCellUpdateBeaconList(List<GCelliBeacon> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found: " + disc_gcell_beacons.size());
	 }

	 // This event means the device has enetred a beacon region. To find out more about what beacons are in the region, start ranging.
	 // This will return beacon UUID, Major, Minor and RSSI values
	 public void didEnterBeaconRegion(GCellBeaconRegion region) {
		 Log.i(TAG, "Entered region: " + region.toString());
		 mbtManager.startRangingforRegion(region);
	 }

	 // The device has exited a region, we can now stop ranging for that region to save battery
	 public void didExitBeaconRegion(GCellBeaconRegion region) {
		 Log.i(TAG, "Exited region: " + region.toString());
		 mbtManager.stopRangingforRegion(region);
	 }

	 // Beacons within a region have been ranged - we now have a list of beacons and their values
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCelliBeacon> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found in region: " + disc_gcell_beacons.size() + " " + region.toString());

	 }

	 // The user has denied permission for coarse location.
	 public void locationPermissionsDenied(){
		 mbtManager.stopScanningForBeacons();
	 }

	 // Bluetooth Low Energy (BLE) is not supported for this device
	 public void bleNotSupported() {

	 }

	 // BLE is not on for this device
	 public void bleNotEnabled() {
		 Log.i(TAG, "BLE not enabled");

	 }
	// endregion
````

## Add Permission Handler for Android 6.0
In order to detect and use beacons, your app needs to have location permissions granted. As of Android 6.0 (API level 23) this is classed as a 'Dangerous' permission and as such the user has to explicitly give approval to your app. The GCellBeaconScanManager library automatically checks and, if required, requests the appropriate location permissions in Marshmallow. In order to handle this request properly you need to implement a *onRequestPermissionResult* method in your Activity. This just calls the *permissionResult* method in the GCellBeaconScanManager Library which will deal with the values. The GCellBeaconScanManager Library requests permissions with a requestCode value of 1. If you need to request additional permissions in your project, either use a requestCode value greater than 1 or change the code the library uses by changing the value of  *coarseLocationRequestcode*.

````java
@Override
public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
         //Check to see if this is feedback from the request that the Scan manager made by checking the requestCode
         if(requestCode == mbtManager.getPermissionRequestCode()) {
             mbtManager.handlePermissionResult(requestCode, permissions, grantResults);
         }else{
             //This wasnt a response to the Scan Manager permission another request, handle accordingly
             Log.i(TAG,"This was another request");
         }

}
````

#Using the library
Now the set up is complete, its time to start using the library. The first task is to create an instance object of GCellBeaconScanmanager
Declare a GCellBeaconScanmanager as an instance variable:
````java
private GCellBeaconScanManager mbtManager;
````

Then create an instance within the onCreate method of your Activity:

````java
		// set up scan manager
		mbtManager = new GCellBeaconScanManager(this);
````


There are two ways to use the library:

## Scanning for Regions
This method will be familar to anyone who has used iBeacon within iOS. Beacon regions are defined and information about whether you are within these regions and what beacons that correspond to that region are returned. 
To use this method, set the *useBeaconRegions* to true.
````java
		//use known regions
		mbtManager.useBeaconRegions(true);
````
### Defining a Beacon region
Regions represent the area a user can be in if they can see one or more beacons within range. You can use this capability to generate alerts or to provide other relevant information when the user enters or exits a beacon region. Rather than being identified by fixed geographical coordinates, a beacon region is identified by the device’s proximity to Bluetooth low-energy beacons that advertise a combination of the following values:

<li>A proximity UUID (universally unique identifier), which is a 128-bit value that uniquely identifies one or more beacons as a certain type or from a certain organization or project
<li>A major value, which is a 16-bit unsigned integer that can be used to group related beacons that have the same proximity UUID
<li>A minor value, which is a 16-bit unsigned integer that differentiates beacons with the same proximity UUID and major value.

Beacon regions can be defined by their proximity UUID only, Proximity UUID and Major number or by the proximity UUID, Major and Minor Numbers. This gives developers flexibility in how they define iBeacon projects and infastructure. The GCellBeaconRegion class has a number of constructures corresponding to these different definitions, but you can also automatically define a region based on the default GCell UUID.

````java
GCellBeaconRegion gCellRegion = new GCellBeaconRegion(); //Sending no parameters initiates a region using the Default GCell UUID
GCellBeaconRegion otherRegion = new GCellBeaconRegion(new GCellUuid("6953fD4f-cfAF-ff58-a9cf-574A5E383c24"), "com.other");
````

Then just add these regions to the manager as an ArrayList

````java
	 private ArrayList<GCellBeaconRegion> beaconRegions = new ArrayList<GCellBeaconRegion>();
	 	....
	 beaconRegions.add(gCellRegion);
	mbtManager.setBeaconRegions(beaconRegions);
````
Then just start monitoring for these regions.
		
````java
	mbtManager.startMonitoringForBeacons();
````

The library will monitor for BLE devices in low power mode; if any iBeacon devices are in range that correspond to the Beacon Regions defined, the library will call the *didEnterRegion* method. To start to get more details of the beacons in that region, then call the *startMonitoringForBeaconinRegion* method.

````java
	// This event means the device has enetred a beacon region. To find out more about what beacons are in the region, start ranging.
	 // This will return beacon UUID, Major, Minor and RSSI values
	public void didEnterBeaconRegion(GCellBeaconRegion region) {
	 mbtManager.startRangingforRegion(region);
	}
````
	 
 Details of the iBeacon devices in range will then be returned via the 
````java
	 	 // Beacons within a region have been ranged - we now have a list of beacons and their values
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCelliBeacon> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found in region: " + disc_gcell_beacons.size() + " " + region.toString());
	 }
````

## Scanning for all iBeacon Devices
You can also scan and return a list of all nearby iBeacon devices regardless of their UUID, Major and Minor settings. In this case set *useBeaconRegions* to false and just start scanning 
````java
mbtManager.useBeaconRegions(false);
mbtManager.startScanningForBeacons();
````
This method of operation can be easier to set up, but can be more power hungry and can also leave you with a large list of beacon devices that you then have to manage yourself. These will be returned via * onGCellUpdateBeaconList*.
````java
	 public void onGCellUpdateBeaconList(List<GCellBleDevice> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found: " + disc_gcell_beacons.size());
	 }
````
## Fine tuning the Library
You can configure some of the default settings of the library as outlined below and in the documentation. 
````java

        // Switch debug to true to get feedback from the library during development via Log.i
        mbtManager.enableDeuggingFeedback(true);
        // Set the library to automatically switch BLE on if it is off or switched off, default is true
        mbtManager.enableBlueToothAutoSwitchOn(true);
        // Set the auto-refresh rate to 10 s - this is the time we will go without seeing a beacon before deleting it from the ranged list. Default is 8s
        mbtManager.setBeaconAutoRefreshRate(10);

        //Set the message to display when showing information dialog before requesting permissions to access users location
        mbtManager.setPermissionExplanationMessage("Example Feedback Message. We need permission sto see your location before we can see beacons!");
        // Set the requestCode used to Request Grant Permissions - default is 1
        mbtManager.setPermissionRequestCode(24);
````
