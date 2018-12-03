package nl.blackstardlb.petstore.views.base;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import nl.blackstardlb.petstore.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingDialogFragment extends DialogFragment {
    private OnDialogDismissedListener onDialogDismissedListener;

    public void setOnDialogDismissedListener(OnDialogDismissedListener onDialogDismissedListener) {
        this.onDialogDismissedListener = onDialogDismissedListener;
    }

    public static LoadingDialogFragment startFor(@Nullable FragmentManager fragmentManager, @Nullable OnDialogDismissedListener onDialogDismissedListener) {
        LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            loadingDialogFragment.setOnDialogDismissedListener(onDialogDismissedListener);
            loadingDialogFragment.show(ft, "loading");
        }
        return loadingDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Loading");
            builder.setNegativeButton("Cancel", (dialog, which) -> this.dismiss());
            builder.setOnDismissListener(dialog -> {
                if (onDialogDismissedListener != null) {
                    this.onDialogDismissedListener.dismiss();
                }
            });
            builder.setView(R.layout.dialog_fragment_loading);
            return builder.show();
        }
        return super.onCreateDialog(savedInstanceState);
    }

    public interface OnDialogDismissedListener {
        void dismiss();
    }
}
