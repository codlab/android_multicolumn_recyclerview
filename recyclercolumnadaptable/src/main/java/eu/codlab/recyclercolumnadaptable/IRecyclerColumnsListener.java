package eu.codlab.recyclercolumnadaptable;

import android.view.ViewGroup;

/**
 * Created by kevinleperf on 10/11/2015.
 */
public interface IRecyclerColumnsListener {
    void onHideContent(ViewGroup content);
    void onShowContent(ViewGroup content);
}
