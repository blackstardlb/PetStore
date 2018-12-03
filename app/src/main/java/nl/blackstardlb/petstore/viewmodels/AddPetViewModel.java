package nl.blackstardlb.petstore.viewmodels;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java8.util.J8Arrays;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.PetStore;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.PetRepository;
import nl.blackstardlb.petstore.services.PetStoreRepository;

public class AddPetViewModel extends BaseViewModel {
    private PetStoreRepository petStoreRepository;
    private PetRepository petRepository;
    private ImageManager imageManager;

    private BehaviorSubject<Pet> petBehaviorSubject = BehaviorSubject.createDefault(new Pet());
    private BehaviorSubject<List<PetStore>> petStoresSubject = BehaviorSubject.createDefault(new ArrayList<>());
    private BehaviorSubject<Integer> selectedIndex = BehaviorSubject.createDefault(0);
    private PublishSubject<Bitmap> bitmapPublishSubject = PublishSubject.create();
    private List<Bitmap> bitmaps = new ArrayList<>();

    @Inject
    public AddPetViewModel(PetStoreRepository petStoreRepository, PetRepository petRepository, ImageManager imageManager) {
        this.petStoreRepository = petStoreRepository;
        this.petRepository = petRepository;
        this.imageManager = imageManager;
    }

    public void loadPetStores() {
        Disposable disposable = petStoreRepository.getPetStores().subscribe(petStores -> petStoresSubject.onNext(petStores));
        addDisposable(disposable);
    }

    public Observable<List<PetStore>> getPetstores() {
        return petStoresSubject;
    }

    private void setSelectedPetstore(PetStore petstore) {
        Pet pet = petBehaviorSubject.getValue();
        pet.setPetStore(petstore);
        petBehaviorSubject.onNext(pet);
    }

    public void setPetName(String name) {
        Pet pet = petBehaviorSubject.getValue();
        pet.setName(name);
        petBehaviorSubject.onNext(pet);
    }

    public void setPetAnimalType(String animalType) {
        Pet pet = petBehaviorSubject.getValue();
        pet.setAnimalType(animalType);
        petBehaviorSubject.onNext(pet);
    }

    public void setPetDescription(String description) {
        Pet pet = petBehaviorSubject.getValue();
        pet.setDescription(description);
        petBehaviorSubject.onNext(pet);
    }

    public Pet getPet() {
        return petBehaviorSubject.getValue();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex.onNext(selectedIndex);
        PetStore petStore = petStoresSubject.getValue().get(selectedIndex);
        Log.d("TEST", "setSelectedIndex: " + petStore);
        setSelectedPetstore(petStore);
    }

    public Integer getSelectedIndex() {
        return selectedIndex.getValue();
    }

    public void reset() {
        petBehaviorSubject.onNext(new Pet());
        setSelectedIndex(0);
        bitmaps = new ArrayList<>();
    }

    public Single<Pet> save() {
        Pet pet = petBehaviorSubject.getValue();

        List<Single<String>> saves = StreamSupport.stream(bitmaps).map(bitmap -> imageManager.saveImage(bitmap)).collect(Collectors.toList());
        if (!saves.isEmpty()) {
            return Single.zip(saves, objects -> J8Arrays.stream(objects).map(it -> (String) it).collect(Collectors.toList())).flatMap(images -> {
                pet.setImages(images);
                return petRepository.setPet(pet);
            });
        } else {
            return petRepository.setPet(pet);
        }
    }

    public void addBitmap(Bitmap imageBitmap) {
        bitmaps.add(imageBitmap);
        bitmapPublishSubject.onNext(imageBitmap);
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public Observable<Bitmap> observeBitmaps() {
        return bitmapPublishSubject;
    }
}
