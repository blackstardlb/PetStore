package nl.blackstardlb.petstore.viewmodels;

import android.graphics.Bitmap;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.UserManager;

public class ProfileImageViewModel extends BaseViewModel {
    private UserManager userManager;
    private ImageManager imageManager;

    private BehaviorSubject<Bitmap> bitmapBehaviorSubject = BehaviorSubject.create();
    private boolean hasBitmap = false;
    private Bitmap bitmap;

    @Inject
    public ProfileImageViewModel(UserManager userManager, ImageManager imageManager) {
        this.userManager = userManager;
        this.imageManager = imageManager;
    }

    public void setBitmap(@Nonnull Bitmap bitmap) {
        hasBitmap = true;
        this.bitmap = bitmap;
        bitmapBehaviorSubject.onNext(bitmap);
    }

    public Observable<Bitmap> getBitmap() {
        if (hasBitmap) {
            bitmapBehaviorSubject.onNext(bitmap);
        }
        return bitmapBehaviorSubject;
    }

    public boolean hasBitMap() {
        return hasBitmap;
    }

    public Completable update() {
        return userManager.updateProfilePicture(bitmapBehaviorSubject.getValue());
    }

    public Observable<Object> getUserImageSource() {
        return userManager.getUser().map(userOptional -> imageManager.getImageLoadSourceForUser(userOptional.get()));
    }

    public void clear() {
        bitmapBehaviorSubject = BehaviorSubject.create();
    }
}
