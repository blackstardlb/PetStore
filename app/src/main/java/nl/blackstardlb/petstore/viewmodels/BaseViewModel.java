package nl.blackstardlb.petstore.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected final void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
        Log.d("Cleared", "onCleared: ViewModelCleared " + this.getClass().getSimpleName());
    }
}
