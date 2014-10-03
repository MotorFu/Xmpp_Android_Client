package com.zxq.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.*;
import com.zxq.service.XmppService;
import com.zxq.util.*;
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
    private PersonEntityInfo personEntityInfo;
    private ImageView imageIcon;
    private TextView textAccount;
    private TextView textName;
    private TextView textSignature;
    private TextView textQQ;
    private TextView textPhone;
    private TextView textEmail;
    private Button btnEditInfo;
    private Button btnEditPassword;
    private XmppService mXmppService;
    private String account;

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mXmppService = ((XmppService.XXBinder) service).getService();
            setupData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mXmppService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindXMPPService();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindXMPPService();
    }


    private void bindXMPPService() {
        LogUtil.i(RegisterActivity.class, "[SERVICE] Unbind");
        Intent mServiceIntent = new Intent(this, XmppService.class);
        bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindXMPPService() {
        try {
            unbindService(mServiceConnection);
            LogUtil.i(RegisterActivity.class, "[SERVICE] Unbind");
        } catch (IllegalArgumentException e) {
            LogUtil.e(RegisterActivity.class, "Service wasn't bound!");
        }
    }

    private void setupData() {
        VCard myInfo = mXmppService.getMyInfo();
        personEntityInfo = new PersonEntityInfo();
        personEntityInfo.setName(myInfo.getField(VCardConstants.KEY_NIKENAME));
        personEntityInfo.setSignature(myInfo.getField(VCardConstants.KEY_SIGNATURE));
        personEntityInfo.setQq(myInfo.getField(VCardConstants.KEY_QQ));
        personEntityInfo.setPhone(myInfo.getField(VCardConstants.KEY_PHONE));
        personEntityInfo.setEmail(myInfo.getField(VCardConstants.KEY_EMAIL));
        account = mXmppService.getXmppUserName();
        textAccount.setText(account);
        textName.setText(personEntityInfo.getName() == null?"(空)":personEntityInfo.getName());
        textSignature.setText(personEntityInfo.getSignature() == null?"(空)":personEntityInfo.getSignature());
        textQQ.setText(personEntityInfo.getQq() == null?"(空)":personEntityInfo.getQq());
        textPhone.setText(personEntityInfo.getPhone() == null?"(空)":personEntityInfo.getPhone());
        textEmail.setText(personEntityInfo.getEmail() == null?"(空)":personEntityInfo.getEmail());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERSON_INFO_REQUEST_CODE) {
            if (resultCode == PERSON_INFO_REQUEST_OK) {
                PersonEntityInfo personInfo = (PersonEntityInfo) data.getSerializableExtra(PERSON_INFO_REQUEST_KEY);
                textName.setText(personInfo.getName());
                textSignature.setText(personInfo.getSignature());
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
