package eu.codlab.recyclercolumnadaptable;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.recyclercolumnadaptable.inflater.AbstractItemInflater;
import eu.codlab.recyclercolumnadaptable.item.AbstractItem;
import eu.codlab.recyclercolumnadaptable.item.HeaderItem;
import eu.codlab.recyclercolumnadaptable.manager.GridLayoutSmoothManager;
import eu.codlab.recyclercolumnadaptable.states.SavedState;
import eu.codlab.recyclercolumnadaptable.view.MainArrayAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public class RecyclerColumnsWithContentView extends FrameLayout implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private final static long DELAY_INVALIDATE_DECORATIONS = 250;
    private final static long DELAY_SET_SCROLL_POSITION = 300;
    private final static int MINIMUM_COLUMNS_COUNT = 3;
    private final static int MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT = 1;

    private int _columns_left = 0;
    private int _columns = 0;
    private RecyclerView _recycler;
    private ViewGroup _left;
    private ViewGroup _content;
    private IRecyclerColumnsListener _listener;
    private List<RecyclerView.ItemDecoration> _tmp_decorations = new ArrayList<>();

    /* * * * * * * * * * * *
     *
     * * * * * * * * * * * */
    private LinearLayout _footer_parent;
    private LinearLayout _footer;
    private LinearLayout _footer_content;
    private int _position_in_provider;
    private AbstractItemInflater _provider;


    private void init(AttributeSet attrs) {

        LayoutInflater.from(getContext())
                .inflate(R.layout.view_recycler_columns, this, true);

        _recycler = (RecyclerView) findViewById(R.id.recycler);
        _left = (ViewGroup) findViewById(R.id.left);
        _content = (ViewGroup) findViewById(R.id.content);

        _footer = (LinearLayout) findViewById(R.id.footer);
        _footer_parent = (LinearLayout) findViewById(R.id.footer_parent);
        _footer_content = (LinearLayout) findViewById(R.id.content_footer);

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RecyclerColumnsWithContentView);
            if (array != null) {
                _columns = array.getInteger(R.styleable.RecyclerColumnsWithContentView_columns,
                        MINIMUM_COLUMNS_COUNT);
                _columns_left = array.getInteger(R.styleable.RecyclerColumnsWithContentView_columnsVisibleExpanded,
                        MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT);
                _columns = Math.max(_columns, MINIMUM_COLUMNS_COUNT);
                setColumnsExpandedVisible(Math.max(_columns_left, MINIMUM_COLUMNS_WHILE_BEING_EXPANDED_COUNT));

                array.recycle();
            }
        }

        //init the recyclerview with the correct amount of columns
        GridLayoutSmoothManager grid_manager = new GridLayoutSmoothManager(getContext(), _columns);
        grid_manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 && hasHeader() ? getColumns() : 1;
            }
        });
        _recycler.setLayoutManager(grid_manager);
        _recycler.setItemAnimator(new LandingAnimator(new OvershootInterpolator(1f)));


        _recycler.setOnTouchListener(this);
        _position_in_provider = -1;
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

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        _tmp_decorations.add(decoration);
        _recycler.addItemDecoration(decoration);
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

    public void setRecyclerAdapter(AbstractItemInflater provider) {
        _provider = provider;
        _recycler.setAdapter(MainArrayAdapter.instantiate(provider, this));
        if (provider.hasFooter()) {
            _footer.removeAllViews();
            _footer.addView(provider.getFooter(_footer));
            _footer.requestLayout();
        }
        checkResume();
    }

    public void setRecyclerColumnsListener(IRecyclerColumnsListener listener) {
        _listener = listener;
        checkResume();
    }

    public boolean showContent(final int position_in_item) {
        boolean shown_content = showContent();
        _position_in_provider = position_in_item;
        if (shown_content) {
            updateScrollPositionToSelected();
            //_position_to_save = _position_in_provider;
            setSelectedItemIndex(-1);
        }
        return shown_content;
    }

    public boolean showContent() {
        boolean shown_content = showContentInternally();
        if (shown_content) {
            updateScrollPositionToSelected();
            //_position_to_save = _position_in_provider;
            setSelectedItemIndex(-1);
        }
        return shown_content;
    }

    private boolean showContentInternally() {
        boolean changed_state = false;
        if (_recycler.getAdapter() != null) {
            if (((MainArrayAdapter) _recycler.getAdapter()).isExpanded()) {
                changed_state = true;
                ((MainArrayAdapter) _recycler.getAdapter())
                        .collapse();

                int footer_new_width = _footer_parent.getWidth() * _columns_left / _columns;
                updateFooter(_footer_parent.getWidth(), footer_new_width);
            }
            invalidateDecorations();
            if (_listener != null) _listener.onShowContent(_content);
        }
        return changed_state;
    }

    public boolean hideContent() {
        boolean changed_state = false;
        if (_recycler.getAdapter() != null) {
            if (!((MainArrayAdapter) _recycler.getAdapter()).isExpanded()) {
                changed_state = true;
                ((MainArrayAdapter) _recycler.getAdapter())
                        .expand();

                updateFooter(_footer.getWidth(), _footer_parent.getWidth());

                updateScrollPositionToSelected();
                //setSelectedItemIndex(-1);
            }
            invalidateDecorations();
            if (_listener != null) _listener.onHideContent(_content);
        }
        return changed_state;
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

    private void setSelectedItemIndex(int position_in_provider) {
        _position_in_provider = position_in_provider;
    }

    private int getSelectedItemIndex() {
        return _position_in_provider;
    }

    private void updateScrollPositionToSelected() {
        if (getSelectedItemIndex() >= 0
                && _provider != null
                && getSelectedItemIndex() < _provider.getItemCount()) {
            final int position = ((MainArrayAdapter) _recycler.getAdapter())
                    .transformInArrayToPositionInRecycler(getSelectedItemIndex());
            View child = _recycler.getChildAt(0);

            if (child == null || !_provider.useAnimation()) {
                _recycler.scrollToPosition(position);
                return;
            }

            final int width = child.getWidth();
            final int height = child.getHeight();

            Handler handler = getHandler();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollToPosition(position, width, height);
                    }
                }, DELAY_SET_SCROLL_POSITION);
            } else {
                _recycler.scrollToPosition(position);
            }
        }
    }

    private void smoothScrollToPosition(int position, int width, int height) {
        int pos = ((GridLayoutSmoothManager) _recycler.getLayoutManager()).findFirstVisibleItemPosition();
        int delta = (position - pos) / getColumns();
        //View firstChild = _recycler.getChildAt(getSelectedItemIndex());
        Log.d("RecyclerColumnsWithContentView", "delta := " + delta + " " + position + " " + pos + " ");
        //Log.d("RecyclerColumnsWithContentView", "delta := " + delta + " " + position + " " + pos + " " + (firstChild.getHeight() * delta));
        if (height > 0) _recycler.smoothScrollBy(0, height * delta);

        //if (_recycler != null) _recycler.smoothScrollToPosition(position);
    }

    private void invalidateFooter() {
        if (_provider != null && _provider.hasFooter()) {
            if (((MainArrayAdapter) _recycler.getAdapter()).isExpanded()) {
                updateFooter(_footer.getWidth(), _footer_parent.getWidth());
            } else {
                int footer_new_width = _footer_parent.getWidth() * _columns_left / _columns;
                updateFooter(_footer_parent.getWidth(), footer_new_width);
            }

            if (Build.VERSION.SDK_INT >= 16) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    private void updateFooter(long from, long to) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                _footer.getLayoutParams().width = ((Float) animation.getAnimatedValue())
                        .intValue();
                _footer.requestLayout();

                _footer_content.getLayoutParams().width = _footer_parent.getWidth()
                        - _footer.getLayoutParams().width;
                _footer_content.requestLayout();
            }
        });
        animator.start();
    }


    @Override
    public void onGlobalLayout() {
        invalidateFooter();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * SAVE / RESTORE INSTANCE
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, isShowingContent(false), getSelectedItemIndex());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            temporary_to_store_is_showing_content = savedState.isShowingContent();
            setSelectedItemIndex(savedState.getPositionInProvider());

            checkResume();

            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    public void checkResume() {
        if (_recycler != null && _recycler.getAdapter() != null && _listener != null) {
            if (temporary_to_store_is_showing_content != null) {
                if (temporary_to_store_is_showing_content) {
                    if (getSelectedItemIndex() >= 0) {
                        showContent(getSelectedItemIndex());
                    } else {
                        showContent();
                    }
                } else {
                    hideContent();
                }
            }
            invalidateFooter();
        }
    }

    private Boolean temporary_to_store_is_showing_content = null;

    private void invalidateDecorations() {
        try {
            //For now, no other solution to the item decoration issue !
            if (_tmp_decorations.size() > 0) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (RecyclerView.ItemDecoration decoration : _tmp_decorations) {
                                _recycler.removeItemDecoration(decoration);
                                _recycler.addItemDecoration(decoration);
                            }
                            _recycler.postInvalidate();
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, DELAY_INVALIDATE_DECORATIONS);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setSelectedItemIndex(-1);
        return false;
    }
}
