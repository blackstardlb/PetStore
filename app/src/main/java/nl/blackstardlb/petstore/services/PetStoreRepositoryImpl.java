package nl.blackstardlb.petstore.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.Completable;
import io.reactivex.Single;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.models.PetStore;

public class PetStoreRepositoryImpl implements PetStoreRepository {
    private FirebaseFirestore firestore;

    public PetStoreRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Single<List<PetStore>> getPetStores() {
        return RxFirestore.getCollection(getCollectionRef())
                .map(this::toPetStores)
                .toSingle();
    }

    @Override
    public Single<PetStore> getPetStore(String id) {
        return RxFirestore.getDocument(getDocumentRef(id))
                .map(this::toPetStore)
                .toSingle();
    }

    @Override
    public Single<PetStore> setPetStore(PetStore petStore) {
        if (petStore.getId() != null) {
            DocumentReference document = getDocumentRef(petStore.getId());
            return RxFirestore.setDocument(document, petStore).andThen(Single.just(petStore));
        }
        return RxFirestore.addDocument(getCollectionRef(), petStore).map(documentReference -> {
            petStore.setId(documentReference.getId());
            return petStore;
        });
    }

    @Override
    public Completable deletePetStore(String id) {
        return RxFirestore.deleteDocument(getDocumentRef(id));
    }

    private CollectionReference getCollectionRef() {
        return firestore.collection("petstores");
    }

    private DocumentReference getDocumentRef(String id) {
        return getCollectionRef().document(id);
    }

    private PetStore toPetStore(DocumentSnapshot documentSnapshot) {
        PetStore petStore = documentSnapshot.toObject(PetStore.class);
        if (petStore != null) {
            petStore.setId(documentSnapshot.getId());
        }
        return petStore;
    }

    private List<PetStore> toPetStores(List<DocumentSnapshot> documentSnapshots) {
        return StreamSupport.stream(documentSnapshots).map(this::toPetStore).collect(Collectors.toList());
    }

    private List<PetStore> toPetStores(QuerySnapshot queryDocumentSnapshots) {
        return toPetStores(queryDocumentSnapshots.getDocuments());
    }
}
