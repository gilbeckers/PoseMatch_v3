package org.tensorflow.demo;



/**
 * Created by Gil on 26/01/2018.
 */

public class Settings {
    private static final Settings instance = new Settings();
    public static Settings getInstance(){
        return instance;
    }


    private String uploadDestination = "server";  // Server of cloud

    public String getUploadDestination() {
        return uploadDestination;
    }

    public void setUploadDestination(String uploadDestination) {
        this.uploadDestination = uploadDestination;
    }


    public String toggleUploadDestination() {
        if(uploadDestination.equals("server")){
            uploadDestination = "cloud";
            return "cloud";
        }
        else{
            uploadDestination = "server";
            return "server";
        }
    }
}
