package org.scriptotek.appinfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.webkit.WebView;
import android.util.Log;
import android.opengl.GLES10;
import java.util.Arrays;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

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
        } else if (action.equals("getResetNotificationStrategy")) {
            this.getResetNotificationStrategy(callbackContext);
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

    private void getResetNotificationStrategy(final CallbackContext callbackContext) {

		this.cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				EGL10 egl = (EGL10)EGLContext.getEGL();

				EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
				int[] vers = new int[2];
				egl.eglInitialize(dpy, vers);

				int[] configAttr = {
					EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
					EGL10.EGL_LEVEL, 0,
					EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
					EGL10.EGL_NONE
				};
				EGLConfig[] configs = new EGLConfig[1];
				int[] numConfig = new int[1];
				egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
				if (numConfig[0] == 0) {
					// TROUBLE! No config found.
					callbackContext.error("No EGL config found");

					return;
				}
				EGLConfig config = configs[0];

				int[] surfAttr = {
					EGL10.EGL_WIDTH, 64,
					EGL10.EGL_HEIGHT, 64,
					EGL10.EGL_NONE
				};
				EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
				final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;  // missing in EGL10
				int[] ctxAttrib = {
					EGL_CONTEXT_CLIENT_VERSION, 1,
					EGL10.EGL_NONE
				};
				EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
				egl.eglMakeCurrent(dpy, surf, surf, ctx);

				int[] strategy = new int[1];
				GLES10.glGetIntegerv(0x8256, strategy, 0); // GL_RESET_NOTIFICATION_STRATEGY_ARB

				callbackContext.success(strategy[0]);
			}
		});
    }
}

