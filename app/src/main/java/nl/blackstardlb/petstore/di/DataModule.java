package nl.blackstardlb.petstore.di;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.blackstardlb.petstore.services.FirebaseImageManager;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.PetRepository;
import nl.blackstardlb.petstore.services.PetRepositoryImpl;
import nl.blackstardlb.petstore.services.PetStoreRepository;
import nl.blackstardlb.petstore.services.PetStoreRepositoryImpl;

@Module
public class DataModule {
    @Singleton
    @Provides
    FirebaseFirestore providesFirebaseFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }

    @Singleton
    @Provides
    PetRepository providesPetRepository(FirebaseFirestore firebaseFirestore, PetStoreRepository petStoreRepository) {
        return new PetRepositoryImpl(firebaseFirestore, petStoreRepository);
    }

    @Singleton
    @Provides
    PetStoreRepository providesPetStoreRepository(FirebaseFirestore firebaseFirestore) {
        return new PetStoreRepositoryImpl(firebaseFirestore);
    }

    @Singleton
    @Provides
    ImageManager providesImageManager(FirebaseStorage firebaseStorage) {
        return new FirebaseImageManager(firebaseStorage);
    }

    @Singleton
    @Provides
    FirebaseStorage providesFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }
}
