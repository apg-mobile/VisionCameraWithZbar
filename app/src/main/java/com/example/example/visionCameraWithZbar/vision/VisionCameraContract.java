package com.example.example.visionCameraWithZbar.vision;

/**
 * Created by X-tivity on 1/10/2017 AD.
 */

public interface VisionCameraContract {

    interface View {
        void setCommunicator(Communicator comm);

        Communicator getCommunicator();

        boolean isAutoFocus();

        boolean isUseFlash();

        boolean hasCameraPermission();

        boolean shouldShowRequestPermissionRationale();

        void requestCameraPermission();

        void showPermissionRationale();

        void showPermissionFailDialog();

        void startCameraSource();

        void stopPreview();

        void releaseCamera();

        VisionCameraRemote getRemote();

        void registerOnUsbListener();

        void unregisterOnUsbListener();

        void showPauseCameraView();

        void hidePauseCameraView();

        boolean isHardwareKeyboardAvailable();

        void resumePreview();
    }

    interface Presenter {
        void setView(View view);

        void init();

        void onResume();

        void onPause();

        void onDetach();

        void onPermissionGranted();

        void onPermissionFail();

        void onResolvePermissionClick();

        void onDetectBarcode(String rawValue);

        void onUsbAttached(boolean b);

        void onRemotePauseCamera();

        void onRemoteResumeCamera();
    }

    interface Communicator {
        void onCameraReady(VisionCameraRemote remote);

        void onShutter();

        void onDetectBarcode(String barcode);

        void onPictureTaken(byte[] bytes);

        void onPermissionDenied();

        void onUsbAttached(boolean hardwareKeyboardAvailable, VisionCameraRemote remote);

    }
}
