package nl.blackstardlb.petstore.views.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import nl.blackstardlb.petstore.R;

public abstract class BaseFabFragment extends BaseFragment {
    protected FloatingActionButton rightFab;

    protected FloatingActionButton leftFab;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            rightFab = activity.findViewById(R.id.fab_right);
            leftFab = activity.findViewById(R.id.fab_left);
        }
    }

    protected void showRightFab(@DrawableRes int icon) {
        showFab(icon, rightFab);
    }

    protected void showLeftFab(@DrawableRes int icon) {
        showFab(icon, leftFab);
    }

    private void showFab(@DrawableRes int icon, FloatingActionButton fab) {
        fab.setImageResource(icon);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);
    }

    private void hideFab(FloatingActionButton fab) {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideFab(leftFab);
        hideFab(rightFab);
    }
}
