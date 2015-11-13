package com.zgsc.jmmsr.activity;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.easemob.chat.EMContactManager;
import com.zgsc.jmmsr.R;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zgsc.jmmsr.Constant;
import com.zgsc.jmmsr.DemoApplication;
import com.zgsc.jmmsr.adapter.ZainaAdapter;
import com.zgsc.jmmsr.domain.User;
import com.zgsc.jmmsr.domain.UserMap;
import com.zgsc.jmmsr.game.GameActivity;
import com.zgsc.jmmsr.game.challenge.ChallengeActivity;
import com.zgsc.jmmsr.utils.CommonUtils;
import com.zgsc.jmmsr.utils.HttpRestClient;
import com.zgsc.jmmsr.utils.JsonToMapList;
import com.zgsc.jmmsr.utils.PreferenceUtils;
import com.zgsc.jmmsr.utils.SerializableData;
import com.zgsc.jmmsr.utils.SerializableMap;

import android.app.AlertDialog;
/**
 * 在一个圆周上添加自定义overlay.
 * @author Wangzhenyue
 * @version 1.0
 */
public class ItemizedOverlayActivity extends Activity {

	final static String TAG = "MainActivty";
	static MapView mMapView = null;
	private MapController mMapController = null;
	private SerializableData tmpdata;
	public MKMapViewListener mMapListener = null;
	Button testItemButton = null;
	Button removeItemButton = null;
	Button removeAllItemButton = null;
	EditText indexText = null;
	OverlayTest ov = null;
	private static List<UserMap> zainaList;//在哪数据集合
	private static int curUserIndex;
	private static int PastUserIndex;
	private ProgressDialog progressDialog;
	//AsyHan ah;
	
