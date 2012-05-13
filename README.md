nfc-app-launcher
================

Simple application to write AAR (Android Application Records) on NFC tags.

http://developer.android.com/guide/topics/nfc/nfc.html#aar

In order to use AAR, you must retrieve the package name of the application:
- go to the android market
- search for your app and check the URL
- the package name is the id parameters in the URL

For example: 
Goole Maps page : https://play.google.com/store/apps/details?id=com.google.android.apps.maps
The package name is : com.google.android.apps.maps 

If the application in not installed on the phone, the user will be redirected to the market page.


TODO :
- List installed applications
- Improve stability / check if NFC is available
- Open intent
- UI ...
- Icons
- Translation
