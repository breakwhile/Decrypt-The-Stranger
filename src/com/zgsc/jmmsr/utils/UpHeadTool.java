package com.zgsc.jmmsr.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @author Wangzhenyue
 * @version 1.0
 */
public class UpHeadTool {
	private Map<String, String> textParams;
	private Map<String, File> filesParams;
	private String upUrl;
	private Context cx;
	private long totalSize;

	public UpHeadTool(Map<String, String> textParams, Map<String, File> filesParams,
			String upUrl, Context cx, long totalSize) {
		this.textParams = textParams;
		this.filesParams = filesParams;
		this.upUrl = upUrl;
		this.cx = cx;
		this.totalSize = totalSize;
	}
	
	public void post() {
		upHeadTask up = new upHeadTask();
		up.execute();
	}

	class upHeadTask extends AsyncTask<Void, Integer, String> {
		private ProgressDialog dialog = null;
		
		@Override
		protected String doInBackground(Void... params) {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			try {
				URL uri = new URL(upUrl);
				HttpURLConnection conn = (HttpURLConnection) uri
						.openConnection();
				conn.setReadTimeout(5 * 1000); // 缓存的最长时间
				conn.setDoInput(true);// 允许输入
				conn.setDoOutput(true);// 允许输出
				conn.setUseCaches(false); // 不允许使用缓存
				conn.setRequestMethod("POST");
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Charsert", "UTF-8");
				conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
						+ ";boundary=" + BOUNDARY);

				// 首先组拼文本类型的参数
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : textParams.entrySet()) {
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINEND);
					sb.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"" + LINEND);
					sb.append("Content-Type: text/plain; charset=" + CHARSET
							+ LINEND);
					sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
					sb.append(LINEND);
					sb.append(entry.getValue());
					sb.append(LINEND);
				}

				DataOutputStream outStream = new DataOutputStream(
						conn.getOutputStream());
				outStream.write(sb.toString().getBytes());
				InputStream in = null;
				// 发送文件数据
				if (filesParams != null) {
					for (Map.Entry<String, File> file : filesParams.entrySet()) {
						StringBuilder sb1 = new StringBuilder();
						sb1.append(PREFIX);
						sb1.append(BOUNDARY);
						sb1.append(LINEND);
						sb1.append("Content-Disposition: form-data; name=\"file[]\"; filename=\""
								+ file.getKey() + "\"" + LINEND);
						sb1.append("Content-Type: application/octet-stream; charset="
								+ CHARSET + LINEND);
						sb1.append(LINEND);
						outStream.write(sb1.toString().getBytes());

						InputStream is = new FileInputStream(file.getValue());
						byte[] buffer = new byte[1024];
						int len = 0;
						long proLen = 0;
						while ((len = is.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
							proLen += len;
							publishProgress((int) (proLen / totalSize));
						}

						is.close();
						outStream.write(LINEND.getBytes());
					}

					// 请求结束标志
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND)
							.getBytes();
					outStream.write(end_data);
					outStream.flush();
					// 得到响应码
					int res = conn.getResponseCode();
					if (res == 200) {
						in = conn.getInputStream();
						int ch;
						StringBuilder sb2 = new StringBuilder();
						while ((ch = in.read()) != -1) {
							sb2.append((char) ch);
						}
						System.out.println(sb2.toString());
					}
					publishProgress(100);
					outStream.close();
					conn.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(cx);
			dialog.setMessage("正在上传头像...");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// 更新进度
			dialog.setProgress(progress[0]);
		}
	}
}
