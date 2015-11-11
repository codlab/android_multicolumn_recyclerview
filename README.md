# android_multicolumn_recyclerview
A simple multi column recyclerview supporting column count modification


# use with Gradle

Install the dependency into your project using
```gradle
dependencies {
  compile 'eu.codlab:android_multicolumn_adaptable:1.+'
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

# Declare an inflater

To create a new Adapter for the grid, you must instantiate a **MainArrayAdapter** object.
The instantiation method helper takes an **AbstractItemInflater** interface instance and a parent RecyclerColumnsWithContentView

The interface signature is declared as :
```java
public interface AbstractItemInflater {
    /**
     * Called from the array adapter with the ColumnItemHolder to create
     *
     * @param parent the parent view
     * @return an instance of ColumnItemHolder with the proper data default data binded
     */
    ColumnItemHolder onCreateViewHolder(ViewGroup parent);

    /**
     * Set the data for a displayed / managed holder containing an AbstractItem
     *
     * @param holder the holder to manage
     */
    void onBindViewHolder(ColumnItemHolder holder);

    /**
     * The number of items
     *
     * @return the number of item managed by the provider
     */
    int getItemCount();

    /**
     * Retrieve a specific item at a given position
     *
     * @param position the position greater or equals than 0
     * @return the corresponding item or null if invalid position
     */
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
    View getHeader(ViewGroup parent);
}
```
