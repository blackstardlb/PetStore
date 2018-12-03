package nl.blackstardlb.petstore.views.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import javax.inject.Inject;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.di.viewmodels.ViewModelFactory;
import nl.blackstardlb.petstore.helpers.ErrorNotifier;
import nl.blackstardlb.petstore.views.MainApplication;

public abstract class BaseDialogFragment extends DialogFragment {
    @Inject
    protected ViewModelFactory viewModelFactory;

    private Unbinder unbinder;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    protected final void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected final void setUnbinder(Unbinder unbinder) {
        this.unbinder = unbinder;
    }

    /**
     * Get a view model that is bound to the lifecycle of this fragment.
     *
     * @param modelClass the view model class
     * @param <T>        the view model type
     * @return view model of type T
     */
    protected <T extends ViewModel> T getPrivateViewModel(Class<T> modelClass) {
        return ViewModelProviders.of(this, viewModelFactory).get(modelClass);
    }

    /**
     * Get a view model that is bound to the lifecycle of the activity this fragment is mounted to
     * and can be shared with other fragments.
     *
     * @param modelClass the view model class
     * @param <T>        the view model type
     * @return view model of type T
     */
    protected <T extends ViewModel> T getSharedViewModel(Class<T> modelClass) {
        //noinspection ConstantConditions
        return ViewModelProviders.of(getActivity(), viewModelFactory).get(modelClass);
    }

    protected void showError(@Nullable Throwable throwable) {
        ErrorNotifier.notifyError(getContext(), throwable);
    }

    protected void showError(@Nullable String error) {
    }

    protected void showMessage(@Nullable String message) {
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        compositeDisposable = new CompositeDisposable();
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getApplicationComponent().inject(this);
    }
}
