package com.example.example.visionCameraWithZbar.vision;

import org.androidannotations.annotations.EBean;

/**
 * Created by X-tivity on 1/10/2017 AD.
 */
@EBean
public class VisionCameraPresenter implements VisionCameraContract.Presenter {

    private VisionCameraContract.View mView;
    private boolean isNeedToShowDialogAfterRequestPermissionFail;

    @Override
    public void setView(VisionCameraContract.View view) {
        mView = view;
    }

    @Override
    public void init() {
        if (mView.getCommunicator() != null) {
            mView.getCommunicator().onCameraReady(mView.getRemote());
        }

        if (!mView.hasCameraPermission() && !mView.shouldShowRequestPermissionRationale()) {
            mView.requestCameraPermission();
        } else if (!mView.hasCameraPermission()) {
            mView.showPermissionRationale();
        }
    }

    @Override
    public void onResume() {

        if (mView.hasCameraPermission()) {
            mView.startCameraSource();

            if (mView.getCommunicator() != null) {
                mView.getCommunicator().onUsbAttached(
                        mView.isHardwareKeyboardAvailable(),
                        mView.getRemote());
            }
        }

        if (isNeedToShowDialogAfterRequestPermissionFail) {
            isNeedToShowDialogAfterRequestPermissionFail = false;
            mView.showPermissionFailDialog();
        }

        mView.registerOnUsbListener();
    }

    @Override
    public void onPause() {
      //  mView.unregisterOnUsbListener();
        mView.stopPreview();
    }

    @Override
    public void onDetach() {
        mView.releaseCamera();
    }

    @Override
    public void onResolvePermissionClick() {
        mView.requestCameraPermission();
    }

    @Override
    public void onPermissionGranted() {
        if (mView.getCommunicator() != null) {
            mView.getCommunicator().onCameraReady(mView.getRemote());
        }
    }

    @Override
    public void onPermissionFail() {
        isNeedToShowDialogAfterRequestPermissionFail = true;
    }


    @Override
    public void onDetectBarcode(String rawValue) {
        if (mView.getCommunicator() != null) {
            mView.getCommunicator().onDetectBarcode(rawValue);
        }
    }

    @Override
    public void onUsbAttached(boolean isAttached) {
        if (mView.getCommunicator() != null) {
            mView.getCommunicator().onUsbAttached(
                    isAttached,
                    mView.getRemote());
        }
    }

    @Override
    public void onRemotePauseCamera() {
        if (mView.hasCameraPermission()) {
            mView.showPauseCameraView();
            mView.stopPreview();
        }
    }

    @Override
    public void onRemoteResumeCamera() {
        if (mView.hasCameraPermission()) {
            mView.hidePauseCameraView();
            mView.startCameraSource();
        }
    }
}
