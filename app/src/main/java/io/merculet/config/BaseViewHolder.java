package io.merculet.config;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views;
    private View itemView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.views = new SparseArray<>();
        this.itemView = itemView;
    }

    public <T extends View> T getView(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }
}