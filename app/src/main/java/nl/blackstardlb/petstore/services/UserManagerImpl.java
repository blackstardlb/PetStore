package nl.blackstardlb.petstore.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.util.Arrays;

import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirebaseUser;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import java8.util.Optional;
import nl.blackstardlb.petstore.models.User;

public class UserManagerImpl implements UserManager {
    private FirebaseAuth firebaseAuth;
    private ImageManager imageManager;
    private BehaviorSubject<Optional<User>> userSubject = null;

    public UserManagerImpl(FirebaseAuth firebaseAuth, ImageManager imageManager) {
        this.firebaseAuth = firebaseAuth;
        this.imageManager = imageManager;
    }

    @Override
    public Completable signIn(String emailAddress, String password) {
        return RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, emailAddress, password).ignoreElement();
    }

    @Override
    public Completable signOut() {
        return Completable.fromAction(() -> firebaseAuth.signOut());
    }

    @Override
    public Observable<Optional<User>> getUser() {
        if (userSubject == null) {
            userSubject = BehaviorSubject.create();
            firebaseAuth.addAuthStateListener(firebaseAuth -> {
                Log.d("UserManager", "getUser: changed");
                Optional<FirebaseUser> firebaseUserOptional = Optional.ofNullable(firebaseAuth.getCurrentUser());
                Optional<User> userOptional = toUser(firebaseUserOptional);
                userSubject.onNext(userOptional);
            });
        }
        return userSubject;
    }

    @Override
    public Completable updateUser(String newDisplayName, Bitmap newImage) {
        String userId = firebaseAuth.getUid();
        File imageFile = new File("/profilepictures/" + userId + ".png");
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(newDisplayName);

        Completable imageCompletable;
        if (newImage != null) {
            imageCompletable = imageManager.saveImage(imageFile.getPath(), newImage);
            builder.setPhotoUri(Uri.fromFile(imageFile));
        } else {
            imageCompletable = imageManager.deleteImage(imageFile.getPath());
            builder.setPhotoUri(null);
        }
        UserProfileChangeRequest changeRequest = builder.build();

        Completable profileUpdateCompletable = RxFirebaseUser.updateProfile(firebaseAuth.getCurrentUser(), changeRequest);
        return Completable.merge(Arrays.asList(profileUpdateCompletable, imageCompletable)).andThen(getUser().firstElement().doAfterSuccess(userOptional -> userSubject.onNext(userOptional)))
                .ignoreElement();
    }

    @Override
    public Completable updateDisplayName(String newDisplayName) {
        String userId = firebaseAuth.getUid();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(newDisplayName);

        UserProfileChangeRequest changeRequest = builder.build();

        Completable profileUpdateCompletable = RxFirebaseUser.updateProfile(firebaseAuth.getCurrentUser(), changeRequest);
        return profileUpdateCompletable.andThen(getUser().firstElement().doAfterSuccess(userOptional -> {
            if (userOptional.isPresent()) {
                userOptional.get().setDisplayName(newDisplayName);
            }
            userSubject.onNext(userOptional);
        })).ignoreElement();
    }

    @Override
    public Completable updateProfilePicture(Bitmap newImage) {
        String userId = firebaseAuth.getUid();
        File imageFile = new File("/profilepictures/" + userId + ".png");
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        Completable imageCompletable;
        if (newImage != null) {
            imageCompletable = imageManager.saveImage(imageFile.getPath(), newImage);
            builder.setPhotoUri(Uri.fromFile(imageFile));
        } else {
            imageCompletable = imageManager.deleteImage(imageFile.getPath());
            builder.setPhotoUri(null);
        }
        UserProfileChangeRequest changeRequest = builder.build();

        Completable profileUpdateCompletable = RxFirebaseUser.updateProfile(firebaseAuth.getCurrentUser(), changeRequest);
        return Completable.merge(Arrays.asList(profileUpdateCompletable, imageCompletable))
                .andThen(getUser().firstElement().doAfterSuccess(userOptional -> userSubject.onNext(userOptional)))
                .ignoreElement();
    }

    private Optional<User> toUser(Optional<FirebaseUser> firebaseUserOptional) {
        if (firebaseUserOptional.isEmpty()) return Optional.empty();
        FirebaseUser firebaseUser = firebaseUserOptional.get();
        Uri photoUrl = firebaseUser.getPhotoUrl();
        if (photoUrl != null) {
            Log.d("TEST", "toUser: " + photoUrl);
            Log.d("TEST", "toUser: " + photoUrl.getPath());
            Log.d("TEST", "toUser: " + photoUrl.getLastPathSegment());
        }
        return Optional.of(new User(firebaseUser.getEmail(), firebaseUser.getDisplayName(), firebaseUser.getUid(), null));
    }
}
