package nl.blackstardlb.petstore.viewmodels;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import java8.util.Optional;
import nl.blackstardlb.petstore.models.PetStore;
import nl.blackstardlb.petstore.services.PetStoreRepository;

public class PetStoreViewModel extends BaseViewModel {

    BehaviorSubject<PetStore> petStoreBehaviorSubject = BehaviorSubject.createDefault(new PetStore(null, "", ""));
    private PetStoreRepository petStoreRepository;

    @Inject
    public PetStoreViewModel(PetStoreRepository petStoreRepository) {
        this.petStoreRepository = petStoreRepository;
    }

    public void setName(String name) {
        PetStore value = petStoreBehaviorSubject.getValue();
        value.setName(name);
        petStoreBehaviorSubject.onNext(value);
    }

    public void setAddress(String address) {
        PetStore value = petStoreBehaviorSubject.getValue();
        value.setAddress(address);
        petStoreBehaviorSubject.onNext(value);
    }

    public Observable<PetStore> getPetStore() {
        return petStoreBehaviorSubject;
    }

    public Observable<Optional<String>> nameError() {
        return getPetStore().map(it -> {
            String message = null;
            String name = it.getName();
            if (name.equals("")) {
                message = "Name is required";
            }
            return Optional.ofNullable(message);
        });
    }

    public Observable<Optional<String>> addressError() {
        return getPetStore().map(it -> {
            String message = null;
            String address = it.getAddress();
            if (address.equals("")) {
                message = "Address is required";
            }
            return Optional.ofNullable(message);
        });
    }

    public Observable<Boolean> isValid() {
        return Observable.combineLatest(nameError(), addressError(), (name, address) -> !name.isPresent() && !address.isPresent());
    }

    public Completable save() {
        return petStoreRepository.setPetStore(petStoreBehaviorSubject.getValue()).ignoreElement();
    }

    public void reset() {
        petStoreBehaviorSubject.onNext(new PetStore(null, "", ""));
    }
}
