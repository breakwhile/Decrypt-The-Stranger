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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.zgsc.jmmsr.R;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.PathUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zgsc.jmmsr.Constant;
import com.zgsc.jmmsr.DemoApplication;
import com.zgsc.jmmsr.game.GameActivity;
import com.zgsc.jmmsr.utils.CommonUtils;
import com.zgsc.jmmsr.utils.DeviceUuidFactory;
import com.zgsc.jmmsr.utils.HttpRestClient;
import com.zgsc.jmmsr.utils.JsonToMapList;
import com.zgsc.jmmsr.utils.MD5;
import com.zgsc.jmmsr.utils.PreferenceUtils;
import com.zgsc.jmmsr.utils.UpHeadTool;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText emailEditText;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private File cameraFile;
	public static final int USERPIC_REQUEST_CODE_CAMERA = 102;
	public static final int USERPIC_REQUEST_CODE_CUT = 103;
	private String sex = null;
	private File userHeadFile;		//用户头像
	/**
	 * 用户头像imageView
	 */
	private ImageView iv_user_photo;
	private String uid = null;

	RadioGroup rg;
	RadioButton b1;
	RadioButton b2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		iv_user_photo = (ImageView) findViewById(R.id.iv_upImage);
		// 提示用户上传头像
		AlertDialog.Builder builder = new Builder(this);
		String[] strarr = { "自拍一张照片作为头像", "返回" };
		builder.setItems(strarr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				if (arg1 == 0) {
					upHeader(getCurrentFocus());
				} else {
					// 返回
				}
			}
		});
		builder.show();

		DeviceUuidFactory uuid = new DeviceUuidFactory(this);
		uid = uuid.getDeviceUuid().toString();

		emailEditText = (EditText) findViewById(R.id.email);
		emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);// 设置限制邮箱格式
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);

		rg = (RadioGroup) findViewById(R.id.sex);
		b1 = (RadioButton) findViewById(R.id.male);
		b2 = (RadioButton) findViewById(R.id.female);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == b1.getId()) {
					sex = "1";
					Toast.makeText(RegisterActivity.this, "男",
							Toast.LENGTH_LONG).show();
				}
				if (checkedId == b2.getId()) {
					sex = "2";
					Toast.makeText(RegisterActivity.this, "女",
							Toast.LENGTH_LONG).show();
				}

			}

		});
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		final String email = emailEditText.getText().toString().trim();
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(email)) {
			Toast.makeText(this, "邮箱不能为空！", Toast.LENGTH_SHORT).show();
			emailEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (sex == null) {
			Toast.makeText(this, "请选择您的性别！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			RequestParams params = new RequestParams();
			params.add("tel_email", email);
			params.add("nickname", username);
			params.add("pwd", MD5.MD5Hash(pwd));
			params.add("sex", sex);
			params.add("uid", uid);

			HttpRestClient.get(Constant.REGISTER_URL, params,
					new BaseJsonHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String rawJsonResponse, Object response) {
							Log.d("login_res_json" + rawJsonResponse);

							if (CommonUtils.isNullOrEmpty(rawJsonResponse)) {
								Toast.makeText(getApplicationContext(),
										"您的网络不稳定,请检查网络！", 0).show();
								return;
							}
							Map<String, Object> lm = JsonToMapList
									.getMap(rawJsonResponse);

							if (lm.get("status").toString() != null
									&& lm.get("status").toString()
											.equals("yes")) {
								Map<String, Object> result = JsonToMapList
										.getMap(lm.get("result").toString());
								String resultStr = "status:" + lm.get("status")
										+ "\n" + "message:" + lm.get("message")
										+ "\n" + "result:" + lm.get("result")
										+ "\n" + "user:" + result.get("user")
										+ "\n" + "pwd:" + result.get("pwd")
										+ "\n";

								Log.d("login_res_obj" + resultStr);
								if (userHeadFile != null && userHeadFile.exists()) {			
									Map<String, String> params = new HashMap<String, String>();
									params.put("title", "strParamValue");
									params.put("remark", "strParamValue");
									params.put("usemame", "strParamValue");
									params.put("usertel", "strParamValue");
									params.put("eventime", "strParamValue");
									params.put("evenaddress", "strParamValue");
									params.put("addtime", "strParamValue");
									params.put("edittime", "strParamValue");
									params.put("muploadid", "strParamValue");
									Map<String, File> files = new HashMap<String, File>();
									files.put(result.get("user") + "userheadurl.png", userHeadFile);
									// 创建消息
									new UpHeadTool(params, files, Constant.MY_HEAD_UP_URL,
											RegisterActivity.this, userHeadFile.length()).post();
								} else {
									Toast toast = Toast.makeText(RegisterActivity.this,
											"无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
								}
								pd.dismiss();

								// Map<String, Object> result =
								// JsonToMapList.getMap(lm.get("result").toString());
								// reg(result.get("user").toString(),result.get("pwd").toString());
								Toast.makeText(getApplicationContext(),
										lm.get("message").toString(), 0).show();
							} else {
								Toast.makeText(getApplicationContext(),
										lm.get("message").toString(), 0).show();
								pd.dismiss();
							}
							
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, String rawJsonData,
								Object errorResponse) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									"您的网络不稳定,请检查网络！", 0).show();
						}

						@Override
						protected Object parseResponse(String rawJsonData,
								boolean isFailure) throws Throwable {
							// TODO Auto-generated method stub
							return null;
						}

					});

		}
	}

	public void back(View view) {
		finish();
	}

	
	
	
	/*
	 * 注册时提示用户自拍一张照片作为头像
	 */
	public void upHeader(View view) {
		if (!CommonUtils.isExitsSdcard()) {
			Toast.makeText(this, "SD卡不存在，不能更改头像", 0).show();
			return;
		}
		cameraFile = new File("//mnt//sdcard//Android//data//com.zgsc.jmmsr//test",
				DemoApplication.getInstance().getUser()
						+ System.currentTimeMillis() + ".jpg");
		if (cameraFile == null || !cameraFile.exists()) {// 如果文件存在就不在创建
			cameraFile.getParentFile().mkdirs();
		}
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				USERPIC_REQUEST_CODE_CAMERA);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == USERPIC_REQUEST_CODE_CAMERA) { // 获取照片
			if (cameraFile != null && cameraFile.exists()) {
				Log.d("cameraFile" + cameraFile.getAbsolutePath());
				// 改成返回到指定的uri imageUri = Uri.fromFile(cameraFile);
				cropImageUri(Uri.fromFile(cameraFile), 300, 300,
						USERPIC_REQUEST_CODE_CUT);
			}
		} else if (requestCode == USERPIC_REQUEST_CODE_CUT) {// 裁剪图片
			// 从剪切图片返回的数据
			if (data != null) {
				//InputStream inputStream = getBitmapInputStreamFromSDCard(cameraFile);
				//Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				Bitmap bitmap = data.getParcelableExtra("data");   
				iv_user_photo.setImageBitmap(bitmap);
				//PreferenceUtils.getInstance(RegisterActivity.this).setSettingUserPic("file:///mnt/sdcard/Android/data/com.zgsc.jmmsr/test/321.jpg");
				userHeadFile = saveJPGE_After(bitmap, cameraFile);
			}
		}
	}

	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	public File saveJPGE_After(Bitmap bitmap, File cameraFile2) {
		// File file = new File(cameraFile2);
		try {
			FileOutputStream out = new FileOutputStream(cameraFile2);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cameraFile2;
	}

	private InputStream getBitmapInputStreamFromSDCard(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			return fileInputStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
