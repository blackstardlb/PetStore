package nl.blackstardlb.petstore.views.petlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.services.PetRepository;
import nl.blackstardlb.petstore.viewmodels.PetViewModel;
import nl.blackstardlb.petstore.views.base.BaseFragment;
import nl.blackstardlb.petstore.views.base.LoadingDialogFragment;

public class PetListFragment extends BaseFragment {
    private PetViewModel petViewModel;
    private LoadingDialogFragment loadingDialogFragment;
    private PetListRecyclerViewAdapater adapter;
    private Disposable petsDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petViewModel = getPrivateViewModel(PetViewModel.class);
    }

    @BindView(R.id.pet_recycler_view)
    protected RecyclerView petRecyclerView;

    @BindView(R.id.swipe_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_list, container, false);
        setUnbinder(ButterKnife.bind(this, view));
        this.adapter = new PetListRecyclerViewAdapater();
        petRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        petRecyclerView.setAdapter(adapter);
        this.loadingDialogFragment = LoadingDialogFragment.startFor(getFragmentManager(), null);
        loadPets();
        swipeRefreshLayout.setOnRefreshListener(this::loadPets);
        return view;
    }

    protected void loadPets() {
        if (petsDisposable != null && !petsDisposable.isDisposed()) {
            petsDisposable.dispose();
            loadingDialogFragment.dismiss();
        }

        petsDisposable = getPets(petViewModel.getPetRepository(), loadingDialogFragment, adapter);
        Disposable disposable = petsDisposable;
        addDisposable(disposable);
    }

    public void remove(Pet pet) {
        adapter.remove(pet);
    }

    protected Disposable getPets(PetRepository petRepository, LoadingDialogFragment loadingDialogFragment, PetListRecyclerViewAdapater adapter) {
        return petRepository.getPets().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pets -> {
            loadingDialogFragment.dismiss();
            adapter.setPets(pets);
            swipeRefreshLayout.setRefreshing(false);
        }, this::notifyError);
    }
}
