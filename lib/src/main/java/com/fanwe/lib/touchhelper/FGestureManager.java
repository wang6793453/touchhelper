package com.fanwe.lib.touchhelper;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class FGestureManager
{
    private final ViewGroup mViewGroup;
    private final FTouchHelper mTouchHelper = new FTouchHelper();
    private final FGestureDetector mGestureDetector;
    private FScroller mScroller;
    private ViewConfiguration mViewConfiguration;

    private Callback mCallback;

    public FGestureManager(ViewGroup viewGroup)
    {
        mViewGroup = viewGroup;
        mGestureDetector = new FGestureDetector(getContext(), new FGestureDetector.Callback()
        {
            @Override
            public boolean onDown(MotionEvent e)
            {
                return getCallback().onGestureDown(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                return getCallback().onGestureScroll(e2);
            }

            @Override
            public void onFinishEvent(MotionEvent event, float velocityX, float velocityY)
            {
                getCallback().onGestureFinish(event, velocityX, velocityY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return getCallback().onGestureSingleTapUp(e);
            }
        });
    }

    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    public Callback getCallback()
    {
        if (mCallback == null)
        {
            mCallback = Callback.DEFAULT;
        }
        return mCallback;
    }

    public Context getContext()
    {
        return mViewGroup.getContext();
    }

    public FTouchHelper getTouchHelper()
    {
        return mTouchHelper;
    }

    public FScroller getScroller()
    {
        if (mScroller == null)
        {
            mScroller = new FScroller(getContext());
        }
        return mScroller;
    }

    public ViewConfiguration getViewConfiguration()
    {
        if (mViewConfiguration == null)
        {
            mViewConfiguration = ViewConfiguration.get(getContext());
        }
        return mViewConfiguration;
    }

    /**
     * 外部调用
     *
     * @param event
     * @return
     */
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (mTouchHelper.isNeedIntercept())
        {
            return true;
        }

        mTouchHelper.processTouchEvent(event);

        boolean intercept = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (getCallback().shouldInterceptTouchEvent(event))
                {
                    intercept = true;
                }
                break;
        }

        if (intercept)
        {
            mTouchHelper.setNeedIntercept(true);
            FTouchHelper.requestDisallowInterceptTouchEvent(mViewGroup, true);
            return true;
        }

        return false;
    }

    /**
     * 外部调用
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        mTouchHelper.processTouchEvent(event);
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 外部调用
     */
    public void computeScroll()
    {
        final int dx = getScroller().getDeltaX();
        final int dy = getScroller().getDeltaY();
        final boolean computeScrollOffset = getScroller().computeScrollOffset();

        if (computeScrollOffset)
        {
            getCallback().onComputeScroll(dx, dy, false);
            mViewGroup.invalidate();
        } else
        {
            getCallback().onComputeScroll(dx, dy, true);
        }
    }

    public interface Callback
    {
        /**
         * 是否要开始手势拦截({@link MotionEvent#ACTION_DOWN}和{@link MotionEvent#ACTION_MOVE}事件触发)
         *
         * @param event
         * @return
         */
        boolean shouldInterceptTouchEvent(MotionEvent event);

        /**
         * 手势按下回调
         *
         * @param event true-消费处理此事件
         * @return
         */
        boolean onGestureDown(MotionEvent event);

        /**
         * 手势点击回调
         *
         * @param event
         * @return
         */
        boolean onGestureSingleTapUp(MotionEvent event);

        /**
         * 手势滚动回调
         *
         * @param event
         * @return
         */
        boolean onGestureScroll(MotionEvent event);

        /**
         * 手势结束回调，收到{@link MotionEvent#ACTION_UP}或者{@link MotionEvent#ACTION_CANCEL}
         *
         * @param event
         * @param velocityX x方向速度
         * @param velocityY y方向速度
         */
        void onGestureFinish(MotionEvent event, float velocityX, float velocityY);

        void onComputeScroll(int dx, int dy, boolean finish);

        Callback DEFAULT = new Callback()
        {
            @Override
            public boolean shouldInterceptTouchEvent(MotionEvent event)
            {
                return false;
            }

            @Override
            public boolean onGestureDown(MotionEvent event)
            {
                return false;
            }

            @Override
            public boolean onGestureSingleTapUp(MotionEvent event)
            {
                return false;
            }

            @Override
            public boolean onGestureScroll(MotionEvent event)
            {
                return false;
            }

            @Override
            public void onGestureFinish(MotionEvent event, float velocityX, float velocityY)
            {

            }

            @Override
            public void onComputeScroll(int dx, int dy, boolean finish)
            {

            }
        };
    }
}
