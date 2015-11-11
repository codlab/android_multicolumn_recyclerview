package eu.codlab.recyclercolumnadaptable.item;

/**
 * Created by kevinleperf on 09/11/2015.
 */
public abstract class AbstractItem {

    private int _position;

    public AbstractItem() {
        setPosition(-1);
    }

    public AbstractItem(int position) {
        this();

        setPosition(position);
    }

    public void setPosition(int position) {
        _position = position;
    }

    public long getPosition() {
        return _position;
    }

}
