package com.scorrer.ravi.scorrer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.RelativeLayout;

public class EventfulRelativeLayout extends RelativeLayout {
    public EventfulRelativeLayout(Context context) {
        super(context);
    }

    public EventfulRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventfulRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EventfulRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent ev){
        boolean r = super.dispatchDragEvent(ev);
        if (r && (ev.getAction() == DragEvent.ACTION_DRAG_STARTED
                || ev.getAction() == DragEvent.ACTION_DRAG_ENDED)){
            onDragEvent(ev);
        }
        return r;
    }
}
