package nl.blackstardlb.petstore.helpers;

import android.content.Context;
import android.widget.Toast;

public class ErrorNotifier {
    public static void notifyError(Context context, Throwable throwable) {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
