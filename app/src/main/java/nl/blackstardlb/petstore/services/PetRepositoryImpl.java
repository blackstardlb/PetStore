package nl.blackstardlb.petstore.services;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.Completable;
import io.reactivex.Single;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.models.Pet;

public class PetRepositoryImpl implements PetRepository {
    private FirebaseFirestore firestore;
    private PetStoreRepository petStoreRepository;

    @Inject
    public PetRepositoryImpl(FirebaseFirestore firestore, PetStoreRepository petStoreRepository) {
        this.firestore = firestore;
        this.petStoreRepository = petStoreRepository;
    }

    @Override
    public Single<List<Pet>> getPets() {
        Query query = getCollectionRef().orderBy("name", Query.Direction.DESCENDING);
        return RxFirestore.getCollection(query)
                .map(this::toPets)
                .toSingle(new ArrayList<>());
    }

    @Override
    public Single<List<Pet>> getPetsByType(String animalType) {
        Query query = getCollectionRef().whereEqualTo("animalType", animalType.toLowerCase()).orderBy("name", Query.Direction.DESCENDING);
        return RxFirestore.getCollection(query)
                .map(this::toPets)
                .toSingle(new ArrayList<>());
    }

    @Override
    public Single<Pet> getPet(String id) {
        return includePetStore(RxFirestore.getDocument(getDocumentRef(id))
                .map(this::toPet)
                .toSingle());
    }

    @Override
    public Single<Pet> setPet(Pet pet) {
        Log.d("TEST", "setPet: " + pet.toString());
        if (pet.getId() != null) {
            DocumentReference document = getDocumentRef(pet.getId());
            return includePetStore(RxFirestore.setDocument(document, pet).andThen(Single.just(pet)));
        }
        return includePetStore(RxFirestore.addDocument(getCollectionRef(), pet).map(documentReference -> {
            pet.setId(documentReference.getId());
            return pet;
        }));
    }

    @Override
    public Completable deletePet(String id) {
        return RxFirestore.deleteDocument(getDocumentRef(id));
    }

    private CollectionReference getCollectionRef() {
        return firestore.collection("pets");
    }

    private DocumentReference getDocumentRef(String id) {
        return getCollectionRef().document(id);
    }

    private Single<Pet> includePetStore(Single<Pet> petSingle) {
        return petSingle.flatMap(pet -> petStoreRepository.getPetStore(pet.getPetStoreId()).map(petStore -> {
            pet.setPetStore(petStore);
            return pet;
        }));
    }

    private Pet toPet(DocumentSnapshot documentSnapshot) {
        Pet pet = documentSnapshot.toObject(Pet.class);
        if (pet != null) {
            pet.setId(documentSnapshot.getId());
        }
        return pet;
    }

    private List<Pet> toPets(List<DocumentSnapshot> documentSnapshots) {
        return StreamSupport.stream(documentSnapshots).map(this::toPet).collect(Collectors.toList());
    }

    private List<Pet> toPets(QuerySnapshot queryDocumentSnapshots) {
        return toPets(queryDocumentSnapshots.getDocuments());
    }
}
