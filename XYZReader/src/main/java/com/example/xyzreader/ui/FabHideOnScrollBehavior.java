package com.example.xyzreader.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class FabHideOnScrollBehavior extends FloatingActionButton.Behavior {

    public FabHideOnScrollBehavior() {
        super();
    }

    public FabHideOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionButton child, @NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        //child -> Floating Action Button
        if (child.getVisibility() == View.VISIBLE && dyConsumed > 0) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                /**
                 * Called when a FloatingActionButton has been hidden
                 *
                 * @param fab the FloatingActionButton that was hidden.
                 */
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        }
        else if (child.getVisibility() == View.GONE && dyUnconsumed == 0) {
            child.show();
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);

        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}