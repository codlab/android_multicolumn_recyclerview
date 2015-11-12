package eu.codlab.recyclercolumnadaptable.inflater;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import eu.codlab.recyclercolumnadaptable.adapter.ColumnItemHolder;
import eu.codlab.recyclercolumnadaptable.item.ContentItem;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public interface AbstractItemInflater<T extends ColumnItemHolder> {
    /**
     * Called from the array adapter with the ColumnItemHolder to create
     *
     * @param parent the parent view
     * @return an instance of ColumnItemHolder with the proper data default data binded
     */
    @NonNull
    T onCreateViewHolder(@NonNull ViewGroup parent);

    /**
     * Set the data for a displayed / managed holder containing an AbstractItem
     * <p/>
     * Each holder will provide an **AbstractItem** which contains getPosition()
     * the getPosition() >= 0 will give a value into the provider's list of real items
     * it is safe to call a get on the provider's internal list of items from this value
     * <p/>
     * for now, returning -1 means that the object was unintialized
     *
     * @param holder the holder to manage
     */
    void onBindViewHolder(@NonNull T holder);

    /**
     * The number of items
     *
     * @return the number of item managed by the provider
     */
    int getItemCount();

    /**
     * Retrieve a specific item at a given position
     * <p/>
     * The implementation must create a ContentItem with at least the usage of ths
     * ContentItem(int position) constructor
     *
     * @param position the position greater or equals than 0
     * @return the corresponding item or null if invalid position
     */
    @NonNull
    ContentItem getContentItemAt(int position);

    /**
     * Set the provider with a specific header in the column
     *
     * @return true if the provider will manage the creation of an header view
     */
    boolean hasHeader();

    /**
     * Create the header view requested by the main component
     *
     * @param parent a non-null parent
     * @return
     */
    @NonNull
    View getHeader(@NonNull ViewGroup parent);

    /**
     * Set the provider with a specific footer in the column
     *
     * @return true if the provider will manage the creation of a footer view
     */
    boolean hasFooter();

    /**
     * Create the header view requested by the main component
     *
     * @param parent a non-null parent
     * @return
     */
    @NonNull
    View getFooter(@NonNull ViewGroup parent);
}
