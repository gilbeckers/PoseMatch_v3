package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import org.opencv.android.OpenCVLoader;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import com.algorithmia.APIException;
import com.algorithmia.AlgorithmException;
import com.algorithmia.Algorithmia;
import com.algorithmia.AlgorithmiaClient;
import com.algorithmia.algo.AlgoResponse;
import com.algorithmia.algo.Algorithm;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MODEL_POSE = 1;
    public static final int REQUEST_POSE_IMAGE = 2;

    public static final String EXTRA_IMAGE = "INUPUT_IMAGE" ;
    public static final String EXTRA_MODEL_POSE = "MODEL_POSE" ;

    private static final Logger LOGGER = new Logger();

    private ImageView imageView;

    private Bitmap poseImage = null;

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

    private String apiResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageViewPose);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerUtil.getDrawer(this, myToolbar);

        // TODO move key to config-file
        String algoKey = Helper.getConfigValue(this, "algoKey");
        String algoUrl = "bilgeckers/OpTFpy3_v2/1.0.2";

        //String algoInput = "\"{\"img\": \"jochen_foto1.jpg\"}\""; // {"img":"jochen_foto1.jpg"}
        String algoInput = "{"
                + "\"img\": \"jochen_foto1.jpg\""
                + "}";

        new AlgorithmiaTask(algoKey, algoUrl).execute(algoInput);

        //AlgorithmiaClient Client = Algorithmia.client("simq5xUy+vzDpdxSDGB4ui/0b+v1");
    }


    private class AlgorithmiaTask extends AsyncTask<String, Void, AlgoResponse> {
        private static final String TAG = "AlgorithmiaTask";

        private String algoUrl;
        private AlgorithmiaClient client;
        private Algorithm algo;

        public AlgorithmiaTask(String api_key, String algoUrl) {
            super();
            this.algoUrl = algoUrl;
            this.client = Algorithmia.client(api_key);
            this.algo = client.algo(algoUrl);
        }

        @Override
        protected AlgoResponse doInBackground(String... inputs) {
            Log.e(TAG, "--STARTING API CALL");
            Log.e(TAG, "--with input: " + inputs[0]);

            try {
                //AlgoResponse response = algo.pipe(inputs[0]);
                AlgoResponse response = algo.pipeJson(inputs[0]);
                Log.e(TAG, "Response arrived! : " + response.toString());
                Log.e(TAG, response.asJsonString());
                return response;
            } catch(APIException e) {
                // Connection error
                Log.e(TAG, "Algorithmia API Exception", e);
                return null;
            } catch (AlgorithmException e) {
                e.printStackTrace();
                Log.e(TAG, "&&&&&&&&Algorithmia API Exception", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(AlgoResponse response) {
            String result = null;
            Log.e(TAG, "Response arrived! : " + response.toString());
            try {
                result = response.asJsonString();
            } catch (AlgorithmException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "API call finished, result: " + result);
        }

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "--STARTING API CALL2");
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
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

                final Intent data1 = data;

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //pictureFile = (File) data.getExtras().get(MainActivity.EXTRA_IMAGE);
                        Bitmap poseBitMap = (Bitmap) data1.getExtras().get(MainActivity.EXTRA_IMAGE);

                        LOGGER.e("--Rescaling bitmappppp");
                        poseImage = Bitmap.createScaledBitmap(poseBitMap, 1100, 1700, true );

                        ImageUtils.saveBitmap(poseImage);
                        LOGGER.e("--Bitmappppp Saved");

                        String path_recorded_img = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tensorflow/preview.png";
                        pictureFile = new File(path_recorded_img);

                        // ImageView van MainAct is set to BitMap in onResume()
                        //imageView.setImageBitmap(pose);
                    }
                });

                t.start();



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

    @Override
    public void onResume() {
        LOGGER.e("RESUMING MAINACt");

        if(poseImage != null){
            imageView.setImageBitmap(poseImage);
        }
        super.onResume();
    }


    public void viewModels(View view) {
        LOGGER.i("-----BROWSE MODELSS");
        // start new activity Choose Model Pose   (to server)
        Intent intent = new Intent(this, ChooseModelPose.class);
        startActivity(intent);
    }

    // Lame Camera-view (not in app)
    // ma dus zonder OD en dus full quality!!
    // TODO
    public void startCameraView2(View view) {
    }
}
