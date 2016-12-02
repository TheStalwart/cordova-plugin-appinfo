package org.scriptotek.appinfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.webkit.WebView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppInfo extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getAppInfo")) {
                this.getAppInfo(callbackContext, args.isNull(0)?this.cordova.getActivity().getPackageName():args.getString(0));
                return true;
        } else if (action.equals("getVersion")) {
            this.getVersion(callbackContext);
            return true;
        } else if (action.equals("getIdentifier")) {
            this.getIdentifier(callbackContext);
            return true;
        } else if (action.equals("getInstallerPackageName")) {
            this.getInstallerPackageName(callbackContext);
            return true;
        } else if (action.equals("getSystemWebViewUserAgent")) {
            this.getSystemWebViewUserAgent(callbackContext);
            return true;
        }
        return false;
    }

    private void getAppInfo(CallbackContext callbackContext, String packageName){

        String versionName = "";
        String versionCode = "";

        PackageManager pm = this.cordova.getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
        }

        JSONObject appInfo = new JSONObject();
        try {
            appInfo.put("identifier", packageName);
            appInfo.put("version", versionName);
            appInfo.put("build", versionCode);
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }

        callbackContext.success(appInfo);
    }

    private void getVersion(CallbackContext callbackContext) {

        String versionName;
        String packageName = this.cordova.getActivity().getPackageName();
        PackageManager pm = this.cordova.getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException nnfe) {
            versionName = "";
        }
        callbackContext.success(versionName);

    }

    private void getIdentifier(CallbackContext callbackContext) {

        String packageName = this.cordova.getActivity().getPackageName();
        callbackContext.success(packageName);
    }

    private void getInstallerPackageName(CallbackContext callbackContext) {

        String installerPackageName;
        String packageName = this.cordova.getActivity().getPackageName();
        PackageManager pm = this.cordova.getActivity().getPackageManager();
        try {
            installerPackageName = pm.getInstallerPackageName(packageName);
        } catch (Exception e) {
            installerPackageName = "";
        }
        callbackContext.success(installerPackageName);

    }

    private void getSystemWebViewUserAgent(final CallbackContext callbackContext) {

        final CordovaPlugin plugin = this;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                WebView webview = new WebView(plugin.cordova.getActivity());
                callbackContext.success(webview.getSettings().getUserAgentString());
            }
        });


    }
}

