package com.zxq.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zxq.adapter.RosterAdapter;
import com.zxq.service.XmppService;
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

    private GroupChatFragment(XmppService mXmppService) {
        this.mXmppService = mXmppService;
    }

    public static GroupChatFragment getInstance(XmppService mXmppService) {
        if (groupChatFragment == null)
            groupChatFragment = new GroupChatFragment(mXmppService);
        return groupChatFragment;
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
        ArrayList<GroupEntry> groupEntries = new ArrayList<GroupEntry>();
        groupEntries.add(new GroupEntry("", "这是第一个群"));
        groupEntries.add(new GroupEntry("", "这是第二个群"));
        groupEntries.add(new GroupEntry("", "这是第三个群"));
        groupEntries.add(new GroupEntry("", "这是第四个群"));
        groupEntries.add(new GroupEntry("", "这是第五个群"));
        groupEntries.add(new GroupEntry("", "这是第六个群"));
        groupChatAdapter = new GroupChatAdapter(groupEntries,this.getActivity());
        listView.setAdapter(groupChatAdapter);
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

    private class GroupEntry {
        private String iconUrl;
        private String title;

        private GroupEntry() {
        }

        private GroupEntry(String iconUrl, String title) {
            this.iconUrl = iconUrl;
            this.title = title;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIconUrl() {

            return iconUrl;
        }

        public String getTitle() {
            return title;
        }
    }

}
