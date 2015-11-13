package com.zgsc.jmmsr.activity;

/**
 * @author Wangzhenyue
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.zgsc.jmmsr.R;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zgsc.jmmsr.Constant;
import com.zgsc.jmmsr.DemoApplication;
import com.zgsc.jmmsr.domain.User;
import com.zgsc.jmmsr.domain.UserMap;
import com.zgsc.jmmsr.utils.CommonUtils;
import com.zgsc.jmmsr.utils.HttpRestClient;
import com.zgsc.jmmsr.utils.JsonToMapList;
import com.zgsc.jmmsr.utils.PreferenceUtils;
import com.zgsc.jmmsr.utils.SerializableData;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
public class ShakeActivity extends FragmentActivity {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private RelativeLayout mTitle;
	FragmentActivity f;
	private SlidingDrawer mDrawer;
	private Button mDrawerBtn;
	private static List<UserMap> zainaList;//在哪数据集合
	
	public static final int FRIST_GET_DATE = 111;
	public static final int REFRESH_GET_DATE = 112;
	public static final int LOADMORE_GET_DATE = 113;
	private static int page = 1;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		// drawerSet ();//设置 drawer监听 切换 按钮的方向		
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		zainaList =  new ArrayList<UserMap>();//实例化在哪数据
		loaddata();
		onLoadMore();
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		mTitle = (RelativeLayout) findViewById(R.id.shake_title_bar);

		mDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		mDrawerBtn = (Button) findViewById(R.id.handle);
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.shake_report_dragger_down));
				TranslateAnimation titleup = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, -1.0f);
				titleup.setDuration(200);
				titleup.setFillAfter(true);
				mTitle.startAnimation(titleup);
			}
		});
		/* 设定SlidingDrawer被关闭的事件处理 */
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.shake_report_dragger_up));
				TranslateAnimation titledn = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, -1.0f,
						Animation.RELATIVE_TO_SELF, 0f);
				titledn.setDuration(200);
				titledn.setFillAfter(false);
				mTitle.startAnimation(titledn);
			}
		});

		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			@SuppressLint("ShowToast")
			public void onShake() {
				// Toast.makeText(getApplicationContext(),
				// "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
				startAnim(); // 开始 摇一摇手掌动画
				mShakeListener.stop();
				startVibrato(); // 开始 震动
				/*
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						this.cancel();
					}
				}, 1000);
				*/

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mVibrator.cancel();
						mShakeListener.start();
						   // 结束SelectCityActivity。
						signMarkerOnMap();
						ShakeActivity.this.finish();
					}
				}, 3000);
			}
		});
	}

	public void startAnim() { // 定义摇一摇动画动画
		/*
		Toast.makeText(getApplicationContext(), "正在讲周围的人标记在地图上...",
				30000).show();
		*/
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
		Toast.makeText(getApplicationContext(), "正在将周围的人标记在地图上...",
				Toast.LENGTH_LONG).show();
	}

	public void startVibrato() { // 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1); // 第一个｛｝里面是节奏数组，
																	// 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	public void shake_activity_back(View v) { // 标题栏 魔棒摇一摇
		startAnim(); // 开始 摇一摇手掌动画
		mShakeListener.stop();
		startVibrato(); // 开始 震动
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mVibrator.cancel();
				mShakeListener.start();
				   // 结束SelectCityActivity。
				signMarkerOnMap();	//正确位置，放在后面会造成第一次摇动无法正确显示
				ShakeActivity.this.finish();
				//signMarkerOnMap();		//测试刷新位置 第一次摇动无法显示，需要第二次摇动
			}
		}, 3000);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	
	/*
	@Override
	//重写返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		Toast.makeText(getApplicationContext(),"很多GGMM在等着你哦，别退出，赶紧摇一摇吧：）", 50).show();
    	}
    	return true;
    }
	*/
	
    /**
     * 第一次加载数据
     */
    private void loaddata() {
		geneItems(FRIST_GET_DATE);
	}
    
    /**
     * 搜寻更多的人 modified by Wangzhenyue
     */    
	public void onLoadMore() {
		page = 20;
		geneItems(LOADMORE_GET_DATE);

	}
    
	private void geneItems(final int ACTION) {
		RequestParams params = new RequestParams(); 
		params.add("user", DemoApplication.getInstance().getUser());
		params.add("sex", PreferenceUtils.getInstance(this).getloadsex());
		//新增按时间地区筛选
		params.add("juli", PreferenceUtils.getInstance(this).getloadtimeloc());
		if(ACTION==FRIST_GET_DATE){//第一次加载 (暂时无法判断是否是第一次加载，因为加了一个性别)
			 params.add("load", "f");
			if (DemoApplication.getInstance().getlastloc() != null) {
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude(); 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude();
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr(); 
				  params.add("jingweidu", Double.toString(Longitude)+
						  ","+Double.toString(Latitude)); 
				  params.add("jiedao", adr);
				  //存储经纬度数据
				  PreferenceUtils.getInstance(this).setSettingUserloc(Double.toString(Longitude)+
						  ","+Double.toString(Latitude));
				  if(!CommonUtils.isNullOrEmpty(adr)){
					  PreferenceUtils.getInstance(this).setSettingUserZaina("我在"+adr);
					 // mListener.onrefresh("我在"+adr);
				  }
				  
			}
			//为搜寻更多的人  modified by Wangzhenyue
			// params.add("load", "l");
			// params.add("page", ""+20);
			
		}else if(ACTION==REFRESH_GET_DATE){//刷新数据
			 DemoApplication.getInstance().startLocate();//每刷新一次，定一次位置
			 params.add("load", "r");
			 String jingweidu = PreferenceUtils.getInstance(this).getSettingUserloc();
			 //如果上个经纬度没有保存而现在可以获取到经纬度则使用现在经纬度
			 if(CommonUtils.isNullOrEmpty(jingweidu)&&DemoApplication.getInstance().getlastloc() != null){
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude(); 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude();
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr(); 
				  params.add("jingweidu", Double.toString(Longitude)+
						  ","+Double.toString(Latitude)); 
				  params.add("jiedao", adr);
				  //存储经纬度数据
				  if(!CommonUtils.isNullOrEmpty(adr)){
					  PreferenceUtils.getInstance(this).setSettingUserloc(Double.toString(Longitude)+
							  ","+Double.toString(Latitude));
					  PreferenceUtils.getInstance(this).setSettingUserZaina("我在"+adr);
				  }
				  
			  //如果上次保存的有经纬度而且这次也获得到经纬度
			 }else if (DemoApplication.getInstance().getlastloc() != null) {
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude()  ; 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude() ;
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr();
				  //String Street = DemoApplication.getInstance().getlastloc().getStreet();
				 String jingweidunow = Double.toString(Longitude)+ ","+Double.toString(Latitude);
				 String[] strarray = jingweidu.split(","); 
				 double dis = CommonUtils.Distance( Double.parseDouble(Double.toString(Longitude)), Double.parseDouble(Double.toString(Latitude)), 
						 Double.parseDouble(strarray[0]),Double.parseDouble(strarray[1]));
				 if(Double.valueOf(dis) > 1000){
					 params.add("jingweidu", Double.toString(Longitude)+
							  ","+Double.toString(Latitude)); 
					 params.add("jiedao", adr);
					//存储经纬度数据 
					  PreferenceUtils.getInstance(this).setSettingUserloc(Double.toString(Longitude)+
							  ","+Double.toString(Latitude)); 
					 
				 }
				 if(!CommonUtils.isNullOrEmpty(adr)){
					 PreferenceUtils.getInstance(this).setSettingUserZaina("我在"+adr);
				 }
				 
			 }
			// jingweidu
		}else if(ACTION==LOADMORE_GET_DATE){//加载更多
			 
			 params.add("load", "l");
			 params.add("page", ""+page);
			 //System.out.println("page=========================="+page);
			 //System.out.println("size=========================="+zainaList.size());
		}
	  
		HttpRestClient.get(Constant.ZAINA_URL, params, new BaseJsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(ShakeActivity.this, "您的网络不稳定,请检查网络！", 0).show();
					return;
				} 
				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
				
        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
        			//Toast.makeText(getActivity(), "更新成功", 0).show();
        			//Log.d("log","message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){
        				List<Map<String, Object>> lmresarr = JsonToMapList.getList(lm.get("result").toString());  
        		       /*********************/
        				if(ACTION==REFRESH_GET_DATE||ACTION==FRIST_GET_DATE){
        		    	   zainaList.clear();
        		        }
        			   /*********************/
        				for(int i=0;i<lmresarr.size();i++){ 
        		            if (lmresarr.get(i).get("umd5").toString().equals(DemoApplication.getInstance().getUser()))
        		            	continue;//剔除自己
        		            boolean flag = false;
        		            for (int j = 0; j < zainaList.size(); j++) {
        		            	if (zainaList.get(j).getUsername().equals(lmresarr.get(i).get("umd5").toString())) {
        		            		flag = true;
        		            		int past = Integer.parseInt(zainaList.get(j).getLasttime());
        		            		int now = Integer.parseInt(lmresarr.get(i).get("time").toString());
        		            		if (now > past)
        		            			flag = false;
        		            	}
        		            }
        		            if (flag)
        		            	continue;
        					UserMap user = new UserMap();
        		            user.setNick(lmresarr.get(i).get("name").toString());
        		            user.setUsername(lmresarr.get(i).get("umd5").toString());
        		            user.setAge(lmresarr.get(i).get("age").toString());
        		            //modified by Wangzhenyue
        		            user.setHeaderurl(lmresarr.get(i).get("headurl").toString());
        		            //user.setHeaderurl(Constant.BASE_URL + lmresarr.get(i).get("headurl").toString());
        		            user.setJiedao(lmresarr.get(i).get("jiedao").toString());
        		            user.setLasttime(lmresarr.get(i).get("time").toString());
        		            user.setSex(lmresarr.get(i).get("sex").toString());
        		            user.setCity(lmresarr.get(i).get("province").toString()+" "+lmresarr.get(i).get("city").toString());
        		            if (lmresarr.get(i).get("geo") != null) {
        		            	user.setGeo(lmresarr.get(i).get("geo").toString());
        		            }
        		            
        		            zainaList.add(user);
        		        }  
        			}
        		}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(ShakeActivity.this, "请求失败,请检查网络！", 0).show();
				//if (progressShow) {
					//pd.dismiss();
				//}
				return;
			}

			@Override
			protected Object parseResponse(String rawJsonData, boolean isFailure)
					throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}
	
	/*
	 * 处理数据，将数据传送到百度地图以标记在地图上
	 */
	public void signMarkerOnMap() {
		Intent intent = new Intent(this, ItemizedOverlayActivity.class);
		SerializableData tmpdata = new SerializableData();
		tmpdata.setZainaList(zainaList);
		tmpdata.setMyLat((int) (DemoApplication.getInstance().getlastloc().getLatitude() * 1000000));
		tmpdata.setMyLon((int) (DemoApplication.getInstance().getlastloc().getLongitude() * 1000000));
		Bundle bd = new Bundle();
		bd.putSerializable("userjwd", tmpdata);
		intent.putExtras(bd);
		startActivity(intent);
	}
}

