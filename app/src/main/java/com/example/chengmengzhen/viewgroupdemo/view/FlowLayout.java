package com.example.chengmengzhen.viewgroupdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmengzhen on 16/7/8.
 */
public class FlowLayout extends ViewGroup {
    private final static String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得模式和宽高
        int MeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int MeasureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int MeasureHight = MeasureSpec.getSize(heightMeasureSpec);
        int MeasureHightMode = MeasureSpec.getMode(heightMeasureSpec);

        //记录的最大宽度
        int recordMaxWidth = 0;
        //总的高度
        int totalHeight = 0;
        //当前行的高度
        int lineHeight = 0;
        //但前行的宽度
        int lineWidth = 0;
        //获得所有的子View
        int childCount = getChildCount();
        //遍历子View
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //测量子宽度
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //获得子控件的宽高
            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
            int childHeight = lp.bottomMargin + lp.topMargin + child.getMeasuredHeight();
            //如果控件已经放不下了
            if (lineWidth + childWidth > MeasureWidth) {
                recordMaxWidth = Math.max(recordMaxWidth, lineWidth);
                lineWidth = childWidth;
                lineHeight = childHeight;
                totalHeight += lineHeight;

            } else {
                lineWidth = lineWidth + childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //如果是最后一个
            if (i == childCount - 1) {
                recordMaxWidth = Math.max(recordMaxWidth, lineWidth);
                totalHeight += lineHeight;
            }
        }

        setMeasuredDimension(MeasureWidthMode == MeasureSpec.EXACTLY ? MeasureWidth : recordMaxWidth,
                MeasureHightMode == MeasureSpec.EXACTLY ? MeasureHight : totalHeight);

    }

    //  存储所有的View，按行记录
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    //  记录每一行的最大高度
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    //
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        //FlowLayout的宽度
        int width = getWidth();
        //记录每一行的宽度
        int lineWidth = 0;
        //记录每一行的最大高度
        int lineMaxHight = 0;
        //一行view容器
        List<View> lineViews = new ArrayList<>();
        int childCount = getChildCount();
        //遍历子View
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //获得子控件的宽高
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
            int childHeight = lp.bottomMargin + lp.topMargin + child.getMeasuredHeight();
            if (lineWidth + childWidth > width) {
                mAllViews.add(lineViews);
                mLineHeight.add(lineMaxHight);
                //重新创建一个list对象
                lineViews = new ArrayList<View>();
                //把容不下上一行的view添加到当前集合
                lineViews.add(child);
                //重新设置每行最大高度
                lineMaxHight = childHeight;
                //设置行当前宽度
                lineWidth = childWidth;
            } else {
                //当前行容的下view ，添加到list集合中
                lineViews.add(child);
                //保存当前行的最大高度
                lineMaxHight = Math.max(lineMaxHight, childHeight);
                //设置当前行的宽度
                lineWidth = lineWidth + childWidth;
            }
        }
        mAllViews.add(lineViews);
        mLineHeight.add(lineMaxHight);


        int top = 0;
        int left = 0;
        int lineHeight = 0;
        //计算有几行view
        int lineNum = mAllViews.size();
        //遍历
        for (int i = 0; i < lineNum; i++) {
            //取出一行view
            List<View> views = mAllViews.get(i);
            //取出当前行的最大高度
            lineHeight = mLineHeight.get(i);
            for (int j = 0; j < views.size(); j++) {
                //取出view
                View child = views.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                //计算每个view的布局参数
                int cl = left + lp.leftMargin;
                int ct = top + lp.topMargin;
                int cr = cl + child.getMeasuredWidth();
                int cb = ct + child.getMeasuredHeight();
                Log.e(TAG, child + " , l = " + cl + " , t = " + ct + " , r ="
                        + cr + " , b = " + cb+" ,child.getMeasuredWidth()"+child.getMeasuredWidth());
                //给view布局
                child.layout(cl, ct, cr, cb);
                left = left + lp.rightMargin + lp.leftMargin + child.getMeasuredWidth();
            }
            left = 0;
            top += lineHeight;
        }
    }
    /*********************************解决类造型异常**********************************************************/
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    // 继承自margin，支持子视图android:layout_margin属性
    public static class LayoutParams extends MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }


        public LayoutParams(int width, int height) {
            super(width, height);
        }


        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }


        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
