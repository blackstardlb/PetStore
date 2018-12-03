package nl.blackstardlb.petstore.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java8.util.Optional;
import nl.blackstardlb.petstore.R;
import nl.blackstardlb.petstore.models.User;
import nl.blackstardlb.petstore.viewmodels.UserProfileViewModel;
import nl.blackstardlb.petstore.views.base.BaseFragment;

import static android.support.constraint.Constraints.TAG;

public class UserProfileFragment extends BaseFragment {
    private UserProfileViewModel mViewModel;

    @BindView(R.id.email_address)
    protected TextView emailAddress;

    @BindView(R.id.display_name)
    protected TextView displayName;

    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;
    private Optional<User> userOptional;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = getPrivateViewModel(UserProfileViewModel.class);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            LoginActivity.start(getActivity());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Unbinder unbinder = ButterKnife.bind(this, view);
        setUnbinder(unbinder);

        addDisposable(
                mViewModel.getUser().subscribe(this::onUserChanged, this::notifyError)
        );
    }

    private void onUserChanged(Optional<User> userOptional) {
        Log.d(TAG, "onUserChanged: Hello");
        this.userOptional = userOptional;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            emailAddress.setText(user.getEmail());
            displayName.setText(user.getDisplayName());

            Disposable disposable = Completable.fromAction(() -> {
                while (getActivity() == null) {
                    Thread.sleep(1000);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(() -> {
                Glide.with(this)
                        .load(mViewModel.getImageLoadSourceForUser(user))
                        .apply(
                                RequestOptions.noAnimation().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .placeholder(R.drawable.ic_account_circle_black_80dp).error(R.drawable.ic_account_circle_black_80dp)
                        )
                        .into(profileImage);
            }, this::notifyError);
            addDisposable(disposable);
        }
    }

    @OnClick(R.id.profile_menu)
    protected void onMenuButtonClicked(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "onOptionsItemSelected: clicked");
            switch (item.getItemId()) {
                case R.id.action_change_image:
                    onChangeImageClicked();
                    return true;
                case R.id.action_change_name:
                    onChangeDisplayNameClicked();
                    return true;
                case R.id.action_logout:
                    onLogoutClicked();
                    return true;

            }
            return false;
        });
        popup.show();
    }

    private void onChangeImageClicked() {
        ProfileImageDialogFragment.startFor(getFragmentManager());
    }

    private void onChangeDisplayNameClicked() {
        UserNameDialogFragment.startFor(getFragmentManager());
    }

    private void onLogoutClicked() {
        Context context = getContext();
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("YES", (dialog, which) -> {
                Completable logout = mViewModel.logout();
                addDisposable(logout.subscribe(() -> LoginActivity.start(getActivity())));
            });
            builder.setTitle("Logout?");
            builder.setMessage("Are you sure you want to log out of " + this.userOptional.get().getEmail() + "?");
            builder.setNegativeButton("CANCEL", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        }
    }
}
