package org.tensorflow.demo;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.tensorflow.demo.env.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChooseModelPose extends AppCompatActivity {

    private Map<Integer, String> modelMap = new LinkedHashMap<>();
    private TableLayout modelListTable;
    private Toolbar myToolbar;

    private static final Logger LOGGER = new Logger();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_model_pose);

        modelListTable = (TableLayout) findViewById(R.id.modelListTable);
        fillModelList();
        createModelListTableView();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        DrawerUtil.getDrawer(this, myToolbar);
    }

    // onClick()-handler voor imageButton
    View.OnClickListener chooseModel(final ImageButton button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();
                Intent intent = new Intent();
                LOGGER.i("Model chosen met id: " + id);
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, id);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private void fillModelList(){
        modelMap.put(1, "model1");
        modelMap.put(2, "model2");
        modelMap.put(3, "model3");
        modelMap.put(4, "model4");
        modelMap.put(5, "model5");
        modelMap.put(6, "model6");
    }

    private void createModelListTableView(){
        Iterator it = modelMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException

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
