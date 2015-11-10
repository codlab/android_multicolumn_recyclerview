package eu.codlab.recyclercolumnadaptable.inflater;

import android.view.ViewGroup;

import eu.codlab.recyclercolumnadaptable.adapter.ColumnItemHolder;
import eu.codlab.recyclercolumnadaptable.item.ContentItem;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public interface AbstractItemInflater {
    ColumnItemHolder onCreateViewHolder(ViewGroup parent);

    void onBindViewHolder(ColumnItemHolder holder);

    int getItemCount();

    ContentItem getContentItemAt(int position);
}
