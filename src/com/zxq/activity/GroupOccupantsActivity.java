package com.zxq.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.zxq.adapter.RosterChooseAdapter;
import com.zxq.service.XmppService;
import com.zxq.util.LogUtil;
import com.zxq.vo.GroupEntry;
import com.zxq.vo.OccupantsEntry;
import com.zxq.xmpp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2014/11/9.
 */
public class GroupOccupantsActivity extends Activity {
    private ImageView actionBarBack;
    private TextView acitonBarTitle;
    private XmppService mXmppService;
    private GroupOccupantsAdapter mAdapter;

    private ListView mGroupOccupantsListView;

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
        setContentView(R.layout.activity_group_chat_group_occupants);
        actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
        acitonBarTitle = (TextView) findViewById(R.id.actionbar_title);
        acitonBarTitle.setText("选择邀请的用户");
        actionBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupOccupantsActivity.this.finish();
            }
        });
        bindXMPPService();
        initView();
    }

    private void setupDate() {
        List<OccupantsEntry> list = new ArrayList<OccupantsEntry>();
        list.add(new OccupantsEntry("aaa","zzz","成员A"));
        list.add(new OccupantsEntry("aaa","zzz","成员B"));
        list.add(new OccupantsEntry("aaa","zzz","成员C"));
        mAdapter = new GroupOccupantsAdapter(this,list);
        mGroupOccupantsListView.setAdapter(mAdapter);
        mGroupOccupantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void initView() {
        mGroupOccupantsListView = (ListView) findViewById(R.id.group_occupants_listview);
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


    private class GroupOccupantsAdapter extends BaseAdapter {
        private List<OccupantsEntry> occupantsList;
        private Context context;

        public List<OccupantsEntry> getGroupList() {
            return occupantsList;
        }

        private GroupOccupantsAdapter(Context context, List<OccupantsEntry> groupList) {
            this.occupantsList = groupList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return occupantsList.size();
        }

        @Override
        public Object getItem(int position) {
            return occupantsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupHolder groupHolder;
            OccupantsEntry occupantsEntry = occupantsList.get(position);
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_group_occupants_listview_item, null);
                groupHolder.occupantsIcon = (ImageView) convertView.findViewById(R.id.group_occupants_item_icon);
                groupHolder.occupantsName = (TextView) convertView.findViewById(R.id.group_occupants_item_title);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.occupantsName.setText(occupantsEntry.getName());
            return convertView;
        }

        private class GroupHolder {
            public ImageView occupantsIcon;
            public TextView occupantsName;
        }
    }

}