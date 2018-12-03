package nl.blackstardlb.petstore.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.viewmodels.PetStoreViewModel;
import nl.blackstardlb.petstore.views.base.BaseFabFragment;
import nl.blackstardlb.petstore.views.base.LoadingDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPetStoreFragment extends BaseFabFragment {
    @BindView(R.id.et_name)
    protected TextInputEditText name;

    @BindView(R.id.til_name)
    protected TextInputLayout nameTil;

    @BindView(R.id.et_address)
    protected TextInputEditText address;

    @BindView(R.id.til_address)
    protected TextInputLayout addressTil;

    private PetStoreViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getSharedViewModel(PetStoreViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_pet_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        Disposable disposable = viewModel.getPetStore().firstElement().subscribe(petStore -> {
            Log.d("TEST", "onViewCreated: " + petStore);
            name.setText(petStore.getName());
            address.setText(petStore.getAddress());
        });
        addDisposable(disposable);

        disposable = viewModel.isValid().subscribe(rightFab::setEnabled);
        addDisposable(disposable);

        disposable = viewModel.nameError().subscribe(error -> ViewHelpers.linkErrorToTextInputLayout(error, nameTil));
        addDisposable(disposable);

        disposable = viewModel.addressError().subscribe(error -> ViewHelpers.linkErrorToTextInputLayout(error, addressTil));
        addDisposable(disposable);

        showLeftFab(R.drawable.ic_restore_black_24dp);
        showRightFab(R.drawable.ic_save_black_24dp);

        rightFab.setOnClickListener(v -> onSaveClicked());
        leftFab.setOnClickListener(v -> reset());
    }


    @OnTextChanged(R.id.et_address)
    protected void onAddressChanged(CharSequence sequence) {
        viewModel.setAddress(sequence.toString());
    }

    @OnTextChanged(R.id.et_name)
    protected void onNameChanged(CharSequence sequence) {
        viewModel.setName(sequence.toString());
    }

    protected void onSaveClicked() {
        LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.startFor(getFragmentManager(), null);
        Disposable disposable = viewModel.save().subscribe(() -> {
            Toast.makeText(getContext(), "Save Successful!", Toast.LENGTH_LONG).show();
            loadingDialogFragment.dismiss();
            reset();
        }, error -> {
            loadingDialogFragment.dismiss();
            notifyError(error);
        });
        addDisposable(disposable);

        LoadingDialogFragment.OnDialogDismissedListener onDialogDismissedListener = () -> {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        };
        loadingDialogFragment.setOnDialogDismissedListener(onDialogDismissedListener);
    }

    protected void reset() {
        viewModel.reset();
        name.setText("");
        address.setText("");
    }
}
