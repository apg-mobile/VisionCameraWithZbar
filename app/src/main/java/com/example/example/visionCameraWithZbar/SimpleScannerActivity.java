package com.example.example.visionCameraWithZbar;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.Toast;

import com.example.example.visionCameraWithZbar.vision.VisionCameraContract;
import com.example.example.visionCameraWithZbar.vision.VisionCameraFragment;
import com.example.example.visionCameraWithZbar.vision.VisionCameraFragment_;
import com.example.example.visionCameraWithZbar.vision.VisionCameraRemote;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_simple_scanner)
public class SimpleScannerActivity extends BaseScannerActivity {

    private VisionCameraRemote mVisionRemote;
    private String barcodeResult;


    VisionCameraContract.Communicator visionComm = new VisionCameraContract.Communicator() {
        @Override
        public void onCameraReady(VisionCameraRemote remote) {
            mVisionRemote = remote;
        }

        @Override
        public void onShutter() {

        }

        @Override
        public void onDetectBarcode(String barcode) {

            barcodeResult = barcode;
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(SimpleScannerActivity.this.getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mVisionRemote.stopDetectBarcode();

            navigateToResultActivity();

        }

        @Override
        public void onPictureTaken(byte[] bytes) {

        }

        @Override
        public void onPermissionDenied() {

        }

        @Override
        public void onUsbAttached(boolean hardwareKeyboardAvailable, VisionCameraRemote remote) {

        }

    };


    @AfterViews
    protected void init() {
        setupToolbar();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        VisionCameraFragment visionFragment = (VisionCameraFragment)
                getSupportFragmentManager().findFragmentByTag("vision");


        if (visionFragment == null) {
            visionFragment = VisionCameraFragment_.builder()
                    .isAutoFocus(true)
                    .isUseFlash(false)
                    .build();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, visionFragment, "vision")
                    .commit();
        }

        visionFragment.setCommunicator(visionComm);
    }

    private void navigateToResultActivity(){
        ResultActivity_.intent(this)
                .result(barcodeResult)
                .start();
    }

}
