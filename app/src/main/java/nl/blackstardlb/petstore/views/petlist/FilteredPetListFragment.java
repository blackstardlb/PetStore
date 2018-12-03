package nl.blackstardlb.petstore.views.petlist;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.blackstardlb.petstore.services.PetRepository;
import nl.blackstardlb.petstore.views.base.LoadingDialogFragment;

public class FilteredPetListFragment extends PetListFragment {
    private String filter = "";

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected Disposable getPets(PetRepository petRepository, LoadingDialogFragment loadingDialogFragment, PetListRecyclerViewAdapater adapter) {
        return petRepository.getPetsByType(filter).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pets -> {
            loadingDialogFragment.dismiss();
            adapter.setPets(pets);
            swipeRefreshLayout.setRefreshing(false);
        }, this::notifyError);
    }
}
