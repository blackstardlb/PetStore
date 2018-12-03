package nl.blackstardlb.petstore.views;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lucasurbas.listitemview.ListItemView;
import com.synnapps.carouselview.CarouselView;

import java.util.List;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.helpers.MapsHelper;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.PetStore;
import nl.blackstardlb.petstore.viewmodels.MainActivityViewModel;
import nl.blackstardlb.petstore.viewmodels.PetDetailsViewModel;
import nl.blackstardlb.petstore.views.base.BaseFullScreenDialogFragment;
import nl.blackstardlb.petstore.views.petlist.PetListFragment;

public class PetDetailsFragment extends BaseFullScreenDialogFragment implements OnMapReadyCallback {
    private static final String PET_ID_KEY = "PET_ID_KEY";
    private PetDetailsViewModel viewModel;

    @BindView(R.id.progressBar1)
    protected ContentLoadingProgressBar contentLoadingProgressBar;

    @BindView(R.id.liv_name)
    protected ListItemView name;

    @BindView(R.id.liv_animal_type)
    protected ListItemView type;

    @BindView(R.id.liv_description)
    protected ListItemView description;

    @BindView(R.id.liv_pet_store)
    protected ListItemView petStore;

    @BindView(R.id.liv_pet_store_address)
    protected ListItemView address;

    @BindView(R.id.carouselView)
    protected CarouselView carouselView;

    @BindView(R.id.main_toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.iv_map_helper)
    protected ImageView imageView;

    @BindView(R.id.scroll_view)
    protected NestedScrollView scrollView;

    @BindView(R.id.fl_map_holder)
    protected FrameLayout mapHolder;

    private Pet pet;
    private MainActivityViewModel mainActivityViewModel;
    private GoogleMap googleMap;

    public static void startFor(@Nullable FragmentManager fragmentManager, @Nonnull String petId) {
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            DialogFragment newFragment = new PetDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PET_ID_KEY, petId);
            Log.d("PET_ID", "PET ID: " + petId);
            newFragment.setArguments(bundle);
            newFragment.show(ft, "PET_DETAILS");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getPrivateViewModel(PetDetailsViewModel.class);
        mainActivityViewModel = getSharedViewModel(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pet_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
            MapsHelper.fixScrolling(imageView, scrollView);
        }
        toolbar.setNavigationOnClickListener(v -> dismiss());

        Bundle arguments = getArguments();
        Context context = getContext();
        if (arguments != null && context != null) {
            String petId = arguments.getString(PET_ID_KEY);
            Disposable disposable = viewModel.getPet(petId).subscribe(pet -> {
                this.pet = pet;
                contentLoadingProgressBar.hide();
                FrameLayout parent = (FrameLayout) contentLoadingProgressBar.getParent();
                toolbar.setTitle(pet.getName());

                parent.setVisibility(View.GONE);
                name.setSubtitle(pet.getName());
                type.setSubtitle(pet.getAnimalType());
                description.setSubtitle(pet.getDescription());
                PetStore petStore = pet.getPetStore();
                this.petStore.setSubtitle(petStore.getName());
                address.setSubtitle(petStore.getAddress());

                List<String> images = pet.getImages();

                if (images.isEmpty()) {
                    carouselView.setImageListener((position, imageView) -> {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(context).load(R.drawable.no_image).into(imageView);
                    });
                    carouselView.setPageCount(1);
                } else {
                    carouselView.setImageListener((position, imageView) -> {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(context).load(viewModel.getImageSource(images.get(position))).into(imageView);
                    });
                    carouselView.setPageCount(images.size());
                }

                Geocoder geocoder = new Geocoder(context);
                Disposable disposable1 = Single.fromCallable(() -> geocoder.getFromLocationName(petStore.getAddress(), 1))
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(addresses -> {
                            if (!addresses.isEmpty() && googleMap != null) {
                                Address address = addresses.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                googleMap.addMarker(new MarkerOptions().position(latLng));
                                mapHolder.setVisibility(View.VISIBLE);
                            }
                        }, this::showError);
                addDisposable(disposable1);
            }, this::showError);
            addDisposable(disposable);
        }
    }

    @OnClick(R.id.fab_delete)
    protected void onDeleteClicked() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setPositiveButton("YES", (dialog, which) -> {
            Disposable disposable = viewModel.delete(pet).subscribe(() -> {
                dialog.dismiss();
                this.dismiss();
                Toast.makeText(getContext(), "Deleted " + pet.getName() + ".", Toast.LENGTH_LONG).show();

                String currentTag = mainActivityViewModel.getCurrentTag();
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(currentTag);
                    Log.d("TEST", "onDeleteClicked: " + fragment);
                    if (fragment instanceof PetListFragment) {
                        PetListFragment petListFragment = (PetListFragment) fragment;
                        petListFragment.remove(pet);
                    }
                }
            }, this::showError);
            addDisposable(disposable);
        });
        alertDialog.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alertDialog.setTitle("Delete?");
        alertDialog.setMessage("Are you sure you want to delete " + pet.getName() + " forever?");
        alertDialog.create().show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            SupportMapFragment fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
            if (fragment != null) {
                getFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
