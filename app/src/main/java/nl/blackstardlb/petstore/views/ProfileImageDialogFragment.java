package nl.blackstardlb.petstore.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.viewmodels.ProfileImageViewModel;
import nl.blackstardlb.petstore.views.base.BaseFullScreenDialogFragment;

public class ProfileImageDialogFragment extends BaseFullScreenDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 333;
    private static final int REQUEST_GET_SINGLE_FILE = 222;
    private ProfileImageViewModel viewModel;

    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;

    @BindView(R.id.btn_save)
    protected Button saveBtn;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    public static void startFor(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            DialogFragment newFragment = new ProfileImageDialogFragment();
            newFragment.show(ft, "profile_image");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getPrivateViewModel(ProfileImageViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_profile_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        Disposable disposable = Single.just(viewModel.hasBitMap()).flatMapMaybe(hasBitMap -> {
            Log.d("IsEmpty", "onCreate: " + hasBitMap);
            if (hasBitMap) {
                return Maybe.never();
            } else {
                return viewModel.getUserImageSource().firstElement();
            }
        }).subscribe(src -> {
            Glide.with(this)
                    .asBitmap()
                    .load(src)
                    .apply(RequestOptions.noAnimation().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            viewModel.setBitmap(resource);
                        }
                    });
        });
        addDisposable(disposable);

        toolbar.setTitle("Change profile picture");
        toolbar.setNavigationOnClickListener(v -> dismiss());
        disposable = viewModel.getBitmap().subscribe(bitmap -> {
            saveBtn.setEnabled(true);
            profileImage.setImageBitmap(bitmap);
        });
        addDisposable(disposable);
    }

    @OnClick(R.id.btn_save)
    protected void OnSaveClicked() {
        Disposable disposable = viewModel.update().subscribe(this::dismiss, this::showError);
        addDisposable(disposable);
    }

    @OnClick(R.id.btn_cancel)
    protected void onCancelClicked() {
        dismiss();
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
                            viewModel.setBitmap(imageBitmap);
                        }
                    }
                case REQUEST_GET_SINGLE_FILE:
                    final Uri imageUri = data.getData();
                    final InputStream imageStream;
                    if (imageUri != null) {
                        try {
                            imageStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            viewModel.setBitmap(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        viewModel.clear();
        super.onDestroyView();
    }

}
