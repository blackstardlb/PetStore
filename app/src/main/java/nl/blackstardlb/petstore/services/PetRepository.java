package nl.blackstardlb.petstore.services;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.Pet;

public interface PetRepository {
    Single<List<Pet>> getPets();

    Single<List<Pet>> getPetsByType(String animalType);

    Single<Pet> getPet(String id);

    Single<Pet> setPet(Pet pet);

    Completable deletePet(String id);
}
