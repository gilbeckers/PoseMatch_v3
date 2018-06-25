package org.tensorflow.demo;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.demo.env.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gil on 26/11/2017.
 */

public class PlotKeyPoints {
    private static final Logger LOGGER = new Logger();

    private static final HashMap<String, Integer> CocoPart;
    private static final Integer[][] CocoColors = {{255, 0, 0}, {255, 85, 0}, {255, 170, 0}, {255, 255, 0}, {170, 255, 0}, {85, 255, 0}, {0, 255, 0},
            {0, 255, 85}, {0, 255, 170}, {0, 255, 255}, {0, 170, 255}, {0, 85, 255}, {0, 0, 255}, {85, 0, 255},
            {170, 0, 255}, {255, 0, 255}, {255, 0, 170}, {255, 0, 85}};

    private static final Integer[][] CocoPairs = {
            {1, 2}, {1, 5}, {2, 3}, {3, 4}, {5, 6}, {6, 7}, {1, 8}, {8, 9}, {9, 10}, {1, 11},
            {11, 12}, {12, 13}, {1, 0}, {0, 14}, {14, 16}, {0, 15}, {15, 17}, {2, 16}, {5, 17}
            };

    private static final Integer[][] CocoPairsRender = {
            {1, 2}, {1, 5}, {2, 3}, {3, 4}, {5, 6}, {6, 7}, {1, 8}, {8, 9}, {9, 10}, {1, 11},
            {11, 12}, {12, 13}, {1, 0}, {0, 14}, {14, 16}, {0, 15}, {15, 17} };
    
    
    static
    {
        CocoPart = new HashMap<String, Integer>();
        CocoPart.put("Nose", 0);
        CocoPart.put("Neck",1);
        CocoPart.put("RShoulder" ,2);
        CocoPart.put("RElbow", 3);
        CocoPart.put("RWrist" ,4);
        CocoPart.put("LShoulder" ,5);
        CocoPart.put("LElbow" ,6);
        CocoPart.put("LWrist" , 7);
        CocoPart.put("RHip" , 8);
        CocoPart.put("RKnee" ,9);
        CocoPart.put("RAnkle", 10);
        CocoPart.put("LHip" , 11);
        CocoPart.put("LKnee" ,12);
        CocoPart.put("LAnkle" , 13);
        CocoPart.put("REye" , 14);
        CocoPart.put("LEye" , 15);
        CocoPart.put("REar" , 16);
        CocoPart.put("LEar" , 17);
        CocoPart.put("Background" , 18);
    }


    public static Bitmap drawKeypoints(Bitmap bitmap, ArrayList<Keypoint> keypoints){
        Mat src = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap,src);

        int radius = 6;

        for(int i=0; i<keypoints.size();i++){
            Keypoint kp = keypoints.get(i);


            if(kp.getX() == 0 || kp.getY()==0){
                continue;
            }

            Scalar circleColor = new Scalar(CocoColors[i][0],CocoColors[i][1],CocoColors[i][2],255);
            Imgproc.circle(src, new Point(kp.getX(),kp.getY()), radius+1, circleColor, radius+1);

            for(int pair_order=0; pair_order<CocoPairsRender.length;pair_order++){
                Integer[] pair = CocoPairsRender[pair_order];
                Scalar color = new Scalar(CocoColors[pair_order][0],CocoColors[pair_order][1], CocoColors[pair_order][2],255);
                Imgproc.line(src, new Point(keypoints.get(pair[0]).getX(), keypoints.get(pair[0]).getY()),
                        new Point(keypoints.get(pair[1]).getX(), keypoints.get(pair[1]).getY()), color, radius);
            }
        }

        Bitmap result = Bitmap.createBitmap(src.cols(),src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src,result);
        return result;
    }


}
