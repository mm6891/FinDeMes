package globalsolutions.findemes.pantallas.util;

import android.content.Context;
import android.widget.Toast;

import globalsolutions.findemes.R;

/**
 * Created by manuel.molero on 17/04/2015.
 */
public class Util {
    public static void showToast(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public static int [] prgmImagesOption={R.drawable.edit,R.drawable.delete};
}
