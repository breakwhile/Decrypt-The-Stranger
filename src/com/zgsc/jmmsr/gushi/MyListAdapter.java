package com.zgsc.jmmsr.gushi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zgsc.jmmsr.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgsc.jmmsr.activity.Userinfo;
import com.zgsc.jmmsr.db.ZanDao;
import com.zgsc.jmmsr.domain.MyBean;
import com.zgsc.jmmsr.gushi.NoScrollGridView;
import com.zgsc.jmmsr.utils.CommonUtils;
import com.zgsc.jmmsr.utils.ImageOptions;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter{

	private List<MyBean> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	//private TextView zan_tv;
	private ZanDao zdao ;
	
	public MyListAdapter(Context context,List<MyBean> zainaList) {
		mInflater = LayoutInflater.from(context);
		mContext=context;
		this.mList=zainaList;
		//判断数据库中是否有这个赞
		zdao = new ZanDao(mContext);
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public MyBean getItem(int position) {
		return mList.get(position);
	}

 
	public String getUser(int position) {
		return mList.get(position).getName();
	}
	
	 @Override
	public long getItemId(int position) {
		//return getItem(position).id;
		String s = mList.get(position).getId(); 
		return Long.valueOf(s).longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.gushi_list_item, null);
			holder.avator=(ImageView)convertView.findViewById(R.id.avator);
			holder.name=(TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.gridView=(NoScrollGridView)convertView.findViewById(R.id.gridView);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.zan = (TextView) convertView.findViewById(R.id.zan);
			//zan_tv = holder.zan;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MyBean bean = getItem(position);
		
		ImageLoader.getInstance().displayImage(bean.getAvator(), holder.avator,ImageOptions.getOptions());
		holder.name.setText(bean.getName());
		holder.content.setText(bean.getContent());
		//设置时间显示
		holder.time.setText(jisuan(bean.getTime()));
		 
		 //System.out.println(bean.getId());
		if(zdao.getZanGushi(bean.getId())){
			if(CommonUtils.isNullOrEmpty(bean.getZan())){
				holder.zan.setText("♥已赞");
			}else{
				holder.zan.setText("♥"+bean.getZan()+"人已赞");
				//holder.zan.setText("♥已赞");
			}
			
		}else{
			if(CommonUtils.isNullOrEmpty(bean.getZan())){
				holder.zan.setText("♡赞");
			}else{
				holder.zan.setText("♡"+bean.getZan()+"人     赞");
				//holder.zan.setText("♡赞");
			}
			
		}
		//点击名称跳转
		holder.name.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.startActivity(new Intent(mContext , Userinfo.class).putExtra("userId", bean.getUser())); 
			}
		});
		//点击头像跳转
		holder.avator.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.startActivity(new Intent(mContext , Userinfo.class).putExtra("userId", bean.getUser())); 
			}  
		});
		
		//点赞事件
		holder.zan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(zdao.getZanGushi(bean.getId())){
					
					if(CommonUtils.isNullOrEmpty(bean.getZan())){
						holder.zan.setText("♡赞");
					}else{
						holder.zan.setText("♡"+bean.getZan()+"人     赞");
						//holder.zan.setText("♡赞"); 
					}
					zdao.deleteContact(bean.getId());
				}else{
					if(CommonUtils.isNullOrEmpty(bean.getZan())){
						holder.zan.setText("♥已赞");
					}else{ 
						holder.zan.setText("♥"+bean.getZan()+"人已赞");
						//holder.zan.setText("♥已赞");
					} 
					zdao.saveContact(bean.getId());
				}
				//holder.zan.setText("♥已赞");
				
			}
		});
		
		if(bean.getUrls()!=null&&bean.getUrls().length>0){
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new MyGridAdapter(bean.getUrls(), mContext));
			holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					imageBrower(position,bean.getUrls());
				}
			});
		}else{
			holder.gridView.setVisibility(View.GONE);
		}
		return convertView;
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
	private static class ViewHolder {

		public TextView name;
		public ImageView avator;
		TextView content;
		TextView time;
		TextView zan;
		NoScrollGridView gridView;
	}
	/*@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}*/
	
	@SuppressLint("SimpleDateFormat")
	private String jisuan(String shijianchuo){
		  long unixLong = 0;
		  StringBuffer sb = null;
		  try {
              unixLong = Long.parseLong(shijianchuo) * 1000;
           } catch(Exception ex) {
              //System.out.println("String转换Long错误，请确认数据可以转换！");
         }
		  
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       java.util.Date now ;
	    try {
	        now = df.parse(df.format(new Date())); 
	      // java.util.Date date=df.parse("2004-01-02 11:30:24");
	       java.util.Date date =  df.parse(df.format(unixLong));//获取参数时间
	       long l=now.getTime()- date.getTime();
	       long day=l/(24*60*60*1000);
	       long hour=(l/(60*60*1000)-day*24);
	       long min=((l/(60*1000))-day*24*60-hour*60);
	       long s=(l/1000-day*24*60*60-hour*60*60-min*60);
	        
	        sb = new StringBuffer();
	       //sb.append("发表于：");
	       if(day > 0){
	           sb.append(day+"天前");
	       }else if(hour > 0 ){
	           sb.append(hour+"小时前");
	       }else if(min > 0 ){
	           sb.append(min+"分钟前");
	       }else{
	    	   sb.append("刚刚");
	       }
	      // sb.append(s+"秒 前");
	      // sb.append("前");
	       //System.out.println(sb.toString());
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
		return sb.toString();
	       
	}
}
