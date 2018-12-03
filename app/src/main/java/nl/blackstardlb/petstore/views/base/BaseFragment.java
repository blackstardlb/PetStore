package nl.blackstardlb.petstore.views.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import javax.inject.Inject;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.helpers.ErrorNotifier;
import nl.blackstardlb.petstore.views.MainApplication;

public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    protected <T extends ViewModel> T getSharedViewModel(Class<T> aClass) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return ViewModelProviders.of(activity, viewModelFactory).get(aClass);
        }
        return null;
    }

    protected <T extends ViewModel> T getPrivateViewModel(Class<T> aClass) {
        return ViewModelProviders.of(this, viewModelFactory).get(aClass);
    }

    protected void setUnbinder(Unbinder unbinder) {
        this.unbinder = unbinder;
    }

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getApplicationComponent().inject(this);
    }

    public void notifyError(Throwable error) {
        ErrorNotifier.notifyError(getContext(), error);
    }

    public void notifyMessage(String message) {
        // TODO
    }
}
