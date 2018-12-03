package nl.blackstardlb.petstore.helpers;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.view.MotionEvent;
import android.widget.ImageView;

import javax.annotation.Nonnull;

public class MapsHelper {
    // Disableing Parent Scrolling when map is scrolled
    @SuppressLint("ClickableViewAccessibility")
    public static void fixScrolling(@Nonnull ImageView ivMapTransparent, @Nonnull NestedScrollView svRestoDetail) {
        ivMapTransparent.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    svRestoDetail.requestDisallowInterceptTouchEvent(true);
                    // Disable touch on transparent view
                    return false;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    svRestoDetail.requestDisallowInterceptTouchEvent(false);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    svRestoDetail.requestDisallowInterceptTouchEvent(true);
                    return false;

                default:
                    return true;
            }
        });
    }
}
