package com.comvee.tnb.ui.tool;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;
import com.comvee.tnb.config.ConfigUrlMrg;
import com.comvee.tnb.http.ComveeHttp;
import com.comvee.tnb.http.ComveeHttpErrorControl;
import com.comvee.tnb.http.ComveePacket;
import com.comvee.tnb.http.OnHttpListener;
import com.comvee.tnb.model.HeatInfo;
import com.comvee.tnb.widget.TitleBarView;
import com.comvee.tnb.widget.XExpandableListView.IXListViewListener;
import com.comvee.tool.ImageLoaderUtil;

//食物血糖生成指数
public class BloodSugarFragment extends BaseFragment implements OnHttpListener, IXListViewListener, OnItemClickListener {
	private TitleBarView mBarView;
	private ListView mListView;
	private ArrayList<HeatInfo> listItems = null;
	private MyAdapter mAdapter;

	public static BloodSugarFragment newInstance() {
		return new BloodSugarFragment();
	}

	public BloodSugarFragment() {
	}

	@Override
	public int getViewLayoutId() {
		return R.layout.fragment_blood_sugar;
	}

	@Override
	public void onLaunch(Bundle bundle) {
		mBarView = (TitleBarView) findViewById(R.id.main_titlebar_view);
		mBarView.setLeftDefault(this);
		init();
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	private void init() {
		mBarView.setTitle("食物血糖生成指数(GI)");
		mListView = (ListView) findViewById(R.id.lv_task);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		requestBloodSugar();
	}

	private void requestBloodSugar() {
		showProgressDialog("请稍候...");

		// ComveeHttp http = new ComveeHttp(getApplicationContext(),
		// "http://192.168.9.101:8080/health/mobile/tool/loadGiFoods");
		ComveeHttp http = new ComveeHttp(getApplicationContext(), ConfigUrlMrg.BLOOD_SUGAR);
		http.setPostValueForKey("food_cate", "0");
		http.setOnHttpListener(1, this);
		http.startAsynchronous();
	}

	@Override
	public void onSussece(int what, byte[] b, boolean fromCache) {
		cancelProgressDialog();
		switch (what) {
		case 1:
			parseBloodSugar(b, fromCache);
			break;
		default:
			break;
		}
	}

	private void parseBloodSugar(byte[] b, boolean fromCache) {

		try {
			ComveePacket packet = ComveePacket.fromJsonString(b);
			if (packet.getResultCode() == 0) {
				if (listItems == null) {
					listItems = new ArrayList<HeatInfo>();
				} else {
					listItems.clear();
				}

				JSONArray array = packet.getJSONObject("body").getJSONArray("rows");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					JSONObject mJsonObject = array.getJSONObject(i);
					HeatInfo info = new HeatInfo();
					info.name = mJsonObject.getString("foodName");
					info.cate = mJsonObject.getString("giId");
					info.picurl = mJsonObject.getString("imgUrl");
					listItems.add(info);
				}
				mAdapter.notifyDataSetChanged();

			} else {
				showToast(packet.getResultMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFialed(int what, int errorCode) {
		cancelProgressDialog();
		ComveeHttpErrorControl.parseError(getActivity(), errorCode);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listItems == null ? 0 : listItems.size();
		}

		@Override
		public HeatInfo getItem(int arg0) {
			return listItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.item_blood_sugar, null);
				holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HeatInfo info = listItems.get(arg0);
			holder.tvTitle.setText(info.name);
			// MainActivity.IMG_LOADER.display(holder.ivIcon, info.imgUrl);
			if (info.picurl.startsWith("http://")) {
				ImageLoaderUtil.getInstance(mContext).displayImage(info.picurl, holder.ivIcon, ImageLoaderUtil.user_options);
			} else {
				ImageLoaderUtil.getInstance(mContext).displayImage("http://" + info.picurl, holder.ivIcon, ImageLoaderUtil.user_options);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView ivIcon;
			TextView tvTitle;
		}

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		HeatInfo info = listItems.get(position);
		toFragment(BloodSugarListFragment.newInstance(info), true, true);
	}
}
