package nl.blackstardlb.petstore.services;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.PetStore;

public interface PetStoreRepository {
    Single<List<PetStore>> getPetStores();

    Single<PetStore> getPetStore(String id);

    Single<PetStore> setPetStore(PetStore pet);

    Completable deletePetStore(String id);
}
