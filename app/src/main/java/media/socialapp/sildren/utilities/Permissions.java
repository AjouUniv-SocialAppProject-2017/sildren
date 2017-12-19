package media.socialapp.sildren.utilities;

import android.Manifest;

import media.socialapp.sildren.MainActivity;


public class Permissions {

    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA
    };

    public static final String[] WRITE_STORAGE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] READ_STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final String[] ACCESS_COARSE_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final String[] ACCESS_FINE_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
}
