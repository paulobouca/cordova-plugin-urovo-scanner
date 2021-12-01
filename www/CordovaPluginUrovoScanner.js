var exec = cordova.require("cordova/exec");

module.exports = {
    scan: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'CordovaPluginUrovoScanner', 'scan', []);
    },
    cancel: function () {
        exec(null, null, 'CordovaPluginUrovoScanner', 'cancel', []);
    }
};