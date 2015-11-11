package eu.codlab.recyclercolumnadaptable.adapter;

import android.view.View;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public class HeaderItemHolder extends AbstractColumnItemHolder {
    public HeaderItemHolder(View itemView, boolean expanded) {
        super(itemView);

        if (expanded) show();
        else hide();
    }

    public void show() {
        if (itemView.getVisibility() != View.VISIBLE)
            itemView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (itemView.getVisibility() != View.GONE)
            itemView.setVisibility(View.GONE);
    }
}
