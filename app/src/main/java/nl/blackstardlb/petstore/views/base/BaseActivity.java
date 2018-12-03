package nl.blackstardlb.petstore.views.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.views.MainApplication;

public abstract class BaseActivity extends AppCompatActivity {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getApplicationComponent().inject(this);
        unbinder = ButterKnife.bind(this);
    }

    protected final void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        compositeDisposable.dispose();
    }


    protected <T extends ViewModel> T getViewModel(Class<T> aClass) {
        return ViewModelProviders.of(this, viewModelFactory).get(aClass);
    }

    public void doOnUnauthenticated() {
        // TODO
    }

    public void notifyError(Throwable error) {
        // TODO
    }

    public void notifyMessage(String message) {
        // TODO
    }
}
