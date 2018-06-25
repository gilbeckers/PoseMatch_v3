package org.tensorflow.demo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.demo.env.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChooseModelPose extends AppCompatActivity {

    private Map<Integer, String> modelMap = new LinkedHashMap<>();
    private TableLayout modelListTable;
    private Toolbar myToolbar;


    private static final Logger LOGGER = new Logger();

    private ArrayList<String> modelPoses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_model_pose);
        modelPoses = (ArrayList<String>) getIntent().getExtras().get(MainActivity.MODEL_POSES);



        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        DrawerUtil.getDrawer(this, myToolbar);


        //modelPoses = server.getAllModels();



    }

    @Override
    protected void onResume() {
        super.onResume();

        modelListTable = (TableLayout) findViewById(R.id.modelListTable);
        fillModelList();
        createModelListTableView();
    }

    public void refreshModels(View view){
        Toast toast = Toast.makeText(this, "Refreshing models ...", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent();
        intent.putExtra(MainActivity.REFRESH_MODELS, 1);
        setResult(RESULT_OK, intent);
        finish();


    }

    // onClick()-handler voor imageButton
    View.OnClickListener chooseModel(final ImageButton button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();
                Intent intent = new Intent();
                LOGGER.i("Model chosen met id: " + id);
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, id);
                intent.putExtra(MainActivity.REFRESH_MODELS, -1);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private void fillModelList(){
        modelMap.put(0, "kever17");
        //modelMap.put(2, "model2");
        //modelMap.put(3, "model3");
        //modelMap.put(4, "model4");
        //modelMap.put(5, "model5");
        //modelMap.put(6, "model6");
    }

    private void createModelListTableView(){
        //Iterator it = modelPoses.entrySet().iterator();
        for(String modelPose: modelPoses){
            //Map.Entry pair = (Map.Entry)it.next();

            String modelName = modelPose;
            String modelImagePath= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tensorflow/"+ modelName + ".jpg";
            Bitmap modelImage = BitmapFactory.decodeFile(modelImagePath);

            LOGGER.i("modelpose :  " + modelName);
            int modelId = Integer.valueOf(modelName.replace("pose", ""));
            //Bitmap modelImage = (Bitmap) pair.getValue();

            TableRow aRow = new TableRow(this);
            ImageButton imageButton = new ImageButton(this);
            imageButton.setImageBitmap(modelImage);
            imageButton.setId(modelId);
            imageButton.setOnClickListener(chooseModel(imageButton));
            imageButton.setAdjustViewBounds(true);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            TextView txtView = new TextView(this);
            txtView.setText(modelName);
            aRow.addView(txtView);
            TableRow aRow2 = new TableRow(this);
            aRow2.addView(imageButton);
            modelListTable.addView(aRow);
            modelListTable.addView(aRow2);



        }
    }

    private void createModelListTableView_old(){
        Iterator it = modelMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            String modelName = (String) pair.getValue();
            int modelId = (int) pair.getKey();
            //LOGGER.i("Image model name: " + modelName);
            //LOGGER.i("Image model id: " + modelId);
            int drawableResourceId = this.getResources().getIdentifier(modelName, "drawable", this.getPackageName());

            TableRow aRow = new TableRow(this);
            ImageButton imageButton = new ImageButton(this);
            imageButton.setImageResource(drawableResourceId);
            imageButton.setId(modelId);
            imageButton.setOnClickListener(chooseModel(imageButton));
            imageButton.setAdjustViewBounds(true);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            TextView txtView = new TextView(this);
            txtView.setText(modelName);
            aRow.addView(txtView);
            TableRow aRow2 = new TableRow(this);
            aRow2.addView(imageButton);
            modelListTable.addView(aRow);
            modelListTable.addView(aRow2);



        }
    }
}
