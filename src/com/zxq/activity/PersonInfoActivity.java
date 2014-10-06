package com.zxq.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.zxq.service.XmppService;
import com.zxq.util.ImageTools;
import com.zxq.util.LogUtil;
import com.zxq.util.VCardConstants;
import com.zxq.vo.PersonEntityInfo;
import com.zxq.xmpp.R;
import org.jivesoftware.smackx.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zxq on 2014/9/15.
 */
public class PersonInfoActivity extends Activity {

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


    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;

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
        textName.setText(personEntityInfo.getName() == null ? "(空)" : personEntityInfo.getName());
        textSignature.setText(personEntityInfo.getSignature() == null ? "(空)" : personEntityInfo.getSignature());
        textQQ.setText(personEntityInfo.getQq() == null ? "(空)" : personEntityInfo.getQq());
        textPhone.setText(personEntityInfo.getPhone() == null ? "(空)" : personEntityInfo.getPhone());
        textEmail.setText(personEntityInfo.getEmail() == null ? "(空)" : personEntityInfo.getEmail());
        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicturePicker(PersonInfoActivity.this);
            }
        });
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
        btnEditPassword = (Button) findViewById(R.id.person_info_btn_edit_password);
        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoToEditInfoActivity();
            }
        });
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoToAlterPasswordActivity();
            }
        });
        acitonBarTitle.setText("个人信息");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfoActivity.this.finish();
            }
        });
    }

    private void onGoToAlterPasswordActivity() {
        Intent intent = new Intent();
        intent.setClass(this, EditPersonPasswordActivity.class);
        startActivity(intent);
    }

    private void onGoToEditInfoActivity() {
        Intent intent = new Intent();
        intent.setClass(this, EditPersonInfoActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CROP:
                    Uri uri = null;
                    if (data != null) {
                        uri = data.getData();
                        System.out.println("Data");
                    } else {
                        System.out.println("File");
                        String fileName = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
                        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                    }
                    cropImage(uri, 320, 320, CROP_PICTURE);
                    break;

                case CROP_PICTURE:
                    Bitmap photo = null;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (photo == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap) extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        }
                    }
                    imageIcon.setImageBitmap(photo);
                    break;
                default:
                    break;
            }
        }
    }


//============个人头像图片显示和截取=======================
    public void showPicturePicker(Context context) {
        // final boolean crop = isCrop;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            //类型码
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Uri imageUri = null;
                        String fileName = null;
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //删除上一次截图的临时文件
                        SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
                        ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));
                        //保存本次截图临时文件名字
                        fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempName", fileName);
                        editor.commit();
                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, CROP);
                        break;
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(openAlbumIntent, CROP);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }
    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
    }
}
