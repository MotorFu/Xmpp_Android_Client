package com.zxq.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.zxq.xmpp.R;

/**
 * Created by zxq on 2014/9/16.
 */
public class EditPersonInfoActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;

    private EditText inputName;
    private EditText inputSignature;
    private EditText inputQQ;
    private EditText inputPhone;
    private EditText inputEmail;
    private Button btnSaveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person_info);
        initView();
        setupData();
    }

    private void setupData() {

    }

    private void initView() {
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        inputName = (EditText) findViewById(R.id.edit_person_info_text_name);
        inputSignature = (EditText) findViewById(R.id.edit_person_info_text_signature);
        inputQQ = (EditText) findViewById(R.id.edit_person_info_text_qq);
        inputPhone = (EditText) findViewById(R.id.edit_person_info_text_phone);
        inputEmail = (EditText) findViewById(R.id.edit_person_info_text_email);
        btnSaveInfo = (Button) findViewById(R.id.edit_person_info_btn_save_info);
        acitonBarTitle.setText("修改用户信息");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPersonInfoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
