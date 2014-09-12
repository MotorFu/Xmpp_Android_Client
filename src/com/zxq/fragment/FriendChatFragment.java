package com.zxq.fragment;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.zxq.activity.ChatActivity;
import com.zxq.activity.MainActivity;
import com.zxq.adapter.RosterAdapter;
import com.zxq.db.RosterProvider;
import com.zxq.service.XmppService;
import com.zxq.ui.iphonetreeview.IphoneTreeView;
import com.zxq.ui.pulltorefresh.PullToRefreshBase;
import com.zxq.ui.pulltorefresh.PullToRefreshScrollView;
import com.zxq.ui.quickaction.ActionItem;
import com.zxq.ui.quickaction.QuickAction;
import com.zxq.ui.view.AddRosterItemDialog;
import com.zxq.ui.view.GroupNameView;
import com.zxq.util.LogUtil;
import com.zxq.util.PreferenceConstants;
import com.zxq.util.PreferenceUtils;
import com.zxq.util.ToastUtil;
import com.zxq.xmpp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2014/9/12.
 */
public class FriendChatFragment extends Fragment {
    private IphoneTreeView mIphoneTreeView;
    private RosterAdapter mRosterAdapter;
    private XmppService mXmppService;
    private Handler mainHandler;
    private PullToRefreshScrollView mPullRefreshScrollView;
    private int mLongPressGroupId, mLongPressChildId;
    private ContentObserver mRosterObserver = new RosterObserver();
    private static final String[] GROUPS_QUERY = new String[]{RosterProvider.RosterConstants._ID, RosterProvider.RosterConstants.GROUP,};
    private static final String[] ROSTER_QUERY = new String[]{RosterProvider.RosterConstants._ID, RosterProvider.RosterConstants.JID, RosterProvider.RosterConstants.ALIAS, RosterProvider.RosterConstants.STATUS_MODE, RosterProvider.RosterConstants.STATUS_MESSAGE,};
    private static FriendChatFragment friendChatFragment;

    private FriendChatFragment(XmppService mXmppService,Handler mainHandler) {
        this.mXmppService = mXmppService;
        this.mainHandler =mainHandler;

    }

    public static FriendChatFragment getInstance(XmppService mXmppService,Handler mainHandler) {
        if (friendChatFragment == null)
            friendChatFragment = new FriendChatFragment(mXmppService, mainHandler);
        return friendChatFragment;
    }

    public abstract class EditOk {
        abstract public void ok(String result);
    }

