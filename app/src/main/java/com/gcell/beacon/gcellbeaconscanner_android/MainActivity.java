 package com.gcell.beacon.gcellbeaconscanner_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconRegion;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBeaconScanManager;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellBleDevice;
import com.gcell.ibeacon.gcellbeaconscanlibrary.GCellUuid;

import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends Activity implements GCellBeaconScanManager.GCellBeaconManagerScanEvents{

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
		//use known regions
		mbtManager.useBeaconRegions(true);
		//set known regions
		GCellBeaconRegion gCellRegion = new GCellBeaconRegion(); //Sending no parameters initiates a region using the Default GCell UUID
		beaconRegions.add(gCellRegion);
		mbtManager.setBeaconRegions(beaconRegions);
		mbtManager.startMonitoringForBeacons();

		/////////// You can also tweak other settings
		// Switch debug to true to get feedback from the library during development
		mbtManager.deBug = true;
		// Set the library to automatically switch BLE on if it is off or switched off
		mbtManager.autoSwitchOnBlueTooth = true;
		// Set the auto-refresh rate
		mbtManager.setBeaconAutoRefreshRate(10);

	}



	 // region Handle BeaconScanManager Events

	 // This event means the scan manager has updated the ranged beacon list
	 public void onGCellUpdateBeaconList(List<GCellBleDevice> disc_gcell_beacons) {
		 Log.i(TAG, "Beacons found: " + disc_gcell_beacons.size());
	 }

	 // This event means the device has enetred a beacon region. To find out more about what beacons are in the region, start ranging.
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
	 public void didRangeBeaconsinRegion(GCellBeaconRegion region, List<GCellBleDevice> disc_gcell_beacons) {
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
		 mbtManager.permissionResult(requestCode, permissions, grantResults);
	 }

 	// end region
 }
