 package com.gcell.beacon.gcellbeaconscanner_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconManagerScanEvents;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconRegion;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconScanManager;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCelliBeacon;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellUuid;

import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends Activity implements GCellBeaconManagerScanEvents {

	 private String TAG = "GCScanManager";
	 private GCellBeaconScanManager mbtManager;
	 private ArrayList<GCellBeaconRegion> beaconRegions = new ArrayList<GCellBeaconRegion>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Use the Beacon Scan manager to start scanning for beacons
		// set up scan manager
		mbtManager = new GCellBeaconScanManager(this);


        /////////// You can configure some of the default settings
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



		//use known regions
		mbtManager.useBeaconRegions(true);
		//set known regions
        //Sending no parameters initiates a region using the Default GCell UUID and label "com.gcell"
		GCellBeaconRegion gCellRegion = new GCellBeaconRegion();

        /*alternative is to use
        GCellBeaconRegion exampleRegion = new GCellBeaconRegion( new GCellUuid((new GCellUuid("26530D1D-F9AF-4152-B99E-9B1A5E826584"), "com.example");
         */
		beaconRegions.add(gCellRegion);
		mbtManager.setBeaconRegions(beaconRegions);
		mbtManager.startMonitoringForBeacons();
	}



	 // region Handle BeaconScanManager Events

	 // This event means the scan manager has updated the ranged beacon list
	 public void onGCellUpdateBeaconList(List<GCelliBeacon> disc_gcell_beacons) {
		 Log.i(TAG, "No Beacons found: " + disc_gcell_beacons.size());
	 }

	 // This event means the device has entered a beacon region. To find out more about what beacons are in the region, start ranging.
	 // This will return beacon UUID, Major, Minor and RSSI values
	 public void didEnterBeaconRegion(GCellBeaconRegion region) {
		 Toast.makeText(this, "Entered region " + region.toString(),
				 Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "Entered region: " + region.toString());
		 mbtManager.startRangingforRegion(region);
	 }

	 // The device has exited a region, we can now stop ranging for that region to save battery
	 public void didExitBeaconRegion(GCellBeaconRegion region) {
		 Toast.makeText(this, "Exited region " + region.toString(),
				 Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "Exited region: " + region.toString());
		 mbtManager.stopRangingforRegion(region);
	 }

	 // Beacons within a region have been ranged - we now have a list of beacons and their values
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCelliBeacon> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found in region: " + disc_gcell_beacons.size() + " " + region.toString());

	 }

	 // The user has denied permission for coarse location.
	 public void locationPermissionsDenied(){
		 Log.i(TAG, "Permission for Location Denied, stopping scan");
		 mbtManager.stopScanningForBeacons();
	 }

	 // Bluetooth Low Energy (BLE) is not supported for this device
	 public void bleNotSupported() {
		 Toast.makeText(this, "BLE not supported",
				 Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "BLE not supported");
		 finish();
	 }

	 // BLE is not on for this device
	 public void bleNotEnabled() {
		 Toast.makeText(this, "BLE Not Enabled",
				 Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "BLE not enabled, enabling...");

	 }
	// endregion

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
         //Check to see if this is feedback from the request that the Scan manager made by checking the requestCode
         if(requestCode == mbtManager.getPermissionRequestCode()) {
             mbtManager.handlePermissionResult(requestCode, permissions, grantResults);
         }else{
             //This wasnt a response to the Scan Manager permission another request, handle accordingly
             Log.i(TAG,"This was another request");
         }

	 }

 	// end region
 }
