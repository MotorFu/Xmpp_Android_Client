package com.zxq.fragment;

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
import java.util.List;

/**
 * Created by zxq on 2014/9/13.
 */
public class GroupChatFragment extends Fragment {
    private ListView listView;
    private XmppService mXmppService;
    private static GroupChatFragment groupChatFragment;

    private GroupChatFragment(XmppService mXmppService) {
        this.mXmppService = mXmppService;
    }

    public static GroupChatFragment getInstance(XmppService mXmppService){
        if(groupChatFragment == null)
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
      //  List<>

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
    private class Group{
        private ImageView groupIcon;
        private TextView groupTitle;

        public void setGroupIcon(ImageView groupIcon) {
            this.groupIcon = groupIcon;
        }

        public void setGroupTitle(TextView groupTitle) {
            this.groupTitle = groupTitle;
        }

        public ImageView getGroupIcon() {
            return groupIcon;
        }

        public TextView getGroupTitle() {
            return groupTitle;
        }
    }
}
