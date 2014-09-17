package com.zxq.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.zxq.util.DialogUtil;
import com.zxq.util.PreferenceConstants;
import com.zxq.util.PreferenceUtils;
import com.zxq.vo.PersonEntityInfo;
import com.zxq.xmpp.R;
import org.jivesoftware.smackx.packet.VCard;

/**
 * Created by zxq on 2014/9/15.
 */
public class PersonInfoActivity extends Activity {
    private static final int PERSON_INFO_REQUEST_CODE = 0X211;
    private static final int PERSON_INFO_REQUEST_OK = 0X112;
    private static final String PERSON_INFO_REQUEST_KEY = "info_entity";
    private ImageView actionBarBack;
    private TextView acitonBarTitle;

    private ImageView imageIcon;
    private TextView textAccount;
    private TextView textName;
    private TextView textSignature;
    private TextView textQQ;
    private TextView textPhone;
    private TextView textEmail;
    private Button btnEditInfo;
    private Button btnEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        initView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERSON_INFO_REQUEST_CODE) {
            if (resultCode == PERSON_INFO_REQUEST_OK) {
                PersonEntityInfo personInfo = (PersonEntityInfo) data.getSerializableExtra(PERSON_INFO_REQUEST_KEY);
                textName.setText(personInfo.getName());
                textSignature.setText(personInfo.getSignaturn());
                textQQ.setText(personInfo.getQq());
                textPhone.setText(personInfo.getPhone());
                textEmail.setText(personInfo.getEmail());
            }
        }
    }

    private void initView() {
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        imageIcon = (ImageView) findViewById(R.id.person_info_icon);
        textAccount = (TextView) findViewById(R.id.person_info_text_account);
        textName = (TextView) findViewById(R.id.person_info_text_name);
        textSignature = (TextView) findViewById(R.id.person_info_text_signature);
        textQQ = (TextView) findViewById(R.id.person_info_text_qq);
        textPhone = (TextView) findViewById(R.id.person_info_text_phone);
        textEmail = (TextView) findViewById(R.id.person_info_text_email);
        btnEditInfo = (Button) findViewById(R.id.person_info_btn_edit_info);
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoToEditInfoActivity();
            }
        });
        btnEditPassword = (Button) findViewById(R.id.person_info_btn_edit_password);
        acitonBarTitle.setText("个人信息");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfoActivity.this.finish();
            }
        });
    }

    private void onGoToEditInfoActivity() {
        Intent intent = new Intent();
        intent.setClass(this, EditPersonInfoActivity.class);
        startActivityForResult(intent, PERSON_INFO_REQUEST_CODE);
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
