package globalsolutions.findemes.pantallas.util;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import globalsolutions.findemes.R;

/**
 * Created by manuel.molero on 17/04/2015.
 */
public class Util {
    public static void showToast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public static int[] prgmImagesOption = {R.drawable.edit, R.drawable.delete};

    /**
     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile - FileInputStream for the file to copy from.
     * @param toFile   - FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    /*public static long dias(String fechaActivacion) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaActivacion));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - cal.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        return days;
    }*/

    //devuelve positivo si f2 es mayor a f1
    public int compare(String f1, String f2) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return f.parse(f2).compareTo(f.parse(f1));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String sumaDias (String fecha, int numDias){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(fecha));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, numDias); // Adding numDias days
        String output = sdf.format(c.getTime());
        return output;
    }
}
