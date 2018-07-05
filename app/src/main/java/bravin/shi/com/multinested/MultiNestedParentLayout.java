package bravin.shi.com.multinested;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import bravin.shi.com.multinested.utils.ScreenUtils;
import bravin.shi.com.multinested.utils.SizeUtils;

/**
 * created by bravin on 2018/7/5.
 */
public class MultiNestedParentLayout extends LinearLayout implements
        NestedScrollingParent2 {

    private static final String TAG = "Multi";
    private int screenHeight;

    private List<NestedScrollingChild2> nestedChildren = new ArrayList<>();

    public MultiNestedParentLayout(Context context) {
        this(context, null);
    }

    public MultiNestedParentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiNestedParentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenHeight = ScreenUtils.getScreenHeight();
        setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mTotalLength = getPaddingTop() + getPaddingBottom();
        final int count = getChildCount();
        Log.d(TAG, "children count: " + count);
        nestedChildren.clear();
        final int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < count;i++){
            View child = getChildAt(i);
            if (child == null) {
                Log.w(TAG, "child is null at " + i);
                continue;
            }
            if (child.getVisibility() == GONE){
                continue;
            }
            if (child instanceof NestedScrollingChild2) {
                nestedChildren.add((NestedScrollingChild2) child);
            }
            int tempHeight = parentHeight;
            if (parentHeightMode == MeasureSpec.EXACTLY
                    || parentHeightMode == MeasureSpec.UNSPECIFIED) {
                // 循环测量父布局避免可滚动的child大小超过屏幕大小
                boolean hasParent = true;
                while (hasParent){
                    if (tempHeight > 0 && tempHeight <= screenHeight){
                        hasParent = false;
                    }else {
                        ViewParent viewParent = getParent();
                        if (viewParent != null){
                            View parent = (View) viewParent;
                            tempHeight = parent.getMeasuredHeight();
                        }else {
                            tempHeight = screenHeight;
                            hasParent = false;
                        }
                    }
                }
            }else {
                if (tempHeight > screenHeight){
                    tempHeight = screenHeight;
                }
            }
            int childHeightMeasureSpec =
                    MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.AT_MOST);
            child.measure(widthMeasureSpec, childHeightMeasureSpec);
            final int childHeight = child.getMeasuredHeight();
            Log.d(TAG, "childHeight: " + childHeight);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            mTotalLength += childHeight + lp.topMargin + lp.bottomMargin;
        }

        if (parentHeightMode == MeasureSpec.EXACTLY
                || parentHeightMode == MeasureSpec.AT_MOST){
            if (mTotalLength > parentHeight) {
                mTotalLength = parentHeight;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mTotalLength, MeasureSpec.AT_MOST);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return false;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

    }
}
