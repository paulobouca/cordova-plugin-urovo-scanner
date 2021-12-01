# Cordova Plugin Urovo Scanner
 Cordova Plugin Urovo Device Scanner

## Supported Platforms

- Android

## Installation

```bash
cordova plugin add com.urovoscanner
```

## Usage

### scan

```js
const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;

urovo.scan(
    data => {
      console.log('## Urovo Scanner barcode received: ', data);
      console.log('barcode: ', data.barcode);
      console.log('barcode type: ', data.type);
    },
    error => {
      console.log('## Urovo Scanner error: ', error);
    },
);
```

### cancel

Unbind the current callback function returned by scan function. Stop scanner device activity.

```js
urovo.cancel();
```
