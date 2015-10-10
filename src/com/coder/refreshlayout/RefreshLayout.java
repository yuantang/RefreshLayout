package com.coder.refreshlayout;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;
public class RefreshLayout extends SwipeRefreshLayout {

	private final int mTouchSlop;
	private ListView mListView;
	private OnLoadListener mOnLoadListener;
	private float firstTouchY;
	private float lastTouchY;
	private boolean isLoading = false;

	public RefreshLayout(Context context) {
		this(context, null);
	}

	@SuppressLint("Recycle")
	public RefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.refresh);
		int color1= array.getColor(R.styleable.refresh_Color1, color.white);
		int color2= array.getColor(R.styleable.refresh_Color2, color.holo_blue_light);
		int color3= array.getColor(R.styleable.refresh_Color3, color.holo_green_dark);
		int color4= array.getColor(R.styleable.refresh_Color4, color.holo_red_light);
		setColorSchemeResources(color1,color2,color3,color4);
	}

	//set the child view of RefreshLayout,ListView
	public void setChildView(ListView mListView) {
		this.mListView = mListView;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			firstTouchY = event.getRawY();
			break;

		case MotionEvent.ACTION_UP:
			lastTouchY = event.getRawY();
			if (canLoadMore()) {
				loadData();
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	private boolean canLoadMore() {
		return isBottom() && !isLoading && isPullingUp();
	}

	private boolean isBottom() {
		if (mListView.getCount() > 0) {
			if (mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1 &&
					mListView.getChildAt(mListView.getChildCount() - 1).getBottom() <= mListView.getHeight()) {
				return true;
			}
		}
		return false;
	}

	private boolean isPullingUp() {
		return (firstTouchY - lastTouchY) >= mTouchSlop;
	}

	private void loadData() {
		if (mOnLoadListener != null) {
			setLoading(true);
		}
	}

	public void setLoading(boolean loading) {
		if (mListView == null) return;
		isLoading = loading;
		if (loading) {
			if (isRefreshing()) {
				setRefreshing(false);
			}
			mListView.setSelection(mListView.getAdapter().getCount() - 1);
			mOnLoadListener.onLoad();
		} else {
			firstTouchY = 0;
			lastTouchY = 0;
		}
	}
	public void setOnLoadListener(OnLoadListener loadListener) {
		mOnLoadListener = loadListener;
	}

	public interface OnLoadListener {
		public void onLoad();
	}
}