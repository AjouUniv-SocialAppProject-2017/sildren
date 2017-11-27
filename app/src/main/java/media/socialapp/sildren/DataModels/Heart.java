package media.socialapp.sildren.DataModels;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Heart {
    private static final String TAG = "Heart";

    public ImageView heartWhite, heartRed;

    public Heart(ImageView heartWhite, ImageView heartRed) {
        this.heartWhite = heartWhite;
        this.heartRed = heartRed;
    }

    public void toggleLike() {
        Log.d(TAG, "toggleLike: toggling heart.");

        if (heartRed.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "toggleLike: toggling red heart off.");

            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);
        } else if (heartRed.getVisibility() == View.GONE) {
            Log.d(TAG, "toggleLike: toggling red heart on.");

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);
        }
    }
}

















