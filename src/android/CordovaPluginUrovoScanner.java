package com.urovoscanner;

import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.device.ScanManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.apache.cordova.CordovaWebView;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginUrovoScanner extends CordovaPlugin {
	private static final String LOG_TAG = "UrovoScannerPlugin";
	private static final String ACTION_DECODE = "android.intent.ACTION_DECODE_DATA";
	private static final String BARCODE_STRING_TAG = "barcode_string";
	private static final String BARCODE_TYPE_TAG = "barcodeType";
	private static final String BARCODE_LENGTH_TAG = "length";
	private static final String DECODE_DATA_TAG = "barcode";

	private static ScanManager scanManager = new ScanManager();
	private static CallbackContext callbackContext = null;
	private static UrovoBroadcastReceiver scanReceiver = null;

	@Override
	protected void pluginInitialize() {
		super.initialize(cordova, webView);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DECODE);
		this.scanReceiver = new UrovoBroadcastReceiver();
		this.webView.getContext().registerReceiver(this.scanReceiver, filter);

		boolean powerOn = scanManager.getScannerState();
		if (!powerOn) {
			powerOn = scanManager.openScanner();

		}
		scanManager.switchOutputMode(0);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if (action.equals("scan")) {
			this.callbackContext = callbackContext;
			return true;
		} else if (action.equals("cancel")) {
			if (this.scanManager != null) {
				this.scanManager.stopDecode();
			}

			if (this.callbackContext != null) {
				PluginResult result = new PluginResult(PluginResult.Status.ERROR, "USER_CANCEL");
				result.setKeepCallback(true);
				this.callbackContext.sendPluginResult(result);
				this.callbackContext = null;
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		if (this.scanReceiver != null) {
			try {
				this.webView.getContext().unregisterReceiver(this.scanReceiver);
			} catch(Exception e) {
				LOG.d(LOG_TAG, "Error unregistering Urovo mobile receiver: " + e.getMessage(), e);
			} finally {
				this.scanReceiver = null;
			}
		}

		super.onDestroy();
	}

	public class UrovoBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// Get scan results, including string and byte data etc.
			byte[] barcode = intent.getByteArrayExtra(DECODE_DATA_TAG);
			int barcodeLen = intent.getIntExtra(BARCODE_LENGTH_TAG, 0);
			byte temp = intent.getByteExtra(BARCODE_TYPE_TAG, (byte) 0);
			String barcodeStr = intent.getStringExtra(BARCODE_STRING_TAG);

			if (scanManager != null) {
				if (callbackContext != null) {
					JSONObject json = new JSONObject();

					try {
						json.put("barcode", barcodeStr);
						json.put("type", barcodeLen);
					} catch(Exception e) {
						LOG.d(LOG_TAG, "Error sending urovo scanner receiver: " + e.getMessage(), e);
					}

					PluginResult result = new PluginResult(PluginResult.Status.OK, json);
					result.setKeepCallback(true);
					callbackContext.sendPluginResult(result);
				}
			}
		}
	}
}
