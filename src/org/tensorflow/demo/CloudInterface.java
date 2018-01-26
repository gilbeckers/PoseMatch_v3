package org.tensorflow.demo;

import android.os.AsyncTask;
import android.util.Log;

import com.algorithmia.APIException;
import com.algorithmia.AlgorithmException;
import com.algorithmia.Algorithmia;
import com.algorithmia.AlgorithmiaClient;
import com.algorithmia.algo.AlgoResponse;
import com.algorithmia.algo.Algorithm;
import com.algorithmia.data.DataDirectory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Gil on 26/01/2018.
 */

public class CloudInterface implements UploadInterface{
    private static final Logger LOGGER = new Logger();
    private String algoKey;
    private String algoUrl;

    // Response van algorithmia
    private JSONObject keypointsResponse = null;
    // keypoints van eerste persoon
    private JSONArray keypoints_person1;
    // beslissing van pose matching door server (  na findmatch()  )
    private String matchOrNot;
    private ArrayList<Keypoint> keypointListPerson1 = new ArrayList<>();

    private UploadImageActivity parent;

    //String algoInput = "\"{\"img\": \"jochen_foto1.jpg\"}\""; // {"img":"jochen_foto1.jpg"}
    private String algoInput;

    private AlgorithmiaClient client;

    public CloudInterface(UploadImageActivity parent){
        this.parent = parent;
        algoKey = Helper.getConfigValue(parent, "algoKey"); // Not included in .git, get your own!
        algoUrl = "bilgeckers/OpTFpy3_v2/1.0.6";
        algoInput = "{"
                + "\"img_file\":\"data://bilgeckers/multiperson_matching/"+ ImageFrameHolder.getInstance().getImgFileToUpload().getName() +"\""
                + "}";

        client = Algorithmia.client(algoKey);
    }

    public void findMatch(File imgToUpload, int modelId){
        LOGGER.e("Starting Algorithmia call");
        new AlgorithmiaTask(algoKey, algoUrl, client).execute(algoInput);
    }

    private class AlgorithmiaTask extends AsyncTask<String, Void, AlgoResponse> {
        private static final String TAG = "AlgorithmiaTask";
        private AlgorithmiaClient client;
        private Algorithm algo;

        private String projectDirPath = "data://bilgeckers/multiperson_matching";
        private DataDirectory projectDir;

        public AlgorithmiaTask(String api_key, String algoUrl, AlgorithmiaClient client) {
            super();
            this.client = client;
            this.algo = client.algo(algoUrl);
        }

        @Override
        protected AlgoResponse doInBackground(String... inputs) {
            Log.e(TAG, "--STARTING API CALL");
            Log.e(TAG, "--with input: " + inputs[0]);

            try {
                // Instantiate a DataDirectory object, set your data URI and call create
                projectDir = client.dir(projectDirPath);
                // Create your data collection if it does not exist
                if (projectDir.exists() == false) {
                    projectDir.create();
                    LOGGER.e("Successfully created project dir on algorithmia");
                }

                // Upload image
                projectDir.putFile(ImageFrameHolder.getInstance().getImgFileToUpload());
                LOGGER.e("###### Image uploaded");
                AlgoResponse response = algo.pipeJson(inputs[0]);
                Log.e(TAG, "Response arrived! : " + response.toString());
                //Log.e(TAG, response.asJsonString());

                return response;
            } catch(APIException e) {
                // Connection error
                Log.e(TAG, "Algorithmia API Exception", e);
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(AlgoResponse response) {
            String result = null;
            Log.e(TAG, "Response arrived! : " + response.toString());

            try {
                keypointsResponse = new JSONObject(response.asJsonString());
            } catch (JSONException e) {
                e.printStackTrace();
                LOGGER.e("ERROR IN JSON OBJECT CONVERSION");
            } catch (AlgorithmException e) {
                e.printStackTrace();
            }

            parseKeypointsJSON();
            parseMatchJSON();

            //signal parent activity upload is ready
            parent.signalImgUploadReady(true, "cloud");

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

    private void parseMatchJSON() {
        try {
            //String match = keypointsResponse.getJSONObject("match").toString();
            //matchOrNot =  keypointsResponse.getJSONArray("match").toString();
            //LOGGER.e("match: " + keypointsResponse.getBoolean("match"));
            matchOrNot = keypointsResponse.getString("match");
            Log.d("APP" , "----match result: " + matchOrNot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //parsen van keypoints
    public void parseKeypointsJSON(){
        try {
            JSONArray ar_people = keypointsResponse.getJSONArray("people");
            Log.d("APP", ar_people.toString());

            //get body keypoints of first person
            JSONArray ar_person1 = ar_people.getJSONObject(0).getJSONArray("pose_keypoints");
            keypoints_person1 = ar_person1;
            Log.d("APP", ar_person1.toString());

            for(int kp = 0; kp<keypoints_person1.length(); kp = kp+3){
                try {
                    double x = Double.parseDouble(keypoints_person1.get(kp).toString());
                    double y = Double.parseDouble(keypoints_person1.get(kp+1).toString());
                    keypointListPerson1.add(new Keypoint(x, y));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getKeypointsResponse() {
        return keypointsResponse;
    }

    public void setKeypointsResponse(JSONObject keypointsResponse) {
        this.keypointsResponse = keypointsResponse;
    }

    public JSONArray getKeypoints_person1() {
        return keypoints_person1;
    }

    public void setKeypoints_person1(JSONArray keypoints_person1) {
        this.keypoints_person1 = keypoints_person1;
    }

    public ArrayList<Keypoint> getKeypointListPerson1() {
        return keypointListPerson1;
    }

    public void setKeypointListPerson1(ArrayList<Keypoint> keypointListPerson1) {
        this.keypointListPerson1 = keypointListPerson1;
    }

    public String isMatchOrNot() {
        return matchOrNot;
    }

}