    public void updateRoster() {
        mRosterAdapter.requery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_friend_chat_tree, container, false);
        initViews(inflate);
        return inflate;
    }

    private void initViews(View inflate) {
        mPullRefreshScrollView = (PullToRefreshScrollView) inflate.findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        mIphoneTreeView = (IphoneTreeView) inflate.findViewById(R.id.iphone_tree_view);
        mIphoneTreeView.setHeaderView(this.getActivity().getLayoutInflater().inflate(R.layout.contact_buddy_list_group, mIphoneTreeView, false));
        mIphoneTreeView.setEmptyView(inflate.findViewById(R.id.empty));
        mIphoneTreeView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int groupPos = (Integer) view.getTag(R.id.xxx01); // 参数值是在setTag时使用的对应资源id号
                int childPos = (Integer) view.getTag(R.id.xxx02);
                mLongPressGroupId = groupPos;
                mLongPressChildId = childPos;
                if (childPos == -1) {// 长按的是父项
                    // 根据groupPos判断你长按的是哪个父项，做相应处理（弹框等）
                    showGroupQuickActionBar(view.findViewById(R.id.group_name));
                } else {
                    // 根据groupPos及childPos判断你长按的是哪个父项下的哪个子项，然后做相应处理。
                    showChildQuickActionBar(view.findViewById(R.id.icon));
                }
                return false;
            }
        });
        mIphoneTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String userJid = mRosterAdapter.getChild(groupPosition, childPosition).getJid();
                String userName = mRosterAdapter.getChild(groupPosition, childPosition).getAlias();
                startChatActivity(userJid, userName);
                return false;
            }
        });
    }


    //发起向好友会话
    private void startChatActivity(String userJid, String userName) {
        Intent chatIntent = new Intent(this.getActivity(), ChatActivity.class);
        Uri userNameUri = Uri.parse(userJid);
        chatIntent.setData(userNameUri);
        chatIntent.putExtra(ChatActivity.INTENT_EXTRA_USERNAME, userName);
        startActivity(chatIntent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupData();
    }

    private void setupData() {
        mRosterAdapter = new RosterAdapter(this.getActivity(), mIphoneTreeView, mPullRefreshScrollView);
        mIphoneTreeView.setAdapter(mRosterAdapter);
        mRosterAdapter.requery();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRosterAdapter.requery();
        this.getActivity().getContentResolver().registerContentObserver(RosterProvider.CONTENT_URI, true, mRosterObserver);

    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().getContentResolver().unregisterContentObserver(mRosterObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //==========QuickAction是一个快速弹出窗口===================
    private void showGroupQuickActionBar(View view) {//父节点弹窗
        QuickAction quickAction = new QuickAction(this.getActivity(), QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(0, getString(R.string.rename)));
        quickAction.addActionItem(new ActionItem(1, getString(R.string.add_friend)));
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                if (!isConnected()) {   // 如果没有连接直接返回
                    ToastUtil.showShort(FriendChatFragment.this.getActivity(), R.string.conversation_net_error_label);
                    return;
                }
                switch (actionId) {
                    case 0:
                        String groupName = mRosterAdapter.getGroup(mLongPressGroupId).getGroupName();
                        if (TextUtils.isEmpty(groupName)) {// 系统默认分组不允许重命名
                            ToastUtil.showShort(FriendChatFragment.this.getActivity(), R.string.roster_group_rename_failed);
                            return;
                        }
                        //   renameRosterGroupDialog(mRosterAdapter.getGroup(mLongPressGroupId).getGroupName());
                        break;
                    case 1:
                        new AddRosterItemDialog((com.zxq.activity.MainActivity) FriendChatFragment.this.getActivity(), mXmppService).show();// 添加联系人
                        break;
                    default:
                        break;
                }
            }
        });
        quickAction.show(view);
        quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
    }


    private void editTextDialog(int titleId, CharSequence message, String text, final EditOk ok) {
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edittext_dialog, null);
        TextView messageView = (TextView) layout.findViewById(R.id.text);
        messageView.setText(message);
        final EditText input = (EditText) layout.findViewById(R.id.editText);
        input.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        input.setText(text);
        new AlertDialog.Builder(this.getActivity()).setTitle(titleId).setView(layout).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (newName.length() != 0)
                    ok.ok(newName);
            }
        }).setNegativeButton(android.R.string.cancel, null).create().show();
    }


    void renameRosterGroupDialog(final String groupName) {
        editTextDialog(R.string.RenameGroup_title, getString(R.string.RenameGroup_summ, groupName), groupName, new EditOk() {
            public void ok(String result) {
                if (mXmppService != null)
                    mXmppService.renameRosterGroup(groupName, result);
            }
        });
    }

    private void showChildQuickActionBar(View view) {//子节点弹窗
        QuickAction quickAction = new QuickAction(FriendChatFragment.this.getActivity(), QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(0, getString(R.string.add)));
        quickAction.addActionItem(new ActionItem(1, getString(R.string.rename)));
        quickAction.addActionItem(new ActionItem(2, getString(R.string.move)));
        quickAction.addActionItem(new ActionItem(3, getString(R.string.delete)));
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                String userJid = mRosterAdapter.getChild(mLongPressGroupId, mLongPressChildId).getJid();
                String userName = mRosterAdapter.getChild(mLongPressGroupId, mLongPressChildId).getAlias();
                if (!isConnected()) {
                    ToastUtil.showShort(FriendChatFragment.this.getActivity(), R.string.conversation_net_error_label);
                    return;
                }
                switch (actionId) {
                    case 0:
                        if (mXmppService != null)
                            mXmppService.requestAuthorizationForRosterItem(userJid);
                        break;
                    case 1:
                        renameRosterItemDialog(userJid, userName);
                        break;
                    case 2:
                        moveRosterItemToGroupDialog(userJid);
                        break;
                    case 3:
                        removeRosterItemDialog(userJid, userName);
                        break;

                    default:
                        break;
                }
            }
        });
        quickAction.show(view);
    }

    void renameRosterItemDialog(final String JID, final String userName) {
        editTextDialog(R.string.RenameEntry_title, getString(R.string.RenameEntry_summ, userName, JID), userName, new EditOk() {
            public void ok(String result) {
                if (mXmppService != null)
                    mXmppService.renameRosterItem(JID, result);
            }
        });
    }


    void moveRosterItemToGroupDialog(final String jabberID) {
        LayoutInflater inflater = (LayoutInflater) FriendChatFragment.this.getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View group = inflater.inflate(R.layout.moverosterentrytogroupview, null);
        final GroupNameView gv = (GroupNameView) group.findViewById(R.id.moverosterentrytogroupview_gv);
        gv.setGroupList(getRosterGroups());
        new AlertDialog.Builder(FriendChatFragment.this.getActivity()).setTitle(R.string.MoveRosterEntryToGroupDialog_title).setView(group).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LogUtil.d("new group: " + gv.getGroupName());
                if (isConnected())
                    mXmppService.moveRosterItemToGroup(jabberID, gv.getGroupName());
            }
        }).setNegativeButton(android.R.string.cancel, null).create().show();
    }

    void removeRosterItemDialog(final String JID, final String userName) {
        new AlertDialog.Builder(FriendChatFragment.this.getActivity()).setTitle(R.string.deleteRosterItem_title).setMessage(getString(R.string.deleteRosterItem_text, userName, JID)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mXmppService.removeRosterItem(JID);
            }
        }).setNegativeButton(android.R.string.no, null).create().show();
    }

    public List<String> getRosterGroups() {//获得所有分组名称
        List<String> list = new ArrayList<String>();
        Cursor cursor = FriendChatFragment.this.getActivity().getContentResolver().query(RosterProvider.GROUPS_URI, GROUPS_QUERY, null, null, RosterProvider.RosterConstants.GROUP);
        int idx = cursor.getColumnIndex(RosterProvider.RosterConstants.GROUP);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(idx));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public List<String[]> getRosterContacts() {//获得所有人员名称
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = FriendChatFragment.this.getActivity().getContentResolver().query(RosterProvider.CONTENT_URI, ROSTER_QUERY, null, null, RosterProvider.RosterConstants.ALIAS);
        int JIDIdx = cursor.getColumnIndex(RosterProvider.RosterConstants.JID);
        int aliasIdx = cursor.getColumnIndex(RosterProvider.RosterConstants.ALIAS);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String jid = cursor.getString(JIDIdx);
            String alias = cursor.getString(aliasIdx);
            if ((alias == null) || (alias.length() == 0))
                alias = jid;
            list.add(new String[]{jid, alias});
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    private boolean isConnected() {
        return mXmppService != null && mXmppService.isAuthenticated();
    }

    //重新获取数据
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            if (!isConnected()) {// 如果没有连接重新连接
                String usr = PreferenceUtils.getPrefString(FriendChatFragment.this.getActivity(), PreferenceConstants.ACCOUNT, "");
                String password = PreferenceUtils.getPrefString(FriendChatFragment.this.getActivity(), PreferenceConstants.PASSWORD, "");
                mXmppService.Login(usr, password);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mRosterAdapter.requery();// 重新查询一下数据库
            mPullRefreshScrollView.onRefreshComplete();
            ToastUtil.showShort(FriendChatFragment.this.getActivity(), "刷新成功!");
            super.onPostExecute(result);
        }
    }


    private class RosterObserver extends ContentObserver {
        public RosterObserver() {
            super(mainHandler);
        }

        public void onChange(boolean selfChange) {
            LogUtil.d(MainActivity.class, "RosterObserver.onChange: " + selfChange);
            if (mRosterAdapter != null)
                mainHandler.postDelayed(new Runnable() {
                    public void run() {
                        updateRoster();
                    }
                }, 100);
        }
    }
}