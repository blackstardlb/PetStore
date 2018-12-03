package nl.blackstardlb.petstore.viewmodels;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.services.PetRepository;

public class PetViewModel extends BaseViewModel {
    private PetRepository petRepository;

    @Inject
    public PetViewModel(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Observable<Pet> getPet(String id) {
        return Observable.empty();
    }

    public Completable setPet(Pet pet) {
        return Completable.complete();
    }

    public Single<List<Pet>> getPets() {
        return petRepository.getPets();
    }

    public PetRepository getPetRepository() {
        return petRepository;
    }
}
