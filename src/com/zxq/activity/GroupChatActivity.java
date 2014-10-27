package com.zxq.activity;

import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.zxq.adapter.ChatAdapter;
import com.zxq.adapter.FaceAdapter;
import com.zxq.adapter.FacePageAdeapter;
import com.zxq.adapter.GroupChatAdapter;
import com.zxq.app.XmppApplication;
import com.zxq.db.GroupChatProvider.GroupChatConstants;
import com.zxq.db.GroupChatProvider;
import com.zxq.db.RosterProvider;
import com.zxq.fragment.GroupChatFragment;
import com.zxq.service.IConnectionStatusCallback;
import com.zxq.service.XmppService;
import com.zxq.ui.emoji.EmojiKeyboard;
import com.zxq.ui.emoji.EmojiKeyboard.EventListener;
import com.zxq.ui.swipeback.SwipeBackActivity;
import com.zxq.ui.view.CirclePageIndicator;
import com.zxq.ui.xlistview.MsgListView;
import com.zxq.ui.xlistview.MsgListView.IXListViewListener;
import com.zxq.util.*;
import com.zxq.xmpp.R;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupChatActivity extends SwipeBackActivity implements OnTouchListener, OnClickListener, IXListViewListener, IConnectionStatusCallback {
	public static final String INTENT_EXTRA_USERNAME = GroupChatActivity.class.getName() + ".username";// 昵称对应的key
	private MsgListView mMsgListView;// 对话ListView
	private boolean mIsFaceShow = false;// 是否显示表情
	private Button mSendMsgBtn;// 发送消息button
	private ImageButton mFaceSwitchBtn;// 切换键盘和表情的button
	private TextView mTitleNameView;// 标题栏
	private EditText mChatEditText;// 消息输入框
	private EmojiKeyboard mFaceRoot;// 表情父容器
	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;
	private List<String> mFaceMapKeys;// 表情对应的字符串数组
	private String mRoomName = null;
    private String mRoomJID = null;// 当前聊天用户的ID
    private MultiUserChat multiUserChat;


//    //加入聊天室(使用昵称喝醉的毛毛虫 ,使用密码ddd)并且获取聊天室里最后5条信息，
//    //注：addMessageListener监听器必须在此join方法之前，否则无法监听到需要的5条消息
//    muc = new MultiUserChat(connection, "ddd@conference.pc2010102716");
//    DiscussionHistory history = new DiscussionHistory();
//    history.setMaxStanzas(5);
//    muc.join("喝醉的毛毛虫", "ddd", history, SmackConfiguration.getPacketReplyTimeout());
//
//    //监听拒绝加入聊天室的用户
//    muc.addInvitationRejectionListener(new InvitationRejectionListener() {
//        @Override
//        public void invitationDeclined(String invitee, String reason) {
//            System.out.println(invitee + " reject invitation, reason is " + reason);
//        }
//    });

    private static final String[] PROJECTION_FROM = new String[] {GroupChatConstants._ID, GroupChatConstants.DATE, GroupChatConstants.DIRECTION, GroupChatConstants.JID, GroupChatConstants.RoomJID, GroupChatConstants.MESSAGE};// 查询字段

	private XmppService mXmppService;// Main服务
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XmppService.XXBinder) service).getService();
			mXmppService.registerConnectionStatusCallback(GroupChatActivity.this);
			// 如果没有连接上，则重新连接xmpp服务器
			if (!mXmppService.isAuthenticated()) {
				String usr = PreferenceUtils.getPrefString(GroupChatActivity.this, PreferenceConstants.ACCOUNT, "");
				String password = PreferenceUtils.getPrefString(GroupChatActivity.this, PreferenceConstants.PASSWORD, "");
				mXmppService.login(usr, password);
			}
            initData();// 初始化数据
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXmppService.unRegisterConnectionStatusCallback();
			mXmppService = null;
		}

	};

    /**
	 * 解绑服务
	 */
	private void unbindXMPPService() {
		try {
			unbindService(mServiceConnection);
		} catch (IllegalArgumentException e) {
			LogUtil.e("Service wasn't bound!");
		}
	}

	/**
	 * 绑定服务
	 */
	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, XmppService.class);
		bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_chat);

		initView();// 初始化view
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
	protected void onDestroy() {
		super.onDestroy();
		if (hasWindowFocus())
			unbindXMPPService();// 解绑服务
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 窗口获取到焦点时绑定服务，失去焦点将解绑
		if (hasFocus)
			bindXMPPService();
		else
			unbindXMPPService();
	}

	private void initData() {
        //TODO:通过intent获取聊天室名称还有JID
        String jid = getIntent().getStringExtra(GroupChatFragment.GROUP_CHAT_ROOM_JID);
        RoomInfo roomInfo = mXmppService.queryGroupChatRoomInfoByJID(jid);
        mRoomName = roomInfo.getSubject();
        multiUserChat = mXmppService.getMultiUserChatByRoomJID(jid);
       // ToastUtil.showShort(this,getIntent().getStringExtra(GroupChatFragment.GROUP_CHAT_ROOM_JID));
        mTitleNameView.setText(mRoomName);
        multiUserChat.addMessageListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                final Message message = (Message) packet;
                System.out.println(message.getFrom() + " : " + message.getBody());
                GroupChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:更新聊天LIST
                        ToastUtil.showShort(GroupChatActivity.this,message.getBody());
                        //TODO:这里利用Adapter进行更新，适当要做些缓存。考虑存入数据库
                    }
                });
            }
        });
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);
        try {
            String name = mXmppService.getXmppUserName();
            name = name.substring(0,name.indexOf("@"));
            multiUserChat.join(name,"",history,3000);

        } catch (XMPPException e) {
            e.printStackTrace();
        }
        // 将表情map的key保存在数组中
		Set<String> keySet = XmppApplication.getInstance().getFaceMap().keySet();
		mFaceMapKeys = new ArrayList<String>();
		mFaceMapKeys.addAll(keySet);

      //  ToastUtil.showShort(this,multiUserChat.get()+"zzzzz");

