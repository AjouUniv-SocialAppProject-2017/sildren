package media.socialapp.sildren.utilities;

import android.os.Environment;

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String DOWNLOADS = ROOT_DIR + "/DCIM/downloads";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}
