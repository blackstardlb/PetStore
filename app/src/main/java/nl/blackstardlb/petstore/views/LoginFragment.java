package nl.blackstardlb.petstore.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.services.UserManager;
import nl.blackstardlb.petstore.viewmodels.LoginViewModel;
import nl.blackstardlb.petstore.views.base.BaseFragment;

public class LoginFragment extends BaseFragment {
    @BindView(R.id.password)
    EditText passwordEditText;

    @BindView(R.id.email_address)
    EditText emailAddressEditText;

    @Inject
    UserManager userManager;

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getApplicationComponent().inject(this);
        loginViewModel = getPrivateViewModel(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setUnbinder(ButterKnife.bind(this, view));
        addDisposable(loginViewModel.getLoginModelBehaviorSubject().observeOn(AndroidSchedulers.mainThread()).subscribe(this::updateViews));
        return view;
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        addDisposable(userManager
                .signIn(emailAddressEditText.getText().toString(), passwordEditText.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::quitActivity, this::notifyError));
    }

    private void quitActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            MainActivity.start(activity);
            activity.finish();
        }
    }

    private void updateViews(LoginViewModel.LoginModel loginModel) {
        passwordEditText.setText(loginModel.getPassword());
        emailAddressEditText.setText(loginModel.getEmail());
    }
}
