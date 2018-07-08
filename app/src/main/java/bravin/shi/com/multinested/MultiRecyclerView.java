package bravin.shi.com.multinested;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MultiRecyclerView extends RecyclerView {
    private final int[] mNestedOffsets = new int[2];
    public MultiRecyclerView(Context context) {
        super(context);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        boolean result = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow,
                type);
        if (offsetInWindow == null) {
            Log.d("MultiRecyclerView", "null");
        }else {
            mNestedOffsets[0] += offsetInWindow[0];
            mNestedOffsets[1] += offsetInWindow[1];
            Log.d("MultiRecyclerView", "offsetInWindow: " + offsetInWindow[0] + " / " + offsetInWindow[1]);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final MotionEvent vtev = MotionEvent.obtain(e);
        final int action = e.getActionMasked();
        final int actionIndex = e.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsets[0] = mNestedOffsets[1] = 0;
        }
//        vtev.offsetLocation(-mNestedOffsets[0], -mNestedOffsets[1]);
//        switch (action) {
//            case MotionEvent.ACTION_MOVE: {
////                if (mNestedOffsets[1] == 0){
////                    vtev.setAction(MotionEvent.ACTION_UP);
////                }
//                dispatchNestedPreScroll(0,13,new int[2],new int[2], ViewCompat.SCROLL_AXIS_VERTICAL);
//                return true;
////                return super.onTouchEvent(vtev);
//            }
//        }

        return super.onTouchEvent(vtev);
    }
}
