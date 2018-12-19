# MLKitScanner

*WIP* *WIP* *WIP*

Nodes scanner library wrapping the Firebase ML Kit

## 🔧 Installation
For now, just drag the ´mlkitscannerlib´ into your project

## Description

The libray is written in Kotlin, and is still under construction. For now it's possible to recognize text and barcodes/QR-codes.

## Getting Started

There's only a couple of steps to set up the scanner.

##### Step 1) Create layout files, unless you wanna use the default

Basic setup could be like this:

```XML
    <dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
            android:id="@+id/camera_source_preview_main"
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
                android:id="@+id/graphics_overlay_main"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>
```

##### Step 2) Set up the scanner in your Activity

```Kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, CameraActivity::class.java)
//      CameraActivity.setup(intent, null, null, null, ProcessorType.Text) //This will default to CameraActivity's layout.
        CameraActivity.setup(intent, R.id.camera_source_preview_main, R.id.graphics_overlay_main, R.layout.activity_main, ProcessorType.Barcode)
        startActivityForResult(intent, CameraActivity.requestCode)
    }
```

This library is still under progress, but feel free to use it as you want to

## 💻 Developers
- @nharbo
