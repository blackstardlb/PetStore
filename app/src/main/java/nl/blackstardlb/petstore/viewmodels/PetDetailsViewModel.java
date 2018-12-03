package nl.blackstardlb.petstore.viewmodels;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.di.FavoritesService;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.PetRepository;

public class PetDetailsViewModel extends BaseViewModel {
    private PetRepository petRepository;
    private ImageManager imageManager;
    private FavoritesService favoritesService;

    @Inject
    public PetDetailsViewModel(PetRepository petRepository, ImageManager imageManager, FavoritesService favoritesService) {
        this.petRepository = petRepository;
        this.imageManager = imageManager;
        this.favoritesService = favoritesService;
    }

    public Single<Pet> getPet(String petId) {
        return petRepository.getPet(petId);
    }

    public Object getImageSource(String path) {
        return imageManager.getImageLoadSource(path);
    }

    public Completable delete(Pet pet) {
        List<String> images = pet.getImages();
        List<Completable> deletes = StreamSupport.stream(images).map(image -> imageManager.deleteImage(image)).collect(Collectors.toList());
        return Completable.merge(deletes).andThen(petRepository.deletePet(pet.getId()));
    }

    public Single<Boolean> toggleFavoriet(Pet pet) {
        return isFavorited(pet).flatMap(isFavorited -> {
            if (!isFavorited) {
                return favoritesService.addFavoriet(pet).andThen(Single.just(!isFavorited));
            } else {
                return favoritesService.deleteFavoriet(pet).andThen(Single.just(!isFavorited));
            }
        });
    }

    public Single<Boolean> isFavorited(Pet pet) {
        return favoritesService.isFavorited(pet);
    }
}
