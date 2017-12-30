package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import org.opencv.android.OpenCVLoader;
import org.tensorflow.demo.env.Logger;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MODEL_POSE = 1;
    public static final int REQUEST_POSE_IMAGE = 2;

    public static final String EXTRA_IMAGE = "INUPUT_IMAGE" ;
    public static final String EXTRA_MODEL_POSE = "MODEL_POSE" ;

    private static final Logger LOGGER = new Logger();

    private ImageView imageView;

    static {
        if(!OpenCVLoader.initDebug()){
            Log.e(CameraActivity.TAG, "OpenCV not loaded");
        } else {
            Log.e(CameraActivity.TAG, "OpenCV loaded");
        }
    }

    // https://www.sitepoint.com/creating-a-cloud-backend-for-your-android-app-using-firebase/
    //private FirebaseAuth mFirebaseAuth;
    //private FirebaseUser mFirebaseUser;

    private Toolbar myToolbar;

    private File pictureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageViewPose);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerUtil.getDrawer(this, myToolbar);
    }

    public void startCameraView(View view) {
        Intent intent = new Intent(this, DetectorActivity.class );

        // Start CameraView DetectorActivity and signal .this when user clicks takePicture() -> onActivityResult() is triggered.
        this.startActivityForResult(intent, MainActivity.REQUEST_POSE_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.i("#######welle hier , resultcode= " + resultCode );

        if (requestCode == REQUEST_POSE_IMAGE) {
            if(resultCode == RESULT_OK) {
                LOGGER.e("Pose image ontvangennnn");
                pictureFile = (File) data.getExtras().get(MainActivity.EXTRA_IMAGE);

                Bitmap image = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                //image = Bitmap.createScaledBitmap(image, 700, 1100, true);
                imageView.setImageBitmap(image);

                // start new activity Choose Model Pose (to server)
                LOGGER.i("--Start ChooseModel Intent");
                Intent intent = new Intent(this, ChooseModelPose.class);
                this.startActivityForResult(intent, REQUEST_CODE_MODEL_POSE);
            }
        }

        if (requestCode == REQUEST_CODE_MODEL_POSE) {

            if(resultCode == RESULT_OK) {
                LOGGER.e("Model pose id ontvangen ");

                int modelId = (int) data.getExtras().get(MainActivity.EXTRA_MODEL_POSE);
                LOGGER.e("#### IN mainactivity  model id: " + modelId);


                // start UploadImageView
                Intent intent = new Intent(this, UploadImageActivity.class);
                intent.putExtra(MainActivity.EXTRA_IMAGE, pictureFile);
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, modelId);
                startActivity(intent);

            }
        }


    }


    public void viewModels(View view) {
        LOGGER.i("-----BROWSE MODELSS");
        // start new activity Choose Model Pose   (to server)
        Intent intent = new Intent(this, ChooseModelPose.class);
        startActivity(intent);
    }
}
