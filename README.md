# WonderToolkitAndroid

### WonderToolkitAndroid
The real essential reusable source code for Android.
It's intent to enjoyfully initiate project in simple steps.

### Installation guide (wpm)

1. Download all source code in ./wonder_modules directory
2. Create "wonderconf.json" and paste the configurations below.

```javascript
{
  "name": "WonderToolkitAndroidSample",
  "description": "Simple script to add WonderToolkitAndroid to your project",
  "host": "http://wondersaga.com/wonder_modules/",
  "version": "0.0.1",
  "dependencies": {
    "Android": {
      "projectPath": "./Your/Project/Path/Is/Here",
      "settingGradleSubPath": "/"
    }
  }
}
```

3. Type wpm "run WonderToolkitAndroid"