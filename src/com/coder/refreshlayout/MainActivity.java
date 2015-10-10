package com.coder.refreshlayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.coder.refreshlayout.RefreshLayout.OnLoadListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements OnRefreshListener,OnLoadListener{
	RefreshLayout mRefresh;
	ListView ls_main;
	private boolean isRefresh = false;//是否刷新中  
	View   footerLayout ;
	ProgressBar progressBar;
	ListView	mListView; 
	SimpleAdapter	mAdapter;
	private ArrayList<Map<String, Object>> mData = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initView() {
		mRefresh=(RefreshLayout) findViewById(R.id.swipe_container);
		mListView=(ListView) findViewById(R.id.list);

		footerLayout = getLayoutInflater().inflate(R.layout.layout_footer, null);
		progressBar = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
		mListView.addFooterView(footerLayout);
		mRefresh.setChildView(mListView);

		mRefresh.setOnRefreshListener(this);  
		mRefresh.setOnLoadListener(this);

	}

	private void initData() {
		final Dialog dialog=new Dialog(this,R.style.lodingdialog);
		dialog.setContentView(R.layout.layout_loding_dialog);
		dialog.show();

		for (int i = 0; i < 10; i++) {
			Map<String, Object> listItem = new HashMap<>();
			listItem.put("img", R.drawable.ic_launcher);
			listItem.put("text", "Item " + i);
			mData.add(listItem);
		}
		mAdapter = new SimpleAdapter(this, mData, R.layout.list_item, new String[]{"img", "text"}, new int[]{R.id.img, R.id.text});
		mListView.setAdapter(mAdapter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss(); 
			}
		}, 4000);

	}

	/**
	 * 模拟下拉刷新时获取新数据
	 * simulate getting new data when pull to refresh
	 */
	private void getNewTopData() {
		Map<String, Object> listItem = new HashMap<>();
		listItem.put("img", R.drawable.ic_launcher);
		listItem.put("text", "New Top Item " + mData.size());
		mData.add(0, listItem);
	}

	/**
	 * 模拟上拉加载更多时获得更多数据
	 * simulate load more data to bottom
	 */
	private void getNewBottomData() {
		int size = mData.size();
		for (int i = 0; i < 3; i++) {
			Map<String, Object> listItem = new HashMap<>();
			listItem.put("img", R.drawable.ic_launcher);
			listItem.put("text", "New Bottom Item " + (size + i));
			mData.add(listItem);
		}
	}

	/**
	 * 模拟一个耗时操作，获取完数据后刷新ListView
	 * simulate update ListView and stop refresh after a time-consuming task
	 */
	private void simulateFetchingData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getNewTopData();
				mRefresh.setRefreshing(false);
				mAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.this, "Refresh Finished!", Toast.LENGTH_SHORT).show();
			}
		}, 2000);
	}

	/**
	 * 模拟一个耗时操作，加载完更多底部数据后刷新ListView
	 * simulate update ListView and stop load more after after a time-consuming task
	 */
	private void simulateLoadingData() {
		progressBar.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getNewBottomData();
				mRefresh.setLoading(false);
				mAdapter.notifyDataSetChanged();
				progressBar.setVisibility(View.GONE);
				Toast.makeText(MainActivity.this, "Load Finished!", Toast.LENGTH_SHORT).show();
			}
		}, 2000);
	}

	@Override
	public void onRefresh() {
		simulateFetchingData();
	}

	@Override
	public void onLoad() {
		simulateLoadingData();
	}
	@Override
	protected void onDestroy() {
		mRefresh.setRefreshing(false);
		mRefresh.setLoading(false);

		super.onDestroy();
	}
}
