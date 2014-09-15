package com.zxq.activity;

import android.app.Activity;
import android.app.Dialog;
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
import com.zxq.xmpp.R;

/**
 * Created by zxq on 2014/9/15.
 */
public class RegisterActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;

    private  EditText inputAccount;
    private EditText inputPassword;
    private EditText inputAgainPassword;
    private Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setupData();
    }

    private void setupData() {

    }

    private void initView() {
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        inputAccount = (EditText) findViewById(R.id.register_input_account);
        inputPassword = (EditText) findViewById(R.id.register_input_password);
        inputAgainPassword = (EditText) findViewById(R.id.register_input_password_again);
        btnRegister = (Button) findViewById(R.id.register_btn_register);
        acitonBarTitle.setText("用户注册");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login_server:
                final Dialog inputDialog = DialogUtil.getInputDialog(this);
                final EditText inputEditText = (EditText) inputDialog.findViewById(R.id.edit_login_server_input);
                String server = PreferenceUtils.getPrefString(this, PreferenceConstants.Server, "");
                inputEditText.setText(server);
                Button inputOkBtn = (Button) inputDialog.findViewById(R.id.btn_login_server_ok);
                Button inputCancelBtn = (Button) inputDialog.findViewById(R.id.btn_login_server_cancel);
                inputOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceUtils.setPrefString(RegisterActivity.this, PreferenceConstants.Server, inputEditText.getText().toString());
                        inputDialog.dismiss();
                    }
                });
                inputCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                    }
                });
                inputDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