//        try {
//            multiUserChat.join("逗比们");
//            //这是一种发送方法，使用Message，不过还是用字符串方便
////            Message msg = new Message();
////            msg.getSubject("zzzzzz");
////            msg.setTo(multiUserChat.getRoom());
////            msg.setType(Message.Type.groupchat);
////            msg.setBody("hahahahahahahazzzzz");
////            multiUserChat.sendMessage(msg);
//            multiUserChat.sendMessage("aaaaaaaaaaaaa");
//            ToastUtil.showShort(this,"multiUserChat成功发送");
//        } catch (XMPPException e) {
//            e.printStackTrace();
//            ToastUtil.showShort(this,"multiUserChat："+e.getMessage());
//        }

    }

	/**
	 * 设置聊天的Adapter
	 */
	private void setChatWindowAdapter() {
		String selection = GroupChatConstants.RoomJID + "='" + mRoomJID + "'";
		// 异步查询数据库
		new AsyncQueryHandler(getContentResolver()) {

			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				ListAdapter adapter = new GroupChatAdapter(GroupChatActivity.this, cursor, PROJECTION_FROM);
				mMsgListView.setAdapter(adapter);
				mMsgListView.setSelection(adapter.getCount() - 1);
			}

		}.startQuery(0, null, GroupChatProvider.CONTENT_URI, PROJECTION_FROM, selection, null, null);
	}

	private void initView() {
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();

		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mSendMsgBtn = (Button) findViewById(R.id.send);
		mFaceSwitchBtn = (ImageButton) findViewById(R.id.face_switch_btn);
		mChatEditText = (EditText) findViewById(R.id.group_input);
		mFaceRoot = (EmojiKeyboard) findViewById(R.id.face_ll);
		mFaceRoot.setEventListener(new EventListener() {

			@Override
			public void onEmojiSelected(String res) {
				EmojiKeyboard.input(mChatEditText, res);
			}

			@Override
			public void onBackspace() {
				EmojiKeyboard.backspace(mChatEditText);
			}
		});
		mChatEditText.setOnTouchListener(this);
		mTitleNameView = (TextView) findViewById(R.id.ivTitleName);
		mChatEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowNanagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || mIsFaceShow) {
						mFaceRoot.setVisibility(View.GONE);
						mIsFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		mChatEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mSendMsgBtn.setEnabled(true);
				} else {
					mSendMsgBtn.setEnabled(false);
				}
			}
		});
		mFaceSwitchBtn.setOnClickListener(this);
		mSendMsgBtn.setOnClickListener(this);

	}

	@Override
	public void onRefresh() {
		mMsgListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// do nothing
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.face_switch_btn) {
			if (!mIsFaceShow) {
				mInputMethodManager.hideSoftInputFromWindow(mChatEditText.getWindowToken(), 0);
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mFaceRoot.setVisibility(View.VISIBLE);
				mFaceSwitchBtn.setImageResource(R.drawable.aio_keyboard);
				mIsFaceShow = true;
			} else {
				mFaceRoot.setVisibility(View.GONE);
				mInputMethodManager.showSoftInput(mChatEditText, 0);
				mFaceSwitchBtn.setImageResource(R.drawable.qzone_edit_face_drawable);
				mIsFaceShow = false;
			}
		} else if (id == R.id.send) {// 发送消息
			sendMessageIfNotNull();
		}
	}

    private void sendMessageIfNotNull() {
        if (mChatEditText.getText().length() >= 1) {
            if (mXmppService != null) {
                mXmppService.sendGroupChat(multiUserChat,mChatEditText.getText().toString());
                if (!mXmppService.isAuthenticated())
                    ToastUtil.showShort(this, "消息已经保存随后发送");
            }
            mChatEditText.setText("");
            mSendMsgBtn.setEnabled(false);
        }
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = v.getId();
		if (id == R.id.msg_listView) {
			mInputMethodManager.hideSoftInputFromWindow(mChatEditText.getWindowToken(), 0);
			mFaceSwitchBtn.setImageResource(R.drawable.qzone_edit_face_drawable);
			mFaceRoot.setVisibility(View.GONE);
			mIsFaceShow = false;
		} else if (id == R.id.input) {
			mInputMethodManager.showSoftInput(mChatEditText, 0);
			mFaceSwitchBtn.setImageResource(R.drawable.qzone_edit_face_drawable);
			mFaceRoot.setVisibility(View.GONE);
			mIsFaceShow = false;
		}
		return false;
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
	}


}
