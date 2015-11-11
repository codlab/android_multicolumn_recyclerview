package eu.codlab.recyclercolumnadaptable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import eu.codlab.recyclercolumnadaptable.item.AbstractItem;
import eu.codlab.recyclercolumnadaptable.item.HeaderItem;
import eu.codlab.recyclercolumnadaptable.view.MainArrayAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public class RecyclerColumnsWithContentView extends FrameLayout {
    private final static int MINIMUM_COLUMNS_COUNT = 3;
    private final static int MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT = 1;

    private int _columns_left = 0;
    private int _columns = 0;
    private RecyclerView _recycler;
    private ViewGroup _left;
    private ViewGroup _content;
    private IRecyclerColumnsListener _listener;

    private void init(AttributeSet attrs) {

        LayoutInflater.from(getContext())
                .inflate(R.layout.view_recycler_columns, this, true);

        _recycler = (RecyclerView) findViewById(R.id.recycler);
        _left = (ViewGroup) findViewById(R.id.left);
        _content = (ViewGroup) findViewById(R.id.content);

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RecyclerColumnsWithContentView);
            if (array != null) {
                _columns = array.getInteger(R.styleable.RecyclerColumnsWithContentView_columns,
                        MINIMUM_COLUMNS_COUNT);
                _columns_left = array.getInteger(R.styleable.RecyclerColumnsWithContentView_columnsVisibleExpanded,
                        MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT);
                _columns = Math.max(_columns, MINIMUM_COLUMNS_COUNT);
                setColumnsExpandedVisible(Math.max(_columns_left, MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT));
            }
        }

        //init the recyclerview with the correct amount of columns
        GridLayoutManager grid_manager = new GridLayoutManager(getContext(), _columns);
        grid_manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 && hasHeader() ? getColumns() : 1;
            }
        });
        _recycler.setLayoutManager(grid_manager);
        _recycler.setItemAnimator(new LandingAnimator(new OvershootInterpolator(1f)));
    }

    public RecyclerColumnsWithContentView(Context context) {
        super(context);
        init(null);
    }

    public RecyclerColumnsWithContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RecyclerColumnsWithContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecyclerColumnsWithContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Set the number of columns in the view
     *
     * @param number_columns
     */
    public void setColumns(int number_columns) {
        _columns = number_columns;
    }

    public int getColumns() {
        return _columns;
    }

    /**
     * Show the number of columns that will be visible on left when the content is expanded
     *
     * @param number_columns_visible
     */
    public void setColumnsExpandedVisible(int number_columns_visible) {
        _columns_left = number_columns_visible;
        int columns_right = _columns - _columns_left;

        ((LinearLayout.LayoutParams) _left.getLayoutParams()).weight = _columns_left;
        ((LinearLayout.LayoutParams) _content.getLayoutParams()).weight = columns_right;
    }

    public int getColumnsExpandedVisible() {
        return _columns_left;
    }

    public void setRecyclerAdapter(MainArrayAdapter adapter) {
        _recycler.setAdapter(adapter);
        checkResume();
    }

    public void setRecyclerColumnsListener(IRecyclerColumnsListener listener) {
        _listener = listener;
        checkResume();
    }

    public void showContent() {
        if (_recycler.getAdapter() != null) {
            ((MainArrayAdapter) _recycler.getAdapter())
                    .collapse();
            if (_listener != null) _listener.onShowContent(_content);
        }
    }

    public void hideContent() {
        if (_recycler.getAdapter() != null) {
            ((MainArrayAdapter) _recycler.getAdapter())
                    .expand();
            if (_listener != null) _listener.onHideContent(_content);
        }
    }

    public boolean isShowingContent() {
        return isShowingContent(false);
    }

    public boolean isShowingContent(boolean with_temp) {
        if (with_temp && temporary_to_store_is_showing_content != null)
            return temporary_to_store_is_showing_content;
        return hasValidAdapter() && !((MainArrayAdapter) _recycler.getAdapter())
                .isExpanded();
    }

    private boolean hasHeader() {
        if (hasValidAdapter()) {
            MainArrayAdapter adapter = (MainArrayAdapter) _recycler.getAdapter();
            AbstractItem item = adapter.getItemAt(0);
            return item != null && item instanceof HeaderItem;
        }
        return false;
    }

    private boolean hasValidAdapter() {
        return _recycler != null && _recycler.getAdapter() != null
                && _recycler.getAdapter() instanceof MainArrayAdapter;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * SAVE / RESTORE INSTANCE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, isShowingContent(false));
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            temporary_to_store_is_showing_content = savedState.isShowingContent();

            checkResume();
        }
    }

    public void checkResume() {
        if (_recycler != null && _recycler.getAdapter() != null && _listener != null) {
            if (temporary_to_store_is_showing_content != null) {
                if (temporary_to_store_is_showing_content) {
                    showContent();
                } else {
                    hideContent();
                }
            }
        }
    }

    private Boolean temporary_to_store_is_showing_content = null;

    protected static class SavedState extends BaseSavedState {

        private Boolean is_showing_content = null;

        private SavedState(Parcelable superState, boolean is_showing_content) {
            super(superState);
            this.is_showing_content = is_showing_content;
        }

        private SavedState(Parcel in) {
            super(in);
            is_showing_content = in.readInt() == 1;
        }

        public Boolean isShowingContent() {
            return is_showing_content;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(is_showing_content ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

    }
}
