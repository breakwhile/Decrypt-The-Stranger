/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zgsc.jmmsr.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.zgsc.jmmsr.R;
import com.zgsc.jmmsr.Constant;
import com.zgsc.jmmsr.DemoApplication;
import com.zgsc.jmmsr.adapter.NewFriendsMsgAdapter;
import com.zgsc.jmmsr.db.InviteMessgeDao;
import com.zgsc.jmmsr.domain.InviteMessage;
import com.zgsc.jmmsr.domain.InviteMessage.InviteMesageStatus;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
	private ListView listView;
	private InviteMessgeDao messgeDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_msg);

		messgeDao = new InviteMessgeDao(this);
		listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		// 设置adapter
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
		DemoApplication.getInstance().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);

	}

	/*
	 * 处理解密结果
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// 当requestCode、resultCode同时为0，也就是处理特定的结果
		if (requestCode == 122 && resultCode == 122) {
			Bundle res = intent.getExtras();
			// 取出Bundle中的数据
			String s = res.getString("challengeResult");
			// 修改city文本框的内容
			if (s.equals("finish")) {
				Toast.makeText(this, "挑战失败：（", Toast.LENGTH_SHORT).show();
				refuseInvitation((InviteMessage) res.getSerializable("userMsg"));
			} else if (s.equals("exit")) {
				refuseInvitation((InviteMessage) res.getSerializable("userMsg"));
			} else if (s.equals("addFriend")) {
				Intent i = getIntent();
				acceptInvitation((InviteMessage) res.getSerializable("userMsg"));
			}
		}
	}
	
	private void acceptInvitation(final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在同意...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if(msg.getGroupId() == null) //同意好友请求
						EMChatManager.getInstance().acceptInvitation(msg.getFrom());
					else //同意加群申请
						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
					NewFriendsMsgActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							msg.setStatus(InviteMesageStatus.AGREED);
							// 更新db
							ContentValues values = new ContentValues();
							values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
							messgeDao.updateMessage(msg.getId(), values);

						}
					});
				} catch (final Exception e) {
					NewFriendsMsgActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(NewFriendsMsgActivity.this, "同意失败: " + e.getMessage(), 1).show();
						}
					});

				}
			}
		}).start();
	}
	
	private void refuseInvitation(final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在同意...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					if(msg.getGroupId() == null) //拒绝好友请求
						EMChatManager.getInstance().refuseInvitation(msg.getFrom());
					else //同意加群申请
						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
					NewFriendsMsgActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							msg.setStatus(InviteMesageStatus.AGREED);
							// 更新db
							ContentValues values = new ContentValues();
							values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
							messgeDao.deleteMessage(msg.getFrom());
							messgeDao.updateMessage(msg.getId(), values);
						}
					});
				} catch (final Exception e) {
					NewFriendsMsgActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(NewFriendsMsgActivity.this, "同意失败: " + e.getMessage(), 1).show();
						}
					});

				}
			}
		}).start();
	}	

	public void back(View view) {
		finish();
	}

}
