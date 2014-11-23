package com.zxq.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.zxq.service.XmppService;
import com.zxq.ui.switcher.Switch;
import com.zxq.util.DialogUtil;
import com.zxq.util.LogUtil;
import com.zxq.util.ToastUtil;
import com.zxq.xmpp.R;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zxq on 2014/9/16.
 */
public class EditGroupInfoActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;

    private EditText groupName;
    private EditText groupDescript;
    private Spinner groupNumber;
    private Switch passwordProtect;
    private Button btnAlterPassword;
    private Button btnDeleteGroup;
    private Button btnSaveInfo;
    private Form form;

    private XmppService mXmppService;
    private String mRoomJID;
    private MultiUserChat multiUserChat;


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
        mRoomJID = getIntent().getStringExtra(GroupChatActivity.MULTI_USER_CHAT_ROOM_JID);
        ToastUtil.showShort(this,mRoomJID);
        multiUserChat = mXmppService.getMultiUserChatByRoomJID(mRoomJID);
        //获取聊天室的配置表单

        Iterator<String> values;
        try {
            form = multiUserChat.getConfigurationForm();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        if(form == null){
            ToastUtil.showShort(this,"网络错误，无法显示信息");
            return;
        }

        FormField fieldRoomName = form.getField("muc#roomconfig_roomname");
        values = fieldRoomName.getValues();
        while (values.hasNext()){
            String tempStr = values.next();
            groupName.setText(tempStr);
        }
//        private EditText groupName;
//        private EditText groupDescript;
//        private Spinner groupNumber;
//        private Switch passwordProtect;
//        private Button btnAlterPassword;

        FormField fieldRoomDesc = form.getField("muc#roomconfig_roomdesc");
        values = fieldRoomDesc.getValues();
        while (values.hasNext()){
            String tempStr = values.next();
            groupDescript.setText(tempStr);
        }
        FormField fieldMaxUsers = form.getField("muc#roomconfig_maxusers");
        values = fieldMaxUsers.getValues();
        while (values.hasNext()){
            String tempStr = values.next();
            int tempNum = (Integer.parseInt(tempStr)/10) - 1;
            LogUtil.e("fieldMaxUsers："+tempNum);
            groupNumber.setSelection(tempNum);
        }
        FormField fieldPasswordProtectRoom = form.getField("muc#roomconfig_passwordprotectedroom");
        values = fieldPasswordProtectRoom.getValues();
        while (values.hasNext()){
            String tempStr = values.next();
            int tempNum = Integer.parseInt(tempStr);
            LogUtil.e("fieldPasswordProtectRoom："+tempNum);
            if(tempNum == 1){
                passwordProtect.setChecked(true);
                btnAlterPassword.setEnabled(true);
                btnAlterPassword.setBackgroundResource(R.drawable.chat_send_button_bg);
            }else{
                passwordProtect.setChecked(false);
                btnAlterPassword.setEnabled(false);
                btnAlterPassword.setBackgroundResource(R.drawable.common_btn_gray_actionsheet);
            }
        }

        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据原始表单创建一个要提交的新表单
                Form submitForm = form.createAnswerForm();
                //向提交的表单添加默认答复
                for(Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
                    FormField field = (FormField) fields.next();
                    if(!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }
                //重新设置聊天室名称（修改名称）
                submitForm.setAnswer("muc#roomconfig_roomname", groupName.getText().toString());
                //设置聊天室的新拥有者（群主转让）
//                List<String> owners = new ArrayList<String>();
//                owners.add("test@pc2010102716");
//                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                //设置密码 （设置密码保护，进入需要密码）
                //设置描述  （设置群描述）
                submitForm.setAnswer("muc#roomconfig_roomdesc", groupDescript.getText().toString());
                ToastUtil.showShort(EditGroupInfoActivity.this, "" + (groupNumber.getSelectedItemPosition()+1)*10);
                List<String> list = new ArrayList<String>();
                list.add((groupNumber.getSelectedItemPosition()+1)*10+"");
                submitForm.setAnswer("muc#roomconfig_maxusers", list);
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);

                //设置聊天室是持久聊天室，即将要被保存下来（是否持久此聊天室）该设置为默认设置
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                //发送已完成的表单到服务器配置聊天室 （发送至服务器）
                try {
                    multiUserChat.sendConfigurationForm(submitForm);
                    ToastUtil.showShort(EditGroupInfoActivity.this, "" + groupNumber.getSelectedItemPosition());
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });

