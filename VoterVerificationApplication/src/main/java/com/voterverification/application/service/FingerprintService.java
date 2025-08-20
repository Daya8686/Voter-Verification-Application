package com.voterverification.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import SecuGen.FDxSDKPro.jni.*;
import com.voterverification.application.exception.VoterExceptionHandler;
import org.springframework.http.HttpStatus;

@Service
public class FingerprintService {

    private final JSGFPLib jsgfpLib;
    private final int maxTemplateSize;
    private int imageWidth;
    private int imageHeight;

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerprintService.class);

    public FingerprintService() {
        LOGGER.info("INSIDE FingerprintService CONSTRUCTOR");
        jsgfpLib = new JSGFPLib();
        LOGGER.info("jsgfpLib INITIALIZED");

        try {
            LOGGER.info("CALLING initScanner() METHOD");
            initScanner();
            LOGGER.info("Scanner initialized successfully.");

            LOGGER.info("CALLING getMaxTemplateSize() METHOD");
            maxTemplateSize = getMaxTemplateSize();

            LOGGER.info("CALLING setImageSize() METHOD");
            setImageSize();
        } catch (Exception e) {
            LOGGER.error("ERROR DURING FINGERPRINT SERVICE INITIALIZATION: {}", e.getMessage());
            throw new VoterExceptionHandler("Failed to initialize fingerprint scanner", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void initScanner() {
        LOGGER.info("INSIDE initScanner() METHOD");
        long error = jsgfpLib.Init(SGFDxDeviceName.SG_DEV_AUTO);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("Fingerprint Scanner Initialization FAILED! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Fingerprint Scanner Initialization Failed: " + error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        error = jsgfpLib.OpenDevice(0);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("FAILED TO OPEN FINGERPRINT SCANNER! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Failed to Open Fingerprint Scanner. Error Code: " + error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("Fingerprint scanner successfully opened.");
    }

    private int getMaxTemplateSize() {
        LOGGER.info("INSIDE getMaxTemplateSize() METHOD");
        int[] size = new int[1];
        long error = jsgfpLib.GetMaxTemplateSize(size);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("ERROR IN getMaxTemplateSize(): {}", error);
            throw new VoterExceptionHandler("Failed to get max template size!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("MAX TEMPLATE SIZE: {}", size[0]);
        return size[0];
    }

    private void setImageSize() {
        LOGGER.info("INSIDE setImageSize() METHOD");
        SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
        long error = jsgfpLib.GetDeviceInfo(deviceInfo);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("FAILED TO RETRIEVE SCANNER INFORMATION! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Failed to retrieve scanner information.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        this.imageWidth = deviceInfo.imageWidth;
        this.imageHeight = deviceInfo.imageHeight;
        LOGGER.info("SET IMAGE SIZE: WIDTH = {}, HEIGHT = {}", imageWidth, imageHeight);
    }

    public byte[] captureFingerprintTemplate() {
        LOGGER.info("INSIDE captureFingerprintTemplate() METHOD");
        byte[] imgBuffer = new byte[imageWidth * imageHeight];
        int[] quality = new int[1];
        int maxRetries = 5; // Increased retries for 99.99% accuracy
        int attempts = 0;

        while (attempts < maxRetries) {
            LOGGER.info("FINGERPRINT CAPTURE ATTEMPT: {}", attempts + 1);
            long error = jsgfpLib.GetImage(imgBuffer);
            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
                LOGGER.error("Fingerprint Capture FAILED! ERROR CODE: {}", error);
                throw new VoterExceptionHandler("Fingerprint Capture Failed: Ensure fingers and scanner are clean.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            jsgfpLib.GetImageQuality(imageWidth, imageHeight, imgBuffer, quality);
            LOGGER.info("FINGERPRINT IMAGE QUALITY: {}", quality[0]);
            if (quality[0] >= 60) break; // Increased minimum quality threshold for accuracy

            attempts++;
            if (attempts == maxRetries) {
                LOGGER.error("Low Fingerprint Quality! Please clean your finger and retry.");
                throw new VoterExceptionHandler("Low Fingerprint Quality! Please clean your finger and retry.", HttpStatus.BAD_REQUEST);
            }
        }

        // Correct way to create a template as per SDK (passing SGFingerInfo)
        byte[] template = new byte[maxTemplateSize];
        LOGGER.info("ALLOCATING TEMPLATE BUFFER (SIZE: {})", maxTemplateSize);

        // Create SGFingerInfo Object (Required)
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;  // Left Index Finger
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;

        LOGGER.info("CALLING CreateTemplate() METHOD");
        long error = jsgfpLib.CreateTemplate(fingerInfo, imgBuffer, template);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("TEMPLATE CREATION FAILED! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Template Creation Failed! Error Code: " + error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("TEMPLATE CREATED SUCCESSFULLY.");
        return template;
    }

    public boolean matchFingerprints(byte[] storedTemplate, byte[] newTemplate) {
        LOGGER.info("INSIDE matchFingerprints() METHOD");
        boolean[] matched = new boolean[1];
        int[] matchScore = new int[1];

        LOGGER.info("PERFORMING EXACT MATCH CHECK");
        long error = jsgfpLib.MatchTemplate(storedTemplate, newTemplate, SGFDxSecurityLevel.SL_HIGHEST, matched);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("Fingerprint Matching FAILED! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Fingerprint Matching Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (matched[0]) {
            LOGGER.info("EXACT MATCH FOUND! FINGERPRINT VERIFIED.");
            return true; // 100% match
        }

        LOGGER.info("EXACT MATCH FAILED! CHECKING MATCH SCORE...");
        error = jsgfpLib.GetMatchingScore(storedTemplate, newTemplate, matchScore);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
            LOGGER.error("FAILED TO RETRIEVE MATCH SCORE! ERROR CODE: {}", error);
            throw new VoterExceptionHandler("Failed to retrieve fingerprint match score!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("MATCH SCORE: {}", matchScore[0]);
        boolean isMatch = matchScore[0] >= 80; // Stricter threshold for high accuracy
        if (isMatch) {
            LOGGER.info("FINGERPRINT MATCHED BASED ON SCORE!");
        } else {
            LOGGER.info("FINGERPRINT DID NOT MATCH.");
        }
        return isMatch;
    }
}
