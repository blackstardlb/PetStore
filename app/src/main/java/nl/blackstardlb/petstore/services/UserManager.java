package nl.blackstardlb.petstore.services;

import android.graphics.Bitmap;

import io.reactivex.Completable;
import io.reactivex.Observable;
import java8.util.Optional;
import nl.blackstardlb.petstore.models.User;

public interface UserManager {
    Completable signIn(String emailAddress, String password);

    Completable signOut();

    Observable<Optional<User>> getUser();

    Completable updateUser(String newDisplayName, Bitmap newImage);

    Completable updateDisplayName(String newDisplayName);

    Completable updateProfilePicture(Bitmap newImage);
}
