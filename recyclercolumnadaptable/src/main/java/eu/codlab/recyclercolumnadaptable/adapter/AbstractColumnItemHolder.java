package eu.codlab.recyclercolumnadaptable.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import eu.codlab.recyclercolumnadaptable.item.AbstractItem;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public abstract class AbstractColumnItemHolder extends RecyclerView.ViewHolder {
    private AbstractItem _item;

    public AbstractColumnItemHolder(View itemView) {
        super(itemView);
    }

    public void onBindViewHolder(AbstractItem item) {
        _item = item;
    }

    public AbstractItem getItem() {
        return _item;
    }
}
