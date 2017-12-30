package org.tensorflow.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChooseModelPose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_model_pose);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        //DrawerUtil.getDrawer(this, myToolbar);
    }


    // onClick()-handler voor imageButton
    public void chooseModel(View view){
        int id = view.getId();

        Intent intent = new Intent();

        switch (id){
            case R.id.btn_pose_model1:
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, 1);
                break;
            case R.id.btn_pose_model2:
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, 2);
                break;
            case R.id.btn_pose_model3:
                intent.putExtra(MainActivity.EXTRA_MODEL_POSE, 99);
                break;
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
