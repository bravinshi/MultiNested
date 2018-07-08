package bravin.shi.com.multinested;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import bravin.shi.com.multinested.utils.ScreenUtils;

/**
 * created by bravin on 2018/7/5.
 */
public class MultiNestedParentLayout extends LinearLayout implements
        NestedScrollingParent2, ScrollingView {

    private static final String TAG = "Multi";
    private static final String TAG1 = "Scroll";
    private int screenHeight;

    private int virtualOffsetY = 0;

    private NestedScrollingParentHelper scrollingParentHelper;

    private List<NestedScrollingChild2> nestedChildren = new ArrayList<>();
    private List<ScrollingView> scrollingChildren = new ArrayList<>();

    public MultiNestedParentLayout(Context context) {
        this(context, null);
    }

    public MultiNestedParentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiNestedParentLayout(Context context, @Nullable AttributeSet attrs,
                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenHeight = ScreenUtils.getScreenHeight();
        setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mTotalLength = getPaddingTop() + getPaddingBottom();
        final int count = getChildCount();
        nestedChildren.clear();
        scrollingChildren.clear();
        final int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int tempHeight = parentHeight;
        if (parentHeightMode == MeasureSpec.EXACTLY
                || parentHeightMode == MeasureSpec.UNSPECIFIED) {
            // 循环测量父布局避免可滚动的child大小超过屏幕大小
            boolean hasParent = true;
            while (hasParent) {
                if (tempHeight > 0 && tempHeight <= screenHeight) {
                    hasParent = false;
                } else {
                    ViewParent viewParent = getParent();
                    if (viewParent != null) {
                        View parent = (View) viewParent;
                        tempHeight = parent.getMeasuredHeight();
                    } else {
                        tempHeight = screenHeight;
                        hasParent = false;
                    }
                }
            }
        } else {
            if (tempHeight > screenHeight) {
                tempHeight = screenHeight;
            }
        }
        int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.AT_MOST);

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == null) {
                Log.w(TAG, "child is null at " + i);
                continue;
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            final LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) child.getLayoutParams();
            final LayoutParams newLayoutParams = new LayoutParams(lp);

            if (child instanceof NestedScrollingChild2) {
                nestedChildren.add((NestedScrollingChild2) child);
            }
            if (child instanceof ScrollingView) {
                scrollingChildren.add((ScrollingView) child);
                newLayoutParams.setScrollingView(true);
            }
            child.setLayoutParams(newLayoutParams);
            int tempMS;
            if (newLayoutParams.height > 0) {
                tempMS = MeasureSpec.makeMeasureSpec(newLayoutParams.height, MeasureSpec.EXACTLY);
                child.measure(widthMeasureSpec, tempMS);
            } else {
                child.measure(widthMeasureSpec, childHeightMeasureSpec);
            }
            final int childHeight = child.getMeasuredHeight();
            Log.d(TAG, "childHeight: " + childHeight);

            mTotalLength += childHeight + newLayoutParams.topMargin + newLayoutParams.bottomMargin;
        }
        Log.d(TAG, "mTotalLength1: " + mTotalLength);

        if (parentHeightMode == MeasureSpec.EXACTLY
                || parentHeightMode == MeasureSpec.AT_MOST) {
            if (mTotalLength > parentHeight) {
                mTotalLength = parentHeight;
            }
        }
        Log.d(TAG, "mTotalLength2: " + mTotalLength);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mTotalLength, MeasureSpec.AT_MOST);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target,
                                       int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && canScroll();
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
                                       int axes, int type) {
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy,
                                  @NonNull int[] consumed, int type) {
        // 可以改成支持滑动 这样就成了可以滑动的LinearLayout
        if (scrollingChildren.size() == 0 || dy == 0) {
            return;
        }
        final int scrollRange = getScrollRange();
        // Y轴最大可偏移距离
        int maxOffsetY = scrollRange - getHeight() - getPaddingTop() - getPaddingBottom();
        // 如果不能滑动，return
        if (dy > 0 && (maxOffsetY <= 0 || maxOffsetY == virtualOffsetY)) {
            return;
        }
        if (dy < 0 && virtualOffsetY == 0) {
            return;
        }

        if (dy > 0) {
            // 计算此次滚动的可处理距离
            int canConsumed = Math.min(maxOffsetY - virtualOffsetY, dy);
            handleScrolling2Bottom(canConsumed, 0);
        } else {
            int canConsumed = Math.min(virtualOffsetY, Math.abs(dy));
            handleScrolling2Top(canConsumed, 0);
        }
        // 全部消费 layout全权代理
        consumed[1] = dy;
    }

    private void handleScrolling2Top(int consumed, int skipNum) {
        final int count = getChildCount();
        if (skipNum >= count || consumed <= 0 || count == 0) {
            return;
        }
        for (int i = count - (skipNum + 1); i >= 0; i--) {
            final View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                // If the child is GONE, skip...
                continue;
            }
            final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int topDifference = viewToTopDistance(view);
            if (topDifference < 0) {// 一定有显示不完全的另一个View 直接跳过当前view
                handleScrolling2Top(consumed, count - i);
                break;
            }
            if (topDifference >= consumed) {
                virtualScrollBy(-consumed);// 直接向上滑动相应距离
                return;
            }

            if (!layoutParams.isScrollingView()) {// 不是ScrollingView
                virtualScrollBy(-topDifference);// 向上滑动相应距离
                final int rest = consumed - topDifference;// 剩余要处理距离
                handleScrolling2Top(rest, count - i);
                break;
            } else {// ScrollingView
                virtualScrollBy(-topDifference);// 向上滑动相应距离
                final int rest = consumed - topDifference;// 剩余要处理距离
                if (view.canScrollVertically(-1)) {
                    int canOffset = getViewMaxOffsetDistanceToTop((ScrollingView) view);
                    if (canOffset >= rest) {
                        virtualScrollBy(-rest, (ScrollingView) view);
                        return;
                    } else {
                        virtualScrollBy(-canOffset, (ScrollingView) view);
                        // 继续处理剩余未处理dy : rest - canOffset
                        handleScrolling2Top(rest - canOffset, count - i);
                        break;
                    }
                } else {
                    handleScrolling2Top(rest, count - i);
                    break;
                }
            }
        }
    }

    /**
     * 处理向下滚动
     *
     * @param consumed
     * @param skipNum
     */
    private void handleScrolling2Bottom(int consumed, int skipNum) {
        Log.d(TAG1, "consumed: " + consumed);

        final int count = getChildCount();
        if (skipNum >= count || consumed <= 0 || count == 0) {
            return;
        }
        for (int i = skipNum; i < count; i++) {
            final View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                // If the child is GONE, skip...
                continue;
            }
            final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int bottomDifference = viewToBottomDistance(view);
            if (bottomDifference >= 0 && bottomDifference <= 25) {
                Log.d(TAG1, "bottomDifference = 0");
            }
            if (bottomDifference >= consumed) {
                virtualScrollBy(consumed);
                return;
            }
            if (bottomDifference < 0) {
                // 如果这个child显示已经在bottom最低点之上，直接跳过
                handleScrolling2Bottom(consumed, i + 1);
                break;
            }
            if (!layoutParams.isScrollingView()) {// 不是ScrollingView
                virtualScrollBy(bottomDifference);
                handleScrolling2Bottom(consumed - bottomDifference, i + 1);
                break;
            } else {// ScrollingView
                virtualScrollBy(bottomDifference);// 先移动view 再考虑view内部
                int rest = consumed - bottomDifference;
                if (view.canScrollVertically(1)) {
                    int canOffset = getViewMaxOffsetDistanceToBottom((ScrollingView) view);
                    if (canOffset >= rest) {
                        virtualScrollBy(rest, (ScrollingView) view);
                        return;
                    } else {
                        virtualScrollBy(canOffset, (ScrollingView) view);
                        handleScrolling2Bottom(rest - canOffset, i + 1);
                        break;
                    }
                } else {// 不可滚动时 直接跳过
                    handleScrolling2Bottom(rest, i + 1);
                    break;
                }
            }
        }
    }

    /**
     * 获取ScrollingView当前可向下滚动的最大距离
     *
     * @param view
     * @return
     */
    private int getViewMaxOffsetDistanceToBottom(ScrollingView view) {
        int distance = view.computeVerticalScrollRange() - view.computeVerticalScrollOffset();
        View v = (View) view;
        distance -= v.getHeight() - (v.getPaddingTop() + v.getPaddingBottom());
        return Math.max(distance, 0);
    }

    /**
     * 获取ScrollingView当前可向上滚动的最大距离
     *
     * @param view
     * @return
     */
    private int getViewMaxOffsetDistanceToTop(ScrollingView view) {
        return view.computeVerticalScrollOffset();
    }

    private void virtualScrollBy(int dy) {
        if (dy == 0)
            return;
        scrollBy(0, dy);
        virtualOffsetY += dy;
    }

    /**
     * 虚拟滑动
     *
     * @param dy
     * @param scrollingView
     */
    private void virtualScrollBy(int dy, ScrollingView scrollingView) {
        if (dy == 0)
            return;
        View view = (View) scrollingView;
        view.setNestedScrollingEnabled(false);
        view.scrollBy(0, dy);
        view.setNestedScrollingEnabled(true);
        virtualOffsetY += dy;
    }

    /**
     * 获取view的bottom到layout bottom的距离
     *
     * @param view
     * @return
     */
    private int viewToBottomDistance(View view) {
        final int bottom = getHeight() - getPaddingBottom();
        return view.getBottom() - bottom - getScrollY();
    }

    /**
     * 获取view的top到layout top的距离
     *
     * @param view
     * @return
     */
    private int viewToTopDistance(View view) {
        final int top = getPaddingTop();
        return top - view.getTop() + getScrollY();
    }

    /**
     * 判断是否有ScrollingView显示不完全
     *
     * @return
     */
    private boolean hasScrollViewShowingHalf() {
        return true;
    }

    /**
     * 获取最靠近底部的可以看见的Scrolling View
     *
     * @return
     */
    private View getMostAlignBottomShowingScrollingView() {
        int scrollingViewCount = scrollingChildren.size();
        int top = getHeight() - getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();
        for (int i = 0; i < scrollingViewCount; i++) {
            View view = (View) scrollingChildren.get(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
//            if (view.getTop() > bottom && view.getBottom() < top)
        }
        return null;
    }

    private int getLastViewBottomWithMargin() {
        final int count = getChildCount();
        for (int i = count - 1; i > -1; i--) {
            final View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                // If the child is GONE, skip...
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            return view.getBottom() + layoutParams.bottomMargin;
        }
        return -1;
    }

    private NestedScrollingParentHelper getScrollingParentHelper() {
        if (scrollingParentHelper == null) {
            scrollingParentHelper = new NestedScrollingParentHelper(this);
        }
        return scrollingParentHelper;
    }

    private int scrollRange;

    private int getScrollRange() {
        return scrollRange;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollRange() {

        final int count = getChildCount();
        final int contentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        if (count == 0) {
            return contentHeight;
        }
        int range = 0;
        ViewGroup.MarginLayoutParams layoutParams;
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }
            layoutParams = (MarginLayoutParams) child.getLayoutParams();
            if (child instanceof ScrollingView) {
                range += ((ScrollingView) child).computeVerticalScrollRange()
                        + child.getPaddingTop() + child.getPaddingBottom()
                        + layoutParams.topMargin + layoutParams.bottomMargin;
            } else {
                range += child.getHeight() + layoutParams.topMargin
                        + layoutParams.bottomMargin;
            }
        }
        Log.d(TAG1, "computeVerticalScrollRange: " + range);
        scrollRange = range;
        return range;
    }

    /*
     * 判断layout当前能否在竖直方向滑动
     */
    private boolean canScroll() {
        return getScrollRange() > getHeight()
                - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return virtualOffsetY;
    }

    @Override
    public int computeVerticalScrollExtent() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return 0;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        private boolean isScrollingView = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        public boolean isScrollingView() {
            return isScrollingView;
        }

        public void setScrollingView(boolean scrollingView) {
            isScrollingView = scrollingView;
        }
    }
}