//
//
//        if(form != null) {
//
//            Iterator<FormField> fields = form.getFields();
//            while (fields.hasNext()) {
//                FormField next = fields.next();
//                LogUtil.e("==========" + next.getLabel() + "==========");
//                Iterator<FormField.Option> options = next.getOptions();
//                LogUtil.e("----------options-----------");
//                while (options.hasNext()) {
//                    FormField.Option option = options.next();
//                    LogUtil.e("{");
//                    LogUtil.e("--------" + option.getLabel() + "----------");
//                    LogUtil.e("---" + option.getValue());
//                    LogUtil.e("}");
//                }
//                LogUtil.e("--------" + next.getDescription());
//                LogUtil.e("--------" + next.getType());
//                LogUtil.e("--------" + next.getVariable());
//                LogUtil.e("----------values-----------");
//                values = next.getValues();
//                while (values.hasNext()) {
//                    String value = values.next();
//                    LogUtil.e("{");
//                    LogUtil.e("--------" + value + "----------");
//                    LogUtil.e("}");
//                }
//                LogUtil.e("=======================================");
//
//            }
//        }

//            //根据原始表单创建一个要提交的新表单
//            Form submitForm = form.createAnswerForm();
//            //向提交的表单添加默认答复
//            for (Iterator<FormField> fields = form.getFields(); fields.hasNext(); ) {
//                FormField field = (FormField) fields.next();
//                if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
//                    submitForm.setDefaultAnswer(field.getVariable());
//                }
//            }
//            //重新设置聊天室名称（修改名称）
//            submitForm.setAnswer("muc#roomconfig_roomname", "Reserved4 Room");
//            //设置聊天室的新拥有者（群主转让）
//            List<String> owners = new ArrayList<String>();
//            owners.add("test@pc2010102716");
//            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
//            //设置密码 （设置密码保护，进入需要密码）
//            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
//            submitForm.setAnswer("muc#roomconfig_roomsecret", "reserved");
//            //设置描述  （设置群描述）
//            submitForm.setAnswer("muc#roomconfig_roomdesc", "新创建的reserved聊天室");
//            //设置聊天室是持久聊天室，即将要被保存下来（是否持久此聊天室）
//            //submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//            //发送已完成的表单到服务器配置聊天室 （发送至服务器）
//            try {
//                multiUserChat.sendConfigurationForm(submitForm);
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            }

    }

    private void initView() {
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        groupName = (EditText) findViewById(R.id.edit_group_info_text_descript_room_name);
        groupDescript = (EditText) findViewById(R.id.edit_group_info_text_descript);
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
        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog groupDeleteDialog = DialogUtil.getGroupDeleteDialog(EditGroupInfoActivity.this);
                Button ok = (Button) groupDeleteDialog.findViewById(R.id.dialog_group_list_btn_delete_group_ok);
                Button cancel = (Button) groupDeleteDialog.findViewById(R.id.dialog_group_list_btn_delete_group_cancel);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupDeleteDialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        groupDeleteDialog.dismiss();
                    }
                });
                groupDeleteDialog.show();

            }
        });
        passwordProtect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordProtect.isChecked()){
                    btnAlterPassword.setEnabled(true);
                    btnAlterPassword.setBackgroundResource(R.drawable.chat_send_button_bg);
                }else{
                    btnAlterPassword.setEnabled(false);
                    btnAlterPassword.setBackgroundResource(R.drawable.common_btn_gray_actionsheet);
                }
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
