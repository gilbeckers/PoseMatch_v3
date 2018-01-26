package org.tensorflow.demo;

import org.json.JSONArray;

import java.io.File;

/**
 * Created by Gil on 26/01/2018.
 */

interface UploadInterface {

    public String isMatchOrNot();

    public JSONArray getKeypoints_person1();

    public void findMatch(File imgToUpload, int modelId);
}
