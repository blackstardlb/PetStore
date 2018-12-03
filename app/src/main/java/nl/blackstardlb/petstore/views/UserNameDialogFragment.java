package nl.blackstardlb.petstore.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.viewmodels.UserNameViewModel;
import nl.blackstardlb.petstore.views.base.BaseFullScreenDialogFragment;

public class UserNameDialogFragment extends BaseFullScreenDialogFragment {

    private UserNameViewModel viewModel;

    @BindView(R.id.til_new_user_name)
    protected TextInputLayout textInputLayout;

    @BindView(R.id.et_new_user_name)
    protected TextInputEditText textInputEditText;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.btn_save)
    protected Button save;

    public static void startFor(@Nullable FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            DialogFragment newFragment = new UserNameDialogFragment();
            newFragment.show(ft, "USER_NAME");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getPrivateViewModel(UserNameViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_user_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        Disposable disposable = viewModel.getDefaultUserName().subscribe(userName -> textInputEditText.setText(userName), this::showError);
        addDisposable(disposable);

        Disposable disposable1 = viewModel.getUserName().firstElement().subscribe(userName -> textInputEditText.setText(userName));
        addDisposable(disposable1);

        Disposable disposable2 = viewModel.getErrorMessage().subscribe(error -> {
            textInputLayout.setErrorEnabled(error.isPresent());
            save.setEnabled(!error.isPresent());
            if (error.isPresent()) {
                textInputLayout.setError(error.get());
            }
        });
        addDisposable(disposable2);

        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        toolbar.setTitle("Change display name");
    }

    @OnTextChanged(R.id.et_new_user_name)
    protected void onNewUserNameChanged(CharSequence sequence) {
        viewModel.setUserName(sequence.toString());
    }

    @OnClick(R.id.btn_save)
    protected void onSaveClicked() {
        Disposable disposable = viewModel.update().subscribe(this::dismiss, this::showError);
        addDisposable(disposable);
    }

    @OnClick(R.id.btn_cancel)
    protected void onCancelClicked() {
        dismiss();
    }
}
