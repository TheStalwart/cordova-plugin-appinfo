
var exec = require('cordova/exec');
var channel = require('cordova/channel');

channel.createSticky('onAppInfoReady');
channel.waitForInitialization('onAppInfoReady');

function appInfo() {

    this.version = null;
    this.identfier = null;
    this.build = null;

    var me = this;

    channel.onCordovaReady.subscribe(function() {
        me.getAppInfo(function(info) {
            me.version = info.version;
            me.identifier = info.identifier;
            me.build = info.build || 'unknown';
            channel.onAppInfoReady.fire();
        },function(e) {
            utils.alert("[ERROR] Error initializing Cordova: " + e);
        });
    });
}

/**
 * Get an object with the keys 'version', 'build' and 'identifier'.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 * @param {string} bundleId     Bundle ID of package to query.
 */
appInfo.prototype.getAppInfo = function(success, fail, bundleId){
    exec(success, fail, 'AppInfo', 'getAppInfo', [bundleId]);
};

/**
 * Get the version name.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 */
appInfo.prototype.getVersion = function(success, fail) {
    exec(success, fail, 'AppInfo', 'getVersion', []);
}

/**
 * Get the app identifier.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 */
appInfo.prototype.getIdentifier = function(success, fail){
    exec(success, fail, 'AppInfo', 'getIdentifier', []);
}

/**
 * Get the app installer.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 */
appInfo.prototype.getInstallerPackageName = function(success, fail){
    exec(success, fail, 'AppInfo', 'getInstallerPackageName', []);
}

/**
 * Get System WebView User-Agent.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 */
appInfo.prototype.getSystemWebViewUserAgent = function(success, fail){
    exec(success, fail, 'AppInfo', 'getSystemWebViewUserAgent', []);
}

/**
 * Get GL_RESET_NOTIFICATION_STRATEGY_ARB.
 *
 * @param {Function} success    Callback method called on success.
 * @param {Function} fail       Callback method called on failure.
 */
appInfo.prototype.getResetNotificationStrategy = function(success, fail){
    exec(success, fail, 'AppInfo', 'getResetNotificationStrategy', []);
}

module.exports = new appInfo();

