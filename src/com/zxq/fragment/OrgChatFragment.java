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
import com.zxq.vo.DepartmentInfo;
import com.zxq.vo.OrgInfo;
import com.zxq.vo.StaffInfo;
import com.zxq.xmpp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2014/9/13.
 */
public class OrgChatFragment extends Fragment {
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflate = inflater.inflate(R.layout.fragment_org_chat_tree, container, false);
        initView(inflate);
        setupData();
        return inflate;
    }

    private void setupData() {
        ArrayList<OrgInfo> arrayList = new ArrayList<OrgInfo>();
        arrayList.add(new DepartmentInfo(null, "总办", 11));
        arrayList.add(new DepartmentInfo(null, "极光工作室", 11));
        arrayList.add(new DepartmentInfo(null, "飞速工作室", 11));
        arrayList.add(new StaffInfo("ponyma", "马化腾", 3, "飞信", "工作组"));
        arrayList.add(new StaffInfo("staff", "雅俗", 3, "飞信", "工作组"));
        arrayList.add(new StaffInfo("hello", "郑潇乾", 3, "啊啊", "工作组"));
        OrgAdapter orgAdapter = new OrgAdapter(arrayList, this.getActivity());
        listView.setAdapter(orgAdapter);
    }

    private void initView(View inflate) {
        listView = (ListView) inflate.findViewById(R.id.org_info_listview);
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

    private class OrgAdapter extends BaseAdapter {
        List<OrgInfo> orgInfos;
        Context context;

        private OrgAdapter(List<OrgInfo> orgInfos, Context context) {
            this.orgInfos = orgInfos;
            this.context = context;
        }

        @Override
        public int getCount() {
            return orgInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return orgInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OrgInfo orgInfo = orgInfos.get(position);
            if (orgInfo.isDepartmentInfo()) {
                DepartmentHolder departmentHolder;
                DepartmentInfo departmentInfo = (DepartmentInfo) orgInfo;
                departmentHolder = new DepartmentHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_org_chat_tree_item_dept, null);
                departmentHolder.deptIcon = (ImageView) convertView.findViewById(R.id.org_dept_item_icon);
                departmentHolder.deptName = (TextView) convertView.findViewById(R.id.org_dept_item_name);
                departmentHolder.deptName.setText(departmentInfo.getDeptName());
            } else {
                StaffHolder staffHolder;
                StaffInfo staffInfo = (StaffInfo) orgInfo;
                staffHolder = new StaffHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_org_chat_tree_item_stuff, null);
                staffHolder.stuffIcon = (ImageView) convertView.findViewById(R.id.org_staff_item_icon);
                staffHolder.stuffName = (TextView) convertView.findViewById(R.id.org_staff_item_name);
                convertView.setTag(staffHolder);
                staffHolder.stuffName.setText(staffInfo.getAlias());
            }
            return convertView;
        }

        private class DepartmentHolder {
            public ImageView deptIcon;
            public TextView deptName;
        }

        private class StaffHolder {
            public ImageView stuffIcon;
            public TextView stuffName;
        }


    }
}
