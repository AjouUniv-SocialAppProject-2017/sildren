package media.socialapp.sildren;

import android.*;

/**
 * Created by andong-won on 2017. 11. 13..
 */

public class Permissions {

    public static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    public static final String[] CAMERA_PERMISSION = {
            android.Manifest.permission.CAMERA
    };

    public static final String[] WRITE_STORAGE_PERMISSION = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] READ_STORAGE_PERMISSION = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
}
