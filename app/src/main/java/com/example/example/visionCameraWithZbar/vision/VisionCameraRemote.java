package com.example.example.visionCameraWithZbar.vision;

/**
 * Created by X-tivity on 1/10/2017 AD.
 */

public interface VisionCameraRemote {

    void startDetectBarcode();

    void stopDetectBarcode();

    void pauseCamera();

    void resumeCamera();
}
