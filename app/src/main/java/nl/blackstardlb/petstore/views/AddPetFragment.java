package nl.blackstardlb.petstore.views;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.PetStore;
import nl.blackstardlb.petstore.viewmodels.AddPetViewModel;
import nl.blackstardlb.petstore.views.base.BaseFabFragment;
import nl.blackstardlb.petstore.views.base.LoadingDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPetFragment extends BaseFabFragment {
    private static final int REQUEST_IMAGE_CAPTURE = 333;
    private static final int REQUEST_GET_SINGLE_FILE = 444;
    private AddPetViewModel viewModel;

    @BindView(R.id.pet_store_spinner)
    protected AppCompatSpinner spinner;

    @BindView(R.id.et_name)
    protected TextInputEditText nameEt;

    @BindView(R.id.et_animal_type)
    protected TextInputEditText animalTypeEt;

    @BindView(R.id.et_description)
    protected TextInputEditText descriptionEt;

    @BindView(R.id.images)
    protected RecyclerView recyclerView;

    private ArrayAdapter<String> dataAdapter;
    private PetImagesRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getSharedViewModel(AddPetViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        viewModel.loadPetStores();

        showLeftFab(R.drawable.ic_restore_black_24dp);
        showRightFab(R.drawable.ic_save_black_24dp);

        rightFab.setOnClickListener(v -> onSaveClicked());
        leftFab.setOnClickListener(v -> onResetClicked());

        Context context = getContext();
        if (context != null) {
            this.dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<>());
            spinner.setAdapter(dataAdapter);

            Disposable disposable = viewModel.getPetstores().subscribe(petStores -> {
                List<String> names = StreamSupport.stream(petStores).map(PetStore::getName).collect(Collectors.toList());
                if (dataAdapter != null) {
                    dataAdapter.clear();
                    dataAdapter.addAll(names);
                }
            }, this::notifyError);
            addDisposable(disposable);

            Pet pet = viewModel.getPet();

            spinner.setSelection(viewModel.getSelectedIndex());
            animalTypeEt.setText(pet.getAnimalType());
            descriptionEt.setText(pet.getDescription());
            nameEt.setText(pet.getName());

            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

            adapter = new PetImagesRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);

            adapter.addBitmaps(viewModel.getBitmaps());
            Disposable disposable1 = viewModel.observeBitmaps().subscribe(adapter::addBitmap);
            addDisposable(disposable1);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    viewModel.setSelectedIndex(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @OnTextChanged(R.id.et_description)
    protected void onDescriptionTextChanged(CharSequence sequence) {
        viewModel.setPetDescription(sequence.toString());
    }

    @OnTextChanged(R.id.et_animal_type)
    protected void onAnimalTypeChanged(CharSequence sequence) {
        viewModel.setPetAnimalType(sequence.toString());
    }

    @OnTextChanged(R.id.et_name)
    protected void oNameChanged(CharSequence sequence) {
        viewModel.setPetName(sequence.toString());
    }


    protected void onSaveClicked() {
        LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.startFor(getFragmentManager(), null);
        Disposable disposable = viewModel.save().subscribe(pet -> {
            loadingDialogFragment.dismiss();
            onResetClicked();
            Toast.makeText(getContext(), "Pet saved successfully!", Toast.LENGTH_LONG).show();
        }, this::notifyError);
        addDisposable(disposable);
        loadingDialogFragment.setOnDialogDismissedListener(disposable::dispose);
    }

    protected void onResetClicked() {
        viewModel.reset();
        nameEt.setText("");
        descriptionEt.setText("");
        animalTypeEt.setText("");
        spinner.setSelection(0);
        adapter.clear();
    }

    @OnClick(R.id.open_camera)
    protected void onUseCameraClicked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable disposable = rxPermissions.request(Manifest.permission.CAMERA).subscribe(granted -> {
            if (granted) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        return;
                    }
                }
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Can't take picture without permission", Toast.LENGTH_LONG).show();
            }
        });
        addDisposable(disposable);
    }

    @OnClick(R.id.open_file)
    protected void onUseFileClicked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable disposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
            } else {
                Toast.makeText(getContext(), "Can't chose picture without permission", Toast.LENGTH_LONG).show();
            }
        });
        addDisposable(disposable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            viewModel.addBitmap(imageBitmap);
                        }
                    }
                case REQUEST_GET_SINGLE_FILE:
                    final Uri imageUri = data.getData();
                    final InputStream imageStream;
                    if (imageUri != null) {
                        try {
                            imageStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            viewModel.addBitmap(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
