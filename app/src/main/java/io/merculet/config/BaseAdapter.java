package io.merculet.config;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author sean
 * @Email xiao.lu@magicwindow.cn
 * @Date 18/09/2018 2:18 PM
 * @Version
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private Boolean checkBoxVisibility = false;

    protected ArrayList<T> list = new ArrayList<T>();

    public Context context;

    public ArrayList getData() {
        return list;
    }

    //增加一列数据
    public void addData(List<T> newData) {
        if (newData == null || newData.isEmpty()) return;
        list.addAll(newData);
        notifyItemRangeInserted(list.size() - newData.size(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    //下拉刷新使用,如果null，需要清除list
    public void setData(List<T> data) {
        list.clear();
        if (data == null || data.isEmpty()) return;
        list.addAll(data);
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        int dataSize = list.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(getLayoutId(viewType), parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        onBindViewHolderImpl(holder, position, list.get(position));
    }

    @Override
    public int getItemCount() {
        return !list.isEmpty() ? list.size() : 0;
    }

    public abstract void onBindViewHolderImpl(BaseViewHolder holder, int position, T t);

    public abstract int getLayoutId(int viewType);
}
