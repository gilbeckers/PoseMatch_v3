package org.tensorflow.demo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.util.ArrayList;

public class UploadImageActivity extends AppCompatActivity {

    private static final Logger LOGGER = new Logger();
    private TextView txtStatus;
    private ImageView imageView;
    private ServerInterface restClient;
    private CloudInterface cloudClient;
    private Bitmap image;


    static {
        if(!OpenCVLoader.initDebug()){
            Log.e(CameraActivity.TAG, "OpenCV not loaded");
        } else {
            Log.e(CameraActivity.TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtStatus.setText("Start upload...");

        // Get the Intent that started this activity and extract the img (File)
        File pictureFile = (File) getIntent().getExtras().get(MainActivity.EXTRA_IMAGE);
        int modelId = (int) getIntent().getExtras().get(MainActivity.EXTRA_MODEL_POSE);

        imageView = (ImageView) findViewById(R.id.imageView);
        image = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
        imageView.setImageBitmap(image);

        if(Settings.getInstance().getUploadDestination().equals("cloud"))
        {
            // -- CLOUD UPLOAD ----
            LOGGER.e("Starting upload to CLOUD");
            txtStatus.setText("Start upload... First call can take a while because server needs to wake up");
            ImageFrameHolder.getInstance().setImgFileToUpload(pictureFile);
            cloudClient = new CloudInterface(this);
            cloudClient.findMatch(pictureFile, modelId);

        }
        else{
            // ---- SERVER UPLOAD -----
            LOGGER.e("Starting upload to SERVER JOCHEN");
            restClient = new ServerInterface(this);
            restClient.findMatch(pictureFile, modelId);

        }






    }

    @Override
    public void onPause() {
        //LOGGER.e("@@Uploading Activity on Pause");
        //CameraActivity.UPLOADING = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        //LOGGER.e("@@Uploading Activity RESUMED");
        //CameraActivity.UPLOADING = true;
        super.onResume();
    }

    public void signalImgUploadProgress(long bytesWritten, long totalSize){
        txtStatus = (TextView) findViewById(R.id.txt_status);
        txtStatus.setText("Uploading ... " + bytesWritten/totalSize + "% complete");

    }


    // is called by ServerInteface when async HTTP POST is finished
    public void signalImgUploadReady(boolean result, String host){
        if(result) {
            ArrayList<Keypoint> keypoints = null;

            // Server jochen
            if(host.equals("server")) {
                txtStatus.setText("Upload finished met jochen server, match: " + restClient.isMatchOrNot() + " \npose: " + restClient.getMp_score());
                keypoints = restClient.getKeypointListPerson1();
            }
            // Cloud algorithmia
            else if(host.equals("cloud")){
                txtStatus.setText("Upload finished met cloud, match: " + cloudClient.isMatchOrNot());
                keypoints = cloudClient.getKeypointListPerson1();
            }

            // TODO add error handling if keypoints is null
            Bitmap newImg = PlotKeyPoints.drawKeypoints(image, keypoints);
            imageView.setImageBitmap(newImg);
            //CameraActivity.UPLOADING = false;
        }
    }


}
