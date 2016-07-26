package com.example.qzl.hua_dong_shan_chu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Qzl on 2016-07-26.
 */
public class SwipeLayout extends FrameLayout {

    private View contentView;//item内容区域的View
    private View deleteView;//delete区域的View
    private int deleteHeight;//delete区域的高度
    private int deleteWidth;//delete区域的宽度
    private int contentWidth;//item内容区域的宽度

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

    }

    /**
     *获取子控件
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //item内容区域的View
        contentView = getChildAt(0);
        //delete区域的View
        deleteView = getChildAt(1);
    }

    /**
     * 获取宽高（OnMeasure已经执行完）
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取控件的宽和高
        deleteHeight = deleteView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
        contentWidth = contentView.getMeasuredWidth();
    }

    /**
     * 重写摆放位置
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //重新摆放位置
        contentView.layout(0,0,contentWidth,deleteHeight);
        deleteView.layout(contentView.getRight(),0,contentView.getRight()+deleteWidth,deleteHeight);
    }
}
