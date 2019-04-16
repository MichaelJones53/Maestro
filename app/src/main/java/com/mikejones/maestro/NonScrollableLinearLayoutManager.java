package com.mikejones.maestro;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class NonScrollableLinearLayoutManager extends LinearLayoutManager {
    public NonScrollableLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