	public static final int FRIST_GET_DATE = 111;
	public static final int REFRESH_GET_DATE = 112;
	public static final int LOADMORE_GET_DATE = 113;
	/**
	 * 圆心经纬度坐标
	 */
	// int cLat = 39909230 ;
	// int cLon = 116397428 ;
	// 存放overlayitem
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	// 存放overlay图片
	public List<Drawable> res = new ArrayList<Drawable>();
	private List<Integer> lonList;
	private List<Integer> latList;
	private int myLon, myLat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(DemoApplication.strKey,
					new DemoApplication.MyGeneralListener());
		}
		setContentView(R.layout.activity_itemizedoverlay);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		//初始化附近的人数据
		tmpdata = (SerializableData) getIntent().getSerializableExtra("userjwd");
		zainaList = new ArrayList<UserMap>();
		if (tmpdata != null) {
			zainaList = tmpdata.getZainaList();
		}
		
		lonList = new ArrayList<Integer>();
		latList = new ArrayList<Integer>();
		//初始化周围的人数据
		initUserGeo(zainaList);
		//设置地图中心点，为当前用户所在的位置
		if (myLat != 0 && myLon != 0)
			mMapController.setCenter(new GeoPoint(myLat, myLon));
		initMapView();
		mMapView.getController().setZoom(17);
		//设置地图中心点
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		testItemButton = (Button) findViewById(R.id.button1);
		removeItemButton = (Button) findViewById(R.id.button2);
		removeAllItemButton = (Button) findViewById(R.id.button3);
		//DistanceUtil.getDistance(new GeoPoint(24629869, 118094329), new GeoPoint(2463131, 118092358));
		
		Drawable marker = ItemizedOverlayActivity.this.getResources()
				.getDrawable(R.drawable.icon_marka);
		mMapView.getOverlays().clear();
		ov = new OverlayTest(marker, this, mMapView);
		mMapView.getOverlays().add(ov);

		//退出
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				ItemizedOverlayActivity.this.finish();
			}
		};
		OnClickListener removeListener = new OnClickListener() {
			public void onClick(View v) {
				testRemoveItemClick();
			}
		};
		OnClickListener removeAllListener = new OnClickListener() {
			public void onClick(View v) {
				testRemoveAllItemClick();
			}
		};

		testItemButton.setOnClickListener(clickListener);
		removeItemButton.setOnClickListener(removeListener);
		removeAllItemButton.setOnClickListener(removeAllListener);
		mMapListener = new MKMapViewListener() {

			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// TODO Auto-generated method stub
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					Toast.makeText(ItemizedOverlayActivity.this, title,
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub

			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager,
				mMapListener);
		//标记
		signMarker();
	}
	
	/*
	 * 将周围的人标记在地图上
	 */
	public void signMarker() {
		int iSize = threeMin(lonList.size(), latList.size(), res.size());
		for (int i = 0; i < iSize; i++) {
			OverlayItem item = new OverlayItem(new GeoPoint(latList.get(i), lonList.get(i)),
					"item" + i, "item" + i);
			item.setMarker(res.get(i % (res.size())));
			ov.addItem(item);
		}		
	}
	
	/*
	 * 处理解密结果
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// 当requestCode、resultCode同时为0，也就是处理特定的结果
		if (requestCode == 120 && resultCode == 120) {
			Bundle res = intent.getExtras();
			// 取出Bundle中的数据
			String s = res.getString("gameResult");
			// 修改city文本框的内容
		 if (s.equals("success")) {
			Intent intent1 = new Intent(ItemizedOverlayActivity.this, Userinfo.class);
			 intent1.putExtra("userId", zainaList.get(curUserIndex).getUsername());
			 intent1.putExtra("nickname", zainaList.get(curUserIndex).getNick());
			 intent1.putExtra("headurl", zainaList.get(curUserIndex).getHeaderurl());
			 intent1.putExtra("UserSex", zainaList.get(curUserIndex).getSex());
			 intent1.putExtra("UserAge", zainaList.get(curUserIndex).getAge());
			 intent1.putExtra("UserArea", zainaList.get(curUserIndex).getCity());
			 intent1.putExtra("UserZaina", zainaList.get(curUserIndex).getJiedao());
			 startActivity(intent1);
		 } else if (s.equals("fail")) {
				Toast.makeText(ItemizedOverlayActivity.this, "解密失败:(", 0).show();
			}
		} else if (requestCode == 121 && resultCode == 121) {
			Bundle res = intent.getExtras();
			toChallenge(res.getString("score"));
		}
	}    

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
	}

	private void initMapView() {
		mMapView.setLongClickable(true);
		// mMapController.setMapClickEnable(true);
		// mMapView.setSatellite(false);
	}

	public void testRemoveAllItemClick() {
		// 清除所有添加的Overlay
		ov.removeAll();
		mMapView.refresh();
		res.clear();//移除标记图标
		//清除经纬度数据
		lonList.clear();
		latList.clear();
	}

	public void testRemoveItemClick() {
		// 删除最后添加的overlay
		if (ov.size() > 0)
			ov.removeItem(ov.getItem(ov.size() - 1));
		mMapView.refresh();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_mainbaidumap, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	private Drawable drawTextAtBitmap(String text) {

		Bitmap bp = null;
		AssetManager asm = getAssets();
		try {
			InputStream is = asm.open("bg.png");
			bp = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// name:图片的名称
		int x = bp.getWidth();
		int y = bp.getHeight();

		// 创建一个和原图同样大小的位图
		Bitmap newbit = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newbit);
		Paint paint = new Paint();
		// 在原始位置0，0插入原图
		canvas.drawBitmap(bp, 0, 0, paint);
		// paint.setColor(Color.parseColor("#dedbde"));
		paint.setColor(Color.RED);
		paint.setTextSize(30);
		// 在原图指定位置写上字
		//canvas.drawText(text, 53, 30, paint);
		canvas.drawText(text, 0, 30, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储
		canvas.restore();
		return new BitmapDrawable(newbit);
	}

	public void initUserGeo(List<UserMap> zainaList) {
		myLat = tmpdata.getMyLat();
		myLon = tmpdata.getMyLon();
		if (zainaList.size() == 0) {
			Toast.makeText(this, "暂时找不到周围的人，可能他们还未开启共享地理位置...",
					Toast.LENGTH_LONG).show();
			Toast.makeText(this, "摇一摇可共享地理位置...",
					Toast.LENGTH_LONG).show();
		}
		for (int i = 0; i < zainaList.size(); i++) {
			res.add(drawTextAtBitmap(zainaList.get(i).getNick()));
			String jwd = zainaList.get(i).getGeo();
			int index = jwd.indexOf(",");
			if (index == -1)
				continue;
			else {
				double lon, lat;
				lon = Double.parseDouble(jwd.substring(0, index));
				lat = Double.parseDouble(jwd.substring(index + 1, jwd.length()));
				lonList.add((int) (lon * 1000000));
				latList.add((int) (lat * 1000000));
			}
		}
		return ;
	}
	
	public int threeMin(int a, int b, int c) {
		int min = a;
		if (b < a)
			min = b;
		if (c < min)
			min = c;
		return min;
	}
	
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Context mContext = null;
		PopupOverlay pop = null;

		Toast mToast = null;

		public OverlayTest(Drawable marker, Context context, MapView mapView) {
			super(marker, mapView);
			this.mContext = context;
			pop = new PopupOverlay(ItemizedOverlayActivity.mMapView,
					new PopupClickListener() {

						@Override
						public void onClickedPopup(int index) {
							if (null == mToast)
								mToast = Toast.makeText(mContext, "popup item :"
										+ index + " is clicked.",
										Toast.LENGTH_SHORT);
							else
								mToast.setText("popup item :" + index
										+ " is clicked.");
							mToast.show();
						}
					});
			// 自2.1.1 开始，使用 add/remove 管理overlay , 无需调用以下接口.
			// populate();

		}

		protected boolean onTap(int index) {
			//TODO
			//modified by Wangzhenyue
			/*
			Intent intent = new Intent(ItemizedOverlayActivity.this, TestActivity.class);
			startActivity(intent);
			*/
			PastUserIndex = index;
			AlertDialog.Builder builder = new Builder(ItemizedOverlayActivity.this);
			String[] strarr = {"进入解密游戏","发起挑战","返回"};
		       builder.setItems(strarr, new DialogInterface.OnClickListener()
		        {
		            public void onClick(DialogInterface arg0, int arg1) {
		                if (arg1 == 0) {
		                	curUserIndex = PastUserIndex;
		    				startActivityForResult(new Intent(ItemizedOverlayActivity.this, GameActivity.class), 120);
		                }else if (arg1 == 1) {
		                	curUserIndex = PastUserIndex;
		    				//toChallenge();
		                	Intent i = new Intent(ItemizedOverlayActivity.this, ChallengeActivity.class);
		                	i.putExtra("mode", "toChallenge");
		                	startActivityForResult(i, 121);
		                } else {
		                	//返回
		                } 
		            }
		        });
		        builder.show();
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}
		
		

		// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
		/*
		 * @Override protected OverlayItem createItem(int i) { return
		 * mGeoList.get(i); }
		 * 
		 * @Override public int size() { return mGeoList.size(); }
		 */
	}
	
	public void toChallenge(final String score) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(zainaList.get(curUserIndex).getUsername(), "向你发起挑战！得分为:" + score);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "请求添加好友失败:" + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}
}
