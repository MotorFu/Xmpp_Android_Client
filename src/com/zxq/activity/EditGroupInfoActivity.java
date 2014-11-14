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
import com.zxq.ui.switcher.Switch;
import com.zxq.util.LogUtil;
import com.zxq.xmpp.R;

/**
 * Created by zxq on 2014/9/16.
 */
public class EditGroupInfoActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;

    private EditText groupName;
    private EditText groupDescript;
    private EditText groupTitle;
    private Spinner groupNumber;
    private Switch passwordProtect;
    private Button btnAlterPassword;
    private Button btnDeleteGroup;
    private Button btnSaveInfo;

    private XmppService mXmppService;



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
        setContentView(R.layout.activity_edit_group_info);
        initView();
    }

    private void setupData() {

    }

    private void initView() {
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        groupName = (EditText) findViewById(R.id.edit_group_info_text_descript_room_name);
        groupDescript = (EditText) findViewById(R.id.edit_group_info_text_descript);
        groupTitle = (EditText) findViewById(R.id.edit_group_info_text_title);
        groupNumber = (Spinner) findViewById(R.id.edit_group_info_spinner_room_person_number);
        passwordProtect = (Switch) findViewById(R.id.edit_group_info_switch_password_protect);
        btnAlterPassword = (Button) findViewById(R.id.edit_group_info_btn_set_room_password);
        btnDeleteGroup = (Button) findViewById(R.id.edit_group_info_btn_delete_group);
        btnSaveInfo = (Button) findViewById(R.id.edit_group_info_btn_save_info);
        String[] mNumberItems = getResources().getStringArray(R.array.group_info_number_array);
        ArrayAdapter<String> mNumberItemsAdapter=new ArrayAdapter<String>(this,R.layout.group_info_number_item, mNumberItems);
        groupNumber.setAdapter(mNumberItemsAdapter);
        acitonBarTitle.setText("修改聊天室信息");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGroupInfoActivity.this.finish();
            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
