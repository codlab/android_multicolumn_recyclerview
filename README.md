# android_multicolumn_recyclerview
A simple multi column recyclerview supporting column count modification


# use with Gradle

Install the dependency into your project using
```gradle
dependencies {
  compile 'eu.codlab:android_multicolumn_adaptable:1.6'
}
```

# Declare in your layout

```xml
    <eu.codlab.recyclercolumnadaptable.RecyclerColumnsWithContentView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/grid"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:columns="5"
        app:columnsVisibleExpanded="3"/>
```

Customize the number of columns shown in expanded mode with:
```
app:columns="integer"
```
Default value : 3

And the number of columns displayed when the content is expanded

```
app:columnsVisibleExpanded="integer"
```
Default value : 1

# Usage

- register a listener for events
```jav
mGrid.setRecyclerColumnsListener(IRecyclerColumnsListener listener)
```

- Set the adapter simply by calling
```java
mGrid.setRecyclerAdapter(AbstractItemInflater inflater)
```

- expand the content using the expand method
```java
mGrid.showContent();
```

- collapse the content using the collapse method
```java
mGrid.hideContent();
```

- set a custom ItemDecoration to the grid using
```java
mGrid.addItemDecoration(ItemDecoration item_decoration)
```

# Declare an inflater

To create a new Adapter for the grid, you must instantiate a **MainArrayAdapter** object.
The instantiation method helper takes an **AbstractItemInflater** interface instance and a parent RecyclerColumnsWithContentView

The interface signature is declared as :
```java
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
     * Create the header view requested by the ain component
     *
     * @param parent a non-null parent
     * @return
     */
    @NonNull
    View getFooter(@NonNull ViewGroup parent);
}
```

# TODO list

- make the header and footer state (always visible or invisible when the content is visible/invisible)
- customize the animation