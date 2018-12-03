package nl.blackstardlb.petstore.services;

import android.graphics.Bitmap;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.User;

public interface ImageManager {
    Observable<Bitmap> getImage(String path);

    Object getImageLoadSourceForUser(User user);

    Object getImageLoadSource(String path);

    Completable saveImage(String path, Bitmap image);

    Single<String> saveImage(Bitmap image);

    Completable deleteImage(String path);
}
