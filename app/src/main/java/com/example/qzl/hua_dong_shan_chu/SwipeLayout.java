package com.example.qzl.hua_dong_shan_chu;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    private ViewDragHelper viewDragHelper;
    private float downX,downY;//按下的一个x和y值

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

    /**
     * 定义枚举常量,记录状态
     */
    enum SwipeState{
        Open,Close;
    }
    private SwipeState currentState = SwipeState.Close;//记录当前状态，默认关闭
    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 获取子控件
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
     *
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
     *
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
        contentView.layout(0, 0, contentWidth, deleteHeight);
        deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
    }

    /**
     * 是否拦截事务
     *
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(event);//是否进行处理
        //如果有打开的，则需要直接拦截，交给onTouch处理
        if (!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
            result = true;
        }
        return result;
    }

    /**
     * 处理触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果当前有打开的，则下面的逻辑不能执行
        if (!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
            //先关闭已经打开的layout
            SwipeLayoutManager.getInstance().closerSwipeLayout();
            requestDisallowInterceptTouchEvent(true);//请求不拦截
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取x和y方向移动的距离
                float moveX = event.getX();
                float moveY = event.getY();
                float delatX = moveX - downX;//x方向移动的距离
                float delatY = moveY - downY;//y方向移动的距离
                if (Math.abs(delatX) > Math.abs(delatY)){
                    //表示移动是偏向与水平方向，那么应该SwipeLayout应该处理，请求listView不要拦截
                    requestDisallowInterceptTouchEvent(true);//不拦截,不让listView处理
                }
                //更新downX，downY
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //交给onInterceptHoverEvent处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 做伴随移动
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == contentView) {
                //手动移动deleteView
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop() + dy, deleteView.getRight() + dx, deleteView.getBottom() + dy);
            } else if (changedView == deleteView) {
                contentView.layout(contentView.getLeft() + dx, contentView.getTop() + dy, contentView.getRight() + dx, contentView.getBottom() + dy);
            }

            //判断开和关闭的逻辑
            if (contentView.getLeft() == 0 && currentState != SwipeState.Close){
                //说明应该讲state更改为关闭
                currentState = SwipeState.Close;
                //回调接口关闭的方法
                if (listener != null){
                    listener.onClose(getTag());
                }
                ////当前的SwipeLayout已经关闭，需要让manager清空一下
                SwipeLayoutManager.getInstance().clearCurrentLayout();
            }else if (contentView.getLeft() == -deleteWidth && currentState != SwipeState.Open){
                //说明应该讲state更改为开
                currentState = SwipeState.Open;
                //回调接口打开的方法
                if (listener != null){
                    listener.onOpen(getTag());
                }
                //当前的SwipeLayout已经打开，需要让manager记录一下
                SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == deleteView;
        }

        /**
         * 拖拽范围
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        /**
         * 控制移动
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0) left = 0;
                if (left < -deleteWidth) left = -deleteWidth;
            } else if (child == deleteView) {
                if (left > contentWidth) left = contentWidth;
                if (left < (contentWidth - deleteWidth)) left = (contentWidth - deleteWidth);
            }
            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth / 2) {
                //应该打开
                open();
            } else {
                //应该关闭
                close();
            }
        }
    };

    /**
     * 关闭的方法
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());//平滑移动
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);//刷新
    }

    /**
     * 打开的方法
     */
    public void open() {
        viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());//平滑移动
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);//刷新
    }

    /**
     * 动画没有完成，继续完成
     */
    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);//刷新
        }
    }

    private OnSwipeStateChangeListener listener;
    public void setOnSwipeStateChageListener(OnSwipeStateChangeListener listener){
        this.listener = listener;
    }
    public interface OnSwipeStateChangeListener{
        void onOpen(Object tag);
        void onClose(Object tag);
    }
}
