# GCellBeaconScanner Android
Example Android Studio Project using the GCellBeaconScan Library to detect nearby beacons.

This project is an example Android Studio project that uses the GCellBeaconScanner Library to easily set up and detect proximity to nearby iBeacons. 

For more information about iBeacons, potential applications, the Framework and other software support such as platforms please contact us at GCell ibeacon.solar.

Latest version: v1_0 (08 August 2016)

## Java Documentation
For Java documentation please see the repository [docs](https://htmlpreview.github.io/?https://raw.githubusercontent.com/david-pugh-gcell/GCellBeaconScanner_Android/master/GCellBeaconDocs/index.html).

The *gcellbeaconscanlibrary* module provides all the tools you need to start scanning for iBeacon devices in Android with minimal code. The library allows the developer to scan for nearby iBeacon devices in two ways:

1. Return a list of all nearby iBeacon devices, regardless of their UUID
2. Return only information on iBeacon devices from pre-determined Beacon Regions. You can monitor and range these regions, in a method very similar to that used in iOS. 


There are 4 classes, the one that you will interact most with is the GCellBeaconScanManager
This class handles the Bluetooth scans and returns any scanned devices in range that have an appropriate advertising packet. E.g., once initialized and running it returns callbacks based on what beacons are ranged. This are returned every 1 second in the form of an array list.
Beacons are flushed from the list if they haven’t been seen for x seconds, as defined by setBeaconAutoRefreshRate, this defaults to 10s to match iOS. During this interval the RSSI of any beacon that may have just gone out of range is set to 0 and proximity unknown. 

# Using the Library
##Compatibility
The library is designed and tested to work with API ??
##Adding the library to your Project
1. Download and Copy the **gcellbeaconscanlibrary-release-vX.aar** file into the libs folder in your Android Studio Project.
2. Within your app build.gradle file add the following entry to allow the app to see the library locally

````xml
  repositories {
        flatDir{
            dirs 'libs'
        }
    }
````

3. and add the aar as a dependency

````xml
dependencies {
    compile(name:'gcellbeaconscanlibrary-release-v1-0', ext: 'aar')
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
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconRegion;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconScanManager;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBleDevice;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellUuid;
````

## Implement GCellBeaconManagerScanEvents interface and Implement methods
````java
 public class MainActivity extends Activity implements GCellBeaconScanManager.GCellBeaconManagerScanEvents{
````

````java
	 // region Handle BeaconScanManager Events

	 // This event means the scan manager has updated the ranged beacon list
	 public void onGCellUpdateBeaconList(List<GCellBleDevice> disc_gcell_beacons) {
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
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCellBleDevice> disc_gcell_beacons) {
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

## Add Permission Handler
In order to ask user for relevant permission in Marshmallow, you need to implement a *onRequestPermissionResult* method. This just calls the *permissionResult* method in the GCellBeaconScanmanager which will deal with the values. 

````java

	 // region Handling permission request
	 /**
	  * This allows us to deal with Location permissions from the user - it allows the library to handle any feedback calls
	  * IT overrides the Activity onrequestPermissionsResult
	  * @param requestCode
	  * @param permissions
	  * @param grantResults
	  */
	 @Override
	 public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		 mbtManager.permissionResult(requestCode, permissions, grantResults);
	 }

 	// end region
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

The library will monitor for BLE devices in low power mode; if any iBeacon devices are in range that correspond to the Beacon Regions defined, the library will call the *didEnterRegion* method. To start to get more details of the beacons in that region, then call teh *startMonitoringForBeaconinRegion* method.

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
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCellBleDevice> disc_gcell_beacons) {
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
````java
		/////////// You can also tweak other settings
		// Switch debug to true to get feedback from the library during development
		mbtManager.deBug = true;
		// Set the library to automatically switch BLE on if it is off or switched off
		mbtManager.autoSwitchOnBlueTooth = true;
		// Set the auto-refresh rate in seconds 
		mbtManager.setBeaconAutoRefreshRate(20);
````
