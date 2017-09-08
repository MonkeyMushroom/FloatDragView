package com.monkey.floatdragviewdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.Arrays;

import static android.content.Context.WINDOW_SERVICE;

/**
 * 可悬浮可拖拽FloatDragView的管理器，用于base_url的选择
 */
public class FloatDragViewManager implements FloatDragView.OnClickListener, FloatDragView.OnScrollListener {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mFdvParams;
    private WindowManager.LayoutParams mRvParams;
    private FloatDragView mFloatDragView;
    private RecyclerView mSpinnerRv;

    private String[] mUrlArr = new String[]{"测试", "正式", "仿真"};
    private Context mContext;
    private boolean mIsSpinnerShow;

    public void showFloatDragView() {
        mContext = BaseApplication.getInstance();
        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mFloatDragView = new FloatDragView(mContext);
        mFloatDragView.setOnClickListener(this);
        mFloatDragView.setOnScrollListener(this);
        mFloatDragView.setText(mUrlArr[0]);
        mFdvParams = new WindowManager.LayoutParams();
        mFdvParams.type = WindowManager.LayoutParams.TYPE_PHONE;//级别
        mFdvParams.format = PixelFormat.TRANSPARENT;//背景透明
        mFdvParams.gravity = Gravity.LEFT | Gravity.TOP;//位置
        mFdvParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mFdvParams.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽高
        mFdvParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mFloatDragView, mFdvParams);
    }

    @Override
    public void onClick() {
        if (mSpinnerRv == null) {
            mSpinnerRv = new RecyclerView(mContext);
            mSpinnerRv.setBackgroundResource(R.drawable.corner_cyan_bg);
            mSpinnerRv.setLayoutManager(new LinearLayoutManager(mContext));
            mSpinnerRv.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mFloatDragView.setText(mUrlArr[position]);
                    Constant.BASE_URL = Constant.URL_ARR[position];
                    Toast.makeText(mContext, Constant.BASE_URL, Toast.LENGTH_SHORT).show();
                    mWindowManager.removeView(mSpinnerRv);
                    mIsSpinnerShow = false;
                }
            });
            mSpinnerRv.setAdapter(new DataAdapter());
        }
        if (mIsSpinnerShow) {
            mWindowManager.removeView(mSpinnerRv);
        } else {
            mRvParams = new WindowManager.LayoutParams();
            mRvParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mRvParams.gravity = Gravity.LEFT | Gravity.TOP;
            mRvParams.format = PixelFormat.TRANSPARENT;
            mRvParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mRvParams.x = mFdvParams.x;
            mRvParams.y = mFdvParams.y + mFloatDragView.getHeight();
            mRvParams.width = mFloatDragView.getWidth();
            mRvParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowManager.addView(mSpinnerRv, mRvParams);
        }
        mIsSpinnerShow = !mIsSpinnerShow;
    }

    /**
     * 滑动监听，动态改变按钮和列表的位置
     */
    @Override
    public void onScroll(int x, int y) {
        mFdvParams.x = x;
        mFdvParams.y = y;
        mWindowManager.updateViewLayout(mFloatDragView, mFdvParams);
        if (mIsSpinnerShow) {
            mRvParams.x = mFdvParams.x;
            mRvParams.y = mFdvParams.y + mFloatDragView.getHeight();
            mWindowManager.updateViewLayout(mSpinnerRv, mRvParams);
        }
    }

    private class DataAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        DataAdapter() {
            super(R.layout.adapter_spinner_item, Arrays.asList(mUrlArr));
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            TextView tv = helper.getView(R.id.item_tv);
            tv.setText(item);
            tv.setTextColor(mContext.getResources().getColor(R.color.base_cyan));
            int small_margin = mContext.getResources().getDimensionPixelOffset(R.dimen.small_margin);
            tv.setPadding(0, small_margin, 0, small_margin);
        }
    }

    public void removeFloatDragView() {
        mWindowManager.removeView(mFloatDragView);
        if (mSpinnerRv != null && mIsSpinnerShow) {
            mWindowManager.removeView(mSpinnerRv);
        }
    }
}
