package com.example.example.visionCameraWithZbar.vision;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.example.visionCameraWithZbar.R;
import com.example.example.visionCameraWithZbar.custom.MobyDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;


import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by X-tivity on 1/10/2017 AD.
 */
@EFragment
public class VisionCameraFragment extends Fragment implements
        VisionCameraContract.View,
        VisionCameraRemote{

    private static final String TAG = VisionCameraFragment.class.getName();
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private VisionCameraContract.Communicator comm;
    private UsbReceiver usbReceiver;

    @Bean
    protected VisionCameraPresenter presenter;

    @FragmentArg
    protected boolean isUseFlash;
    @FragmentArg
    protected boolean isAutoFocus;

    private ZBarScannerView mScannerView;

    private List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        setUpFormats();
        return mScannerView;
    }


    @AfterViews
    protected void init() {

        MobyDialog permissionDialog = (MobyDialog)
                getChildFragmentManager().findFragmentByTag("dialog_no_permission");

        if (permissionDialog != null) {
            permissionDialog.setPositiveButton(listener);
        }

        presenter.setView(this);
        presenter.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onDetach() {
        presenter.onDetach();
        super.onDetach();
    }

    @Override
    public boolean isAutoFocus() {
        return isAutoFocus;
    }

    @Override
    public boolean isUseFlash() {
        return isUseFlash;
    }

    @Override
    public boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA);
    }

    @Override
    public void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        String[] permissions = {Manifest.permission.CAMERA};
        requestPermissions(permissions, RC_HANDLE_CAMERA_PERM);
    }

    @Override
    public void showPermissionRationale() {
        View.OnClickListener listener = view -> presenter.onResolvePermissionClick();

        Snackbar snackbar = Snackbar.make(mScannerView, getActivity().getResources().getString(R.string.permission_camera_rationale),
                Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.white))
                .setAction(R.string.ok, listener);

        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void showPermissionFailDialog() {
        new MobyDialog.Builder(getContext())
                .setTitle(getString(R.string.dialog_title_camera_permission))
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show(getChildFragmentManager(), "dialog_no_permission");
    }

    private MobyDialog.OnClickListener listener = (v, d) -> {
        d.dismiss();
        if (comm != null) {
            comm.onPermissionDenied();
        }
    };


    @Override
    public void startCameraSource() {
        if(mScannerView!=null) {
            mScannerView.setResultHandler(resultHandler);
            mScannerView.startCamera();
            mScannerView.setAutoFocus(true);
            mScannerView.setFormats(formats);
        }

    }

    ZBarScannerView.ResultHandler resultHandler = new ZBarScannerView.ResultHandler() {
        @Override
        public void handleResult(Result result) {
            presenter.onDetectBarcode(result.getContents());
        }

    };
    @Override
    public void stopPreview() {
        if(mScannerView!=null){
            mScannerView.stopCamera();
        }
    }

    @Override
    public void releaseCamera() {
        if(mScannerView!=null){
            mScannerView.stopCamera();
        }
        mScannerView = null;
    }

    @Override
    public VisionCameraRemote getRemote() {
        return this;
    }

    @Override
    public void registerOnUsbListener() {
        IntentFilter i = new IntentFilter();
        i.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        i.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbReceiver = new UsbReceiver();
        getContext().registerReceiver(usbReceiver, i);
    }

    @Override
    public void unregisterOnUsbListener() {
        getContext().unregisterReceiver(usbReceiver);
    }

    @Override
    public void showPauseCameraView() {

    }

    @Override
    public void hidePauseCameraView() {

    }

    @Override
    public boolean isHardwareKeyboardAvailable() {
        //return Apg.usb().isHardwareKeyboardAvailable(getContext());
        return true;
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            presenter.onPermissionGranted();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        presenter.onPermissionFail();
    }

    @Override
    public void startDetectBarcode() {
        if(isVisible() && mScannerView!=null) {
            mScannerView.resumeCameraPreview(resultHandler);
        }
    }

    @Override
    public void stopDetectBarcode() {
        if(mScannerView!=null){
            mScannerView.stopCamera();
        }
    }


    @Override
    public void pauseCamera() {
        presenter.onRemotePauseCamera();
    }

    @Override
    public void resumeCamera() {
        presenter.onRemoteResumeCamera();
    }

    @Override
    public void setCommunicator(VisionCameraContract.Communicator comm) {
        this.comm = comm;
    }

    @Override
    public VisionCameraContract.Communicator getCommunicator() {
        return comm;
    }

    public class UsbReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (presenter != null) {
                    presenter.onUsbAttached(true);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (presenter != null) {
                    presenter.onUsbAttached(false);
                }
            }
        }
    }

    private void setUpFormats(){
        // supported formats
        formats.add(BarcodeFormat.CODE39);
        formats.add(BarcodeFormat.CODE93);
        formats.add(BarcodeFormat.CODE128);
        formats.add(BarcodeFormat.QRCODE);

    }

}
