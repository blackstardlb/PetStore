package nl.blackstardlb.petstore.services;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.User;

public class FirebaseImageManager implements ImageManager {
    private FirebaseStorage firebaseStorage;

    public FirebaseImageManager(FirebaseStorage firebaseStorage) {
        this.firebaseStorage = firebaseStorage;
    }

    @Override
    public Observable<Bitmap> getImage(String path) {
        return null;
    }

    @Override
    public Object getImageLoadSourceForUser(User user) {
        return firebaseStorage.getReference("/profilepictures/" + user.getId() + ".png");
    }

    @Override
    public Completable saveImage(String path, Bitmap image) {
        return RxFirebaseStorage.putBytes(getReference(path), toByteArray(image)).ignoreElement();
    }

    @Override
    public Single<String> saveImage(Bitmap image) {
        UUID uuid = UUID.randomUUID();
        String path = "/pets/" + uuid.toString() + ".png";
        return RxFirebaseStorage.putBytes(getReference(path), toByteArray(image)).map(taskSnapshot -> path);
    }

    @Override
    public Completable deleteImage(String path) {
        return RxFirebaseStorage.delete(getReference(path));
    }

    @Override
    public Object getImageLoadSource(String path) {
        return getReference(path);
    }

    private StorageReference getReference(String path) {
        return firebaseStorage.getReference(path);
    }

    private byte[] toByteArray(Bitmap image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }
}
