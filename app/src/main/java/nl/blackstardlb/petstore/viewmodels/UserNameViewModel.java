package nl.blackstardlb.petstore.viewmodels;


import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import java8.util.Optional;
import nl.blackstardlb.petstore.services.UserManager;

public class UserNameViewModel extends BaseViewModel {
    private BehaviorSubject<String> userName = BehaviorSubject.create();
    private UserManager userManager;

    @Inject
    public UserNameViewModel(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setUserName(String userName) {
        this.userName.onNext(userName);
    }

    public Observable<String> getUserName() {
        return userName;
    }

    public Observable<Optional<String>> getErrorMessage() {
        return userName.map(userName -> {
            String message = null;
            if (userName.length() < 5) {
                message = "Your user name must be at least 5 chars long";
            } else if (userName.length() > 50) {
                message = "You user name can't be longer than 50 characters";
            }
            return Optional.ofNullable(message);
        });
    }

    public Single<String> getDefaultUserName() {
        return userManager.getUser().firstOrError().map(userOptional -> userOptional.get().getDisplayName());
    }

    public Completable update() {
        return userManager.updateDisplayName(userName.getValue());
    }
}
