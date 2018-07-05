package bravin.shi.com.multinested;

import android.support.v4.view.NestedScrollingChild2;

/**
 * created by bravin on 2018/7/5.
 */
public interface MultiNestedScrollChild extends NestedScrollingChild2 {

    boolean canScroll2Top();

    boolean canScroll2Bottom();

    boolean preFling();
}