/**
 * 一个检测手机摇晃的监听器
 */
class ShakeListener implements SensorEventListener {
	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 3000;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;
	// 传感器管理器
	private SensorManager sensorManager;
	// 传感器
	private Sensor sensor;
	// 重力感应监听器
	private OnShakeListener onShakeListener;
	// 上下文
	private Context mContext;
	// 手机上一个位置时重力感应坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 上次检测时间
	private long lastUpdateTime;

	// 构造器
	public ShakeListener(Context c) {
		// 获得监听对象
		mContext = c;
		start();
	}

	// 开始
	public void start() {
		// 获得传感器管理器
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			// 获得重力传感器
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		// 注册
		if (sensor != null) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}

	}

	// 停止检测
	public void stop() {
		sensorManager.unregisterListener(this);
	}

	// 设置重力感应监听器
	public void setOnShakeListener(OnShakeListener listener) {
		onShakeListener = listener;
	}

	// 重力感应器感应获得变化数据
	public void onSensorChanged(SensorEvent event) {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 现在的时间变成last时间
		lastUpdateTime = currentUpdateTime;

		// 获得x,y,z坐标
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 获得x,y,z的变化值
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// 将现在的坐标变成last坐标
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ timeInterval * 10000;
		Log.v("thelog", "===========log===================");
		// 达到速度阀值，发出提示
		if (speed >= SPEED_SHRESHOLD) {
			onShakeListener.onShake();
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
}

// 摇晃监听接口
interface OnShakeListener {
	public void onShake();
}