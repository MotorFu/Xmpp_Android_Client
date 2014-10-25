package com.zxq.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.zxq.activity.FragmentCallBack;
import com.zxq.activity.GroupChatActivity;
import com.zxq.adapter.RosterAdapter;
import com.zxq.service.XmppService;
import com.zxq.util.LogUtil;
import com.zxq.vo.GroupEntry;
import com.zxq.xmpp.R;

import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2014/9/13.
 */
public class GroupChatFragment extends Fragment {
    private ListView listView;
    private XmppService mXmppService;
    private static GroupChatFragment groupChatFragment;
    private GroupChatAdapter groupChatAdapter;
    private  List<GroupEntry> groupEntryList;

    private FragmentCallBack mFragmentCallBack;

    public static String GROUP_CHAT_ROOM_JID = "GCRJ";

    public static GroupChatFragment getInstance() {
        if (groupChatFragment == null)
            groupChatFragment = new GroupChatFragment();
        return groupChatFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallBack = (FragmentCallBack) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflate = inflater.inflate(R.layout.fragment_group_chat_tree, container, false);
        initView(inflate);
        setupData();
        return inflate;
    }


    private void initView(View inflate) {
        listView = (ListView) inflate.findViewById(R.id.group_chat_tree_view);
    }

    private void setupData() {
        XmppService xmppService = mFragmentCallBack.getService();
        String username = xmppService.getXmppUserName();
        groupEntryList = xmppService.getGroupEntryList();
        groupChatAdapter = new GroupChatAdapter(groupEntryList, this.getActivity());
        listView.setAdapter(groupChatAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupEntry groupEntry = groupEntryList.get(position);
                Intent intent = new Intent();
                intent.putExtra(GROUP_CHAT_ROOM_JID,groupEntry.getJid());
                intent.setClass( GroupChatFragment.this.getActivity(),GroupChatActivity.class);
                GroupChatFragment.this.startActivity(intent);
                //GroupChatActivity
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class GroupChatAdapter extends BaseAdapter {
        private List<GroupEntry> groupList;
        private Context context;

        private GroupChatAdapter() {
        }

        public List<GroupEntry> getGroupList() {
            return groupList;
        }

        public void setGroupList(List<GroupEntry> groupList) {

            this.groupList = groupList;
        }

        private GroupChatAdapter(List<GroupEntry> groupList, Context context) {
            this.groupList = groupList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return groupList.size();
        }

        @Override
        public Object getItem(int position) {
            return groupList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupHolder groupHolder;
            GroupEntry groupEntry = groupList.get(position);
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_group_chat_tree_item, null);
                groupHolder.groupIcon = (ImageView) convertView.findViewById(R.id.group_chat_item_icon);
                groupHolder.groupTitle = (TextView) convertView.findViewById(R.id.group_chat_item_title);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.groupTitle.setText(groupEntry.getTitle());
            return convertView;
        }

        private class GroupHolder {
            public ImageView groupIcon;
            public TextView groupTitle;
        }
    }


}
