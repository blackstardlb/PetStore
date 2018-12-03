package nl.blackstardlb.petstore.views;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.blackstardlb.petstore.services.PetRepository;
import nl.blackstardlb.petstore.views.base.LoadingDialogFragment;
import nl.blackstardlb.petstore.views.petlist.PetListFragment;
import nl.blackstardlb.petstore.views.petlist.PetListRecyclerViewAdapater;

public class FavorietenListFragment extends PetListFragment {

    private FavorietViewModel favorietViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favorietViewModel = getPrivateViewModel(FavorietViewModel.class);
    }

    @Override
    protected Disposable getPets(PetRepository petRepository, LoadingDialogFragment loadingDialogFragment, PetListRecyclerViewAdapater adapter) {
        return favorietViewModel.getFavorieten().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pets -> {
            loadingDialogFragment.dismiss();
            adapter.setPets(pets);
            swipeRefreshLayout.setRefreshing(false);
        }, this::notifyError);
    }
}
