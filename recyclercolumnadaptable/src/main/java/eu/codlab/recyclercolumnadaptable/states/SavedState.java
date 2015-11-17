package eu.codlab.recyclercolumnadaptable.states;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by kevinleperf on 16/11/2015.
 */
public class SavedState extends View.BaseSavedState {

    private Boolean is_showing_content = null;
    private int position_in_provider;

    public SavedState(Parcelable superState, boolean is_showing_content, int position_in_provider) {
        super(superState);
        this.is_showing_content = is_showing_content;
        this.position_in_provider = position_in_provider;
    }

    private SavedState(Parcel in) {
        super(in);
        is_showing_content = in.readInt() == 1;
        position_in_provider = in.readInt();
    }

    public Boolean isShowingContent() {
        return is_showing_content;
    }

    public int getPositionInProvider() {
        return position_in_provider;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        super.writeToParcel(destination, flags);
        destination.writeInt(is_showing_content ? 1 : 0);
        destination.writeInt(position_in_provider);
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
