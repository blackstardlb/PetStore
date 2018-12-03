package nl.blackstardlb.petstore.viewmodels;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import java8.util.Optional;
import nl.blackstardlb.petstore.models.User;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.UserManager;

public class UserProfileViewModel extends BaseViewModel {
    private UserManager userManager;
    private ImageManager imageManager;

    @Inject
    public UserProfileViewModel(UserManager userManager, ImageManager imageManager) {
        this.userManager = userManager;
        this.imageManager = imageManager;
    }

    public Observable<Optional<User>> getUser() {
        return userManager.getUser();
    }

    public Object getImageLoadSourceForUser(User user) {
        return imageManager.getImageLoadSourceForUser(user);
    }

    public Completable logout() {
        return userManager.signOut();
    }
}
