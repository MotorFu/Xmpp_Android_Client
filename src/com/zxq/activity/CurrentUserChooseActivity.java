package com.zxq.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.zxq.adapter.RosterAdapter;
import com.zxq.adapter.RosterChooseAdapter;
import com.zxq.db.RosterProvider;
import com.zxq.service.XmppService;
import com.zxq.util.LogUtil;
import com.zxq.util.ToastUtil;
import com.zxq.xmpp.R;

/**
 * Created by zxq on 2014/11/9.
 */
public class CurrentUserChooseActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;
    private XmppService mXmppService;
    private RosterChooseAdapter mRosterAdapter;

    private ExpandableListView mFriendChatTreeView;

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

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mXmppService = ((XmppService.XXBinder) service).getService();
            setupDate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mXmppService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_current_user_choose);
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        acitonBarTitle.setText("选择邀请的用户");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUserChooseActivity.this.finish();
            }
        });
        bindXMPPService();
        initView();
    }

    private void setupDate() {
        mFriendChatTreeView = (ExpandableListView) findViewById(R.id.friend_chat_tree_view);
        mFriendChatTreeView.setEmptyView(findViewById(R.id.empty));
        mRosterAdapter = new RosterChooseAdapter(this,mXmppService);
        mFriendChatTreeView.setAdapter(mRosterAdapter);
        mFriendChatTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                String userJid = mRosterAdapter.getChild(groupPosition, childPosition).getJid();
//                String userName = mRosterAdapter.getChild(groupPosition, childPosition).getAlias();
//                ToastUtil.showShort(FriendChatFragment.this.getActivity(), userJid + "=====" + userName);
//                startChatActivity(userJid, userName);
                return false;
            }
        });

        mRosterAdapter.requery();
    }

    private void initView() {


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindXMPPService();
    }


}