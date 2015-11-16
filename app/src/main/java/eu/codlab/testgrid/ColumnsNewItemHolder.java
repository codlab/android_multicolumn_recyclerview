package eu.codlab.testgrid;

import android.view.View;
import android.widget.TextView;

import eu.codlab.recyclercolumnadaptable.adapter.ColumnItemHolder;

/**
 * Created by kevinleperf on 11/11/2015.
 */
public class ColumnsNewItemHolder extends ColumnItemHolder {
    public TextView example_content;

    public ColumnsNewItemHolder(View itemView) {
        super(itemView);

        example_content = (TextView) itemView.findViewById(R.id.test_content);
    }
}
