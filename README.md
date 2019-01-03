# MLKitScanner

*WIP* *WIP* *WIP*

Nodes scanner library wrapping the Firebase ML Kit

## 🔧 Installation
For now, just drag the ´mlkitscannerlib´ into your project

## Description

The libray is written in Kotlin, and is still under construction. For now it's possible to recognize text and barcodes/QR-codes.

## Getting Started

There's only a couple of steps to set up the scanner.

##### Step 1) Install the library

##### Step 2) Set up Firebase in your project

Follow the instructions here: https://firebase.google.com/docs/android/setup

- Make sure to copy the Google-Service.json file to your project and add classpath 'com.google.gms:google-services:4.0.1' in project dependencies

##### Step 3a - fragment)

Create a FrameLayout:

```XML
<FrameLayout
    android:id="@+id/barcodeCameraContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/navy"
    android:layout_weight="1"/>
```

```
val fragment = CameraFragment.newInstance(ProcessorType.Barcode, this)

activity.supportFragmentManager
        .beginTransaction()
        .add(R.id.barcodeCameraContainer, fragment, "BARCODE_SCANNER_FRAGMENT")
        .commit()
```

Extend your fragment with `MLKitScannerLibContract.CameraFragmentOutput` to get `onScannerResult` and `onScannerError` functions.

##### Step 3b - activity)

Create a custom layout:

```XML
    <dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
            android:id="@+id/custom_camera_source_preview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginBottom="8dp"
            android:layout_marginEnd="8dp"
            android:layout_marginStart="8dp"
            android:layout_marginTop="8dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">

        <dk.nodes.mlkitscannerlib.other.GraphicOverlay
                android:id="@+id/custom_graphics_overlay"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

    </dk.nodes.mlkitscannerlib.camera.CameraSourcePreview>
```

Or use the default layout.

```
val intent = Intent(this, CameraActivity::class.java)

//CameraActivity.setup(intent, null, null, null, ProcessorType.Barcode) //This will default to CameraActivity's layout.
CameraActivity.setup(intent, R.id.custom_camera_source_preview, R.id.custom_graphics_overlay, R.layout.custom_camera_layout, scannerType)

startActivityForResult(intent, CameraActivity.requestCode)
```

```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

   if (requestCode == CameraActivity.requestCode) {
       if (resultCode == Activity.RESULT_OK) {
           val result = data?.getStringExtra("result")
                
       }
       if (resultCode == Activity.RESULT_CANCELED) {

       }
   }
}
```

This library is still under progress, but feel free to use it as you want to

## 💻 Developers
- @nharbo
