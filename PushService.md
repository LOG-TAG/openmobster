### Introduction ###
This tutorial covers the cross platform Push service. To clarify cross platform, what it means is that the Push service provides a single API regardless of the platform associated with the device. The Push Engine takes care of the platform-specific Push implementation. E.g. Push in Android is based on a persistent socket connection, while Push on the iPhone is based on the Apple Push Notification Service (APN).


#### Step 1: Send a Push ####
```
Device device = DeviceController.getInstance().read(deviceId);
if(device != null)
{
   PushService pushService = PushService.getInstance();
   pushService.push(device.getIdentity().getPrincipal(), appId, "Test   Push", "Test Push", "Test Push");
}
```

Initially a device is obtained using its device id. If a device is found, it can now receive Push. An instance of the **PushService** is obtained.
Now, a Push is finally sent.

  * parameter 1: the user that is the target of the Push
  * parameter 2: the application on the device that is associated with this Push
  * parameter 3: The core message of the Push
  * parameter 4: The title of the Push
  * parameter 5: A detailed message associated with the Push


---


Before the PushService works you must make sure all your platform specific configuration is setup.

  * **Android**: No configuration needed

  * **iPhone**: Apple Push Notification setup is needed as described [here](iPhonePushSetup.md)