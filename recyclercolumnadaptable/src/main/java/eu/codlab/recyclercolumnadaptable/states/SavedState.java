package eu.codlab.recyclercolumnadaptable.states;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by kevinleperf on 16/11/2015.
 */
public class SavedState extends View.BaseSavedState {

    private Boolean is_showing_content = null;

    public SavedState(Parcelable superState, boolean is_showing_content) {
        super(superState);
        this.is_showing_content = is_showing_content;
    }

    private SavedState(Parcel in) {
        super(in);
        is_showing_content = in.readInt() == 1;
    }

    public Boolean isShowingContent() {
        return is_showing_content;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        super.writeToParcel(destination, flags);
        destination.writeInt(is_showing_content ? 1 : 0);
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
