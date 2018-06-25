package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.env.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gil on 26/11/2017.
 */

public class ServerInterface implements UploadInterface{
    // json antwoord van server op img http post (bevat gedetecteerde keypoints)
    private JSONObject keypointsResponse = null;
    // keypoints van eerste persoon
    private JSONArray keypoints_person1;

    // beslissing van pose matching door server (  na findmatch()  )
    private String matchOrNot;



    private double sp_score;
    private double mp_score;
    private double us_score;

    //private HashMap<String, Bitmap> modelPoses = new HashMap<>();
    private ArrayList<String> modelPoses = new ArrayList<>();

    private ArrayList<Keypoint> keypointListPerson1 = new ArrayList<>();

    // Activity parent; wordt gebruikt om terug te singallen als async http post klaar is
    private UploadImageActivity parentCallback;

    public ServerInterface(UploadImageActivity parent){
        parentCallback = parent;
    }

    public ServerInterface(){};

    // asynch http post call dus met callback werken -> voor wanneer server terug antwoord
    public void findMatch(File imgToUpload, int modelId){
        String Url = "http://1dr8.be/findmatch";
        //If any auth is needed
        String username = "username";
        String password = "password";

        // Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(username, password);
        client.addHeader("enctype", "multipart/form-data");
        RequestParams params = new RequestParams();
        try {
            //params.put("pic", storeImage(imgToUpload));
            params.put("file", imgToUpload);
            params.put("id", modelId);
        } catch (FileNotFoundException e) {
            Log.d("MyApp", "File not found!!!" + imgToUpload.getAbsolutePath());
        }

        client.post(Url, params, new JsonHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                //Log.d(MainActivity.TAG, "PROGRESS:   writen: " + bytesWritten + "   totalsize: " + totalSize );

                final long bytes = bytesWritten;
                final long total = totalSize;
                parentCallback.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // werkt niet!!
                        ((TextView) parentCallback.findViewById(R.id.txt_status)).setText("Uploading ... " + bytes/total + "% complete");
                        //Log.d(MainActivity.TAG, "###In UI Thread!" );
                        //parentCallback.signalImgUploadProgress(bytesWritten, totalSize);
                    }
                });

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {

                //Do what's needed
                Log.d("APP ", "Gelukt!!!:");
                Log.d("APP", "antwoord: " + responseBody.toString());

                keypointsResponse = responseBody;
                parseKeypointsJSON();
                parseMatchJSON();

                //signal parent activity upload is ready
                parentCallback.signalImgUploadReady(true, "server");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable error) {
                //Print error
                Log.d("APP", errorResponse + "   " + error.toString());
                Log.d("APP", "----ERROORRRRRRRRRR");
            }
        });
    }

    public ArrayList<String> getAllModels(MainActivity parent){
        String Url = "http://1dr8.be/getAllPoses";
        //If any auth is needed
        String username = "username";
        String password = "password";

        // Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(username, password);
        client.addHeader("enctype", "multipart/form-data");

        modelPoses.clear();

        client.get(Url, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseBody) {

                //Do what's needed
                Log.d("APP ", "Gelukt!!!:");
                Log.d("APP", "antwoord: " + responseBody.toString());
                String pic1 = "";

                try {
                    //pic1 = responseBody.getJSONObject(0).getString("foto");
                    //pic1 = responseBody.getJSONObject(0);

                    for(int i=0; i<responseBody.length();i++){
                        pic1 = responseBody.getJSONObject(i).getString("foto");

                        final byte[] decodedBytes = Base64.decode(pic1.getBytes(), 0);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        modelPoses.add(responseBody.getJSONObject(i).getString("naam"));


                        String randomFileName = responseBody.getJSONObject(i).getString("naam")  + ".jpg"; //+ randomNum;

                        ImageUtils.saveBitmap(decodedByte, randomFileName);
                        String path_recorded_img = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tensorflow/"+ randomFileName;


                        Log.d("APP", "first pic: " + pic1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable error) {
                //Print error
                Log.d("APP", errorResponse + "   " + error.toString());
                Log.d("APP", "----ERROORRRRRRRRRR");
            }
        });


        Log.d("APP", "refresh readyy !!!");
        parent.displayFeedback("Models loaded");

        return modelPoses;
    }


    private void parseMatchJSON() {
        try {
            //String match = keypointsResponse.getJSONObject("match").toString();
            matchOrNot =  String.valueOf(keypointsResponse.getBoolean("match"));
            sp_score = (double) keypointsResponse.getJSONArray("SP").get(0);
            mp_score = (double) Math.round(keypointsResponse.getDouble("MP")*100.0)/100.0;
            us_score = keypointsResponse.getDouble("US");
            Log.d("APP" , "----match result: " + matchOrNot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // oude functie toen findMatch() nog niet bestond en server enkel keypoints terug gaf.
    public void uploadNewModel(File imgToUpload, final MainActivity parent) {
        String Url = "http://1dr8.be/uploadPose";
        //If any auth is needed
        String username = "username";
        String password = "password";

        // Bitmap compressedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(username, password);
        client.addHeader("enctype", "multipart/form-data");
        final RequestParams params = new RequestParams();
        try {
            //params.put("pic", storeImage(imgToUpload));
            params.put("file", imgToUpload);
        } catch (FileNotFoundException e) {
            Log.d("MyApp", "File not found!!!" + imgToUpload.getAbsolutePath());
        }
        client.post(Url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                //Do what's needed
                Log.d("APP ", "Gelukt!!!:");
                Log.d("APP", "antwoord: " + responseBody.toString());

                try {
                    parent.displayFeedback("new model added with id " + String.valueOf(responseBody.getInt("id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable error) {
                //Print error
                Log.d("APP", errorResponse + "   " + error.toString());
                Log.d("APP", "----ERROORRRRRRRRRR");
                parent.displayFeedback(errorResponse.toString());
            }
        });
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

    public double getSp_score() {
        return sp_score;
    }

    public double getMp_score() {
        return mp_score;
    }

    public double getUs_score() {
        return us_score;
    }
}