var BionContentProviderPlugin = function(){};

BionContentProviderPlugin.prototype.insertCredential = function(success, failure, args){
    cordova.exec(success, failure, "BionContentProviderPlugin", "insertCredential", args);
};

BionContentProviderPlugin.prototype.getCredential = function(success, failure, args) {
    cordova.exec(success, failure, "BionContentProviderPlugin", "getCredential", args);
};

BionContentProviderPlugin.prototype.updateCredential = function(success, failure, args) {
    cordova.exec(success, failure, "BionContentProviderPlugin", "updateCredential", args);
};

BionContentProviderPlugin.prototype.deleteCredential = function(success, failure, args) {
    cordova.exec(success, failure, "BionContentProviderPlugin", "deleteCredential", args);
};
//Plug in to Cordova
cordova.addConstructor(function() {
    if (!window.Cordova) {
        window.Cordova = cordova;
    };
    if(!window.plugins) window.plugins = {};
    window.plugins.BionContentProviderPlugin = new BionContentProviderPlugin();
});
