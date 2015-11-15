package eu.codlab.recyclercolumnadaptable.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.recyclercolumnadaptable.R;
import eu.codlab.recyclercolumnadaptable.RecyclerColumnsWithContentView;
import eu.codlab.recyclercolumnadaptable.adapter.AbstractColumnItemHolder;
import eu.codlab.recyclercolumnadaptable.adapter.ColumnItemHolder;
import eu.codlab.recyclercolumnadaptable.adapter.EmptyItemHolder;
import eu.codlab.recyclercolumnadaptable.adapter.HeaderItemHolder;
import eu.codlab.recyclercolumnadaptable.inflater.AbstractItemInflater;
import eu.codlab.recyclercolumnadaptable.item.AbstractItem;
import eu.codlab.recyclercolumnadaptable.item.EmptyItem;
import eu.codlab.recyclercolumnadaptable.item.HeaderItem;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public class MainArrayAdapter extends RecyclerView.Adapter<AbstractColumnItemHolder> {
    private final static int ITEM_EMPTY = 0;
    private final static int ITEM_CONTENT = 1;
    private final static int ITEM_HEADER = 2;

    public final static MainArrayAdapter instantiate(AbstractItemInflater provider, RecyclerColumnsWithContentView grid) {
        return new MainArrayAdapter(provider, grid.getColumns(), grid.getColumnsExpandedVisible());
    }

    private AbstractItemInflater _provider;

    private List<AbstractItem> _content;
    private boolean _is_expanded;
    private int _column_count;
    private int _column_left;

    public MainArrayAdapter(AbstractItemInflater provider, int column_count,
                            int column_left) {
        _provider = provider;
        _is_expanded = false;

        _column_count = column_count;
        _column_left = column_left;

        _content = new ArrayList<>();
        if (_provider.hasHeader()) _content.add(new HeaderItem());
        for (int i = 0; i < _provider.getItemCount(); i++) {
            _content.add(_provider.getContentItemAt(i));
        }
        expand();
    }

    public AbstractColumnItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_HEADER)
            return new HeaderItemHolder(_provider.getHeader(parent), isExpanded());
        if (viewType == ITEM_CONTENT) {
            return _provider.onCreateViewHolder(parent);
        }

        return new EmptyItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_empty, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (_content.get(position) instanceof HeaderItem)
            return ITEM_HEADER;
        if (_content.get(position) instanceof EmptyItem)
            return ITEM_EMPTY;
        return ITEM_CONTENT;
    }

    @Override
    public void onBindViewHolder(AbstractColumnItemHolder holder, int position) {
        if (holder instanceof HeaderItemHolder) {
            HeaderItemHolder header = (HeaderItemHolder) holder;
        }

        if (holder instanceof ColumnItemHolder) {
            ColumnItemHolder current_holder = (ColumnItemHolder) holder;
            current_holder.setRealPosition(position);
            holder.onBindViewHolder(_content.get(position));
            _provider.onBindViewHolder((ColumnItemHolder) holder);
        }
    }

    @Override
    public int getItemCount() {
        return _content.size();
    }

    public AbstractItem getItemAt(int i) {
        return _content.get(i);
    }

    public void expand() {
        if (!_is_expanded) {
            unsetEmptyContent();
            _is_expanded = true;
        }
    }

    public void collapse() {
        if (_is_expanded) {
            setEmptyContent();

            _is_expanded = false;
        }
    }

    public boolean isExpanded() {
        return _is_expanded;
    }

    private void setEmptyContent() {
        int column_at_right = _column_count - _column_left;
        boolean has_fetched_item = true;

        ArrayList<Changed> changed = new ArrayList<>();
        List<AbstractItem> items = new ArrayList<>();

        int index_cursor = 0;
        int original_index = 0;

        if (_provider.hasHeader() && _content.size() > 0) {
            items.add(_content.get(0));
            original_index++;
            index_cursor++;
        }

        while (has_fetched_item) {
            has_fetched_item = false;

            for (int count = 0, i = original_index
                 ; count < _column_left && i < _content.size()
                    ; count++, i++) {
                items.add(_content.get(i).updatePosition(index_cursor));
                has_fetched_item = true;
                original_index++;
                index_cursor++;
            }

            if (has_fetched_item) {
                int index = items.size();
                for (int i = 0; i < column_at_right; i++) {
                    items.add(new EmptyItem());
                    index_cursor++;
                }
                changed.add(new Changed(index, column_at_right));
            }
        }
        _content = items;

        for (Changed change : changed) {
            notifyItemRangeInserted(change.start, change.count);
        }

        if (_provider.hasHeader()) {
            _content.remove(0);
            notifyItemRemoved(0);
        }
    }

    private void unsetEmptyContent() {
        ArrayList<Changed> changed = new ArrayList<>();

        int index_cursor = 0;
        //if (_provider.hasHeader()) i++;
        Changed change = null;
        AbstractItem tmp;
        while (index_cursor < _content.size()) {
            tmp = _content.get(index_cursor);
            if (tmp instanceof EmptyItem) {
                if (change == null) change = new Changed(index_cursor, 0);
                _content.remove(index_cursor);
                change.count++;
            } else {
                tmp.updatePosition(index_cursor);
                if (change != null) {
                    changed.add(change);
                    change = null;
                }
            }
            index_cursor++;
        }

        if (change != null) changed.add(change);

        for (Changed remove : changed) notifyItemRangeRemoved(remove.start, remove.count);
        if (_provider.hasHeader()
                && (_content.size() == 0 || !(_content.get(0) instanceof HeaderItem))) {
            _content.add(0, new HeaderItem());
            notifyItemInserted(0);
        }
    }

    /**
     * Represents a range of modification
     */
    private class Changed {
        public int start;
        public int count;

        public Changed(int start, int count) {
            this.start = start;
            this.count = count;
        }
    }
}
