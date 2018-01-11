package org.tensorflow.demo;

/**
 * Created by Gil on 11/01/2018.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.algorithmia.APIException;
import com.algorithmia.AlgorithmException;
import com.algorithmia.Algorithmia;
import com.algorithmia.AlgorithmiaClient;
import com.algorithmia.algo.AlgoResponse;
import com.algorithmia.algo.Algorithm;

/**
 * AsyncTask helper to make it easy to call Algorithmia in the background
 * @param <T> the type of the input to send to the algorithm
 */
public abstract class AlgorithmiaTask<T> extends AsyncTask<String, Void, AlgoResponse> {
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
        if(inputs.length == 1) {
            String input = inputs[0];
            // Call algorithmia
            try {
                AlgoResponse response = algo.pipe(input);
                Log.e(TAG, response.asJsonString());
                return response;
            } catch(APIException e) {
                // Connection error
                Log.e(TAG, "Algorithmia API Exception", e);
                return null;
            } catch (AlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // Too many inputs
            return null;
        }
    }
}
