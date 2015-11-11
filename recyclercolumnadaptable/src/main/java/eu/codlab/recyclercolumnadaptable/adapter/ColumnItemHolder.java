package eu.codlab.recyclercolumnadaptable.adapter;

import android.view.View;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public class ColumnItemHolder extends AbstractColumnItemHolder {
    public int _position;

    public ColumnItemHolder(View itemView) {
        super(itemView);
    }

    public void setRealPosition(int position) {
        _position = position;
    }

    public int getRealPosition() {
        return _position;
    }
}
