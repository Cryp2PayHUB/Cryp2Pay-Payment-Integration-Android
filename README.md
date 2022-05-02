
# Cryp2Pay Payment Integration Library for Android

A Library for integrating Cryp2Pay wallet & Payment system for your business.



## Implementation

Note: please ensure to register your business with [Cryp2Pay Merchant app](https://play.google.com/store/apps/details?id=com.cryp2pay.merchant2022).

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```        
### Step 2. Add the dependency
```groovy
dependencies {
	         implementation 'com.github.Cryp2PayHUB:Cryp2Pay-Payment-Integration-Android:0.9'
	}
```

## Usage
### Java
#### To open Cryp2Pay layout in your app simply follow this code.
```groovy
// Call the constructor
new Cryp2Pay(Context context, Class resultActivity, double amount,
            String phone_number, String merchantID);        
```
Defination of above parameters:

context: Your context or resultActivity

resultActivity: Your resultActivity

amount: pass the INR amount

phone_number: pass the registred phone number of Cryp2Pay Merchant app.

merchantID: To get merchantID of your business account kindly mail us at 
support@cryp2.in

#### To get transaction info simply add following code in your resultActivity.

```groovy
Intent intent = getIntent();
String intentResult = intent.getStringExtra("result");
// do something with it
```

