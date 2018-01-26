package org.tensorflow.demo;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Gil on 25/01/2018.
 */

// DataHolder class for big BitMap object containing the captured frame
public class ImageFrameHolder {
    private static final ImageFrameHolder instance = new ImageFrameHolder();

    private Bitmap frame;
    private File imgFileToUpload;

    public Bitmap getFrame() {
        return frame;
    }

    public void setFrame(Bitmap frame) {
        this.frame = frame;
    }

    public static ImageFrameHolder getInstance(){
        return instance;
    }

    public File getImgFileToUpload() {
        return imgFileToUpload;
    }

    public void setImgFileToUpload(File imgFileToUpload) {
        this.imgFileToUpload = imgFileToUpload;
    }
}
