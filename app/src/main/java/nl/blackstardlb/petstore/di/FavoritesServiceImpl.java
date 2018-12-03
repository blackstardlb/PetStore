package nl.blackstardlb.petstore.di;

import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.room.PetDao;
import nl.blackstardlb.petstore.models.room.PetEntity;
import nl.blackstardlb.petstore.models.room.PetImageDao;
import nl.blackstardlb.petstore.models.room.PetImageEntity;
import nl.blackstardlb.petstore.services.AppDatabase;

public class FavoritesServiceImpl implements FavoritesService {

    private final PetDao petDao;
    private final PetImageDao petImageDao;

    public FavoritesServiceImpl(AppDatabase appDatabase) {
        petDao = appDatabase.petDao();
        petImageDao = appDatabase.petImageDao();
    }

    @Override
    public Completable addFavoriet(Pet pet) {
        PetImageEntity[] petImageEntities = {};
        PetImageEntity[] collect = StreamSupport.stream(pet.getImages()).map(image -> {
            PetImageEntity petImageEntity = new PetImageEntity();
            petImageEntity.petId = pet.getId();
            petImageEntity.imageUrl = image;
            return petImageEntity;
        }).collect(Collectors.toList()).toArray(petImageEntities);

        return Completable.fromAction(() -> {
            petDao.insertLargeNumberOfPets(PetEntity.fromPet(pet));
            petImageDao.insertLargeNumberOfPetImages(collect);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deleteFavoriet(Pet pet) {
        return Completable.fromAction(() -> petDao.delete(PetEntity.fromPet(pet))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<List<Pet>> getFavorieten() {
        return Single.fromCallable(petDao::loadPets).map(pets -> {
            Log.d("Test", "Pets " + pets.toString());
            return StreamSupport.stream(pets).map(PetEntity::toPet).collect(Collectors.toList());
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Boolean> isFavorited(Pet pet) {
        return Single.fromCallable(() -> {
            PetEntity petEntity = petDao.loadPetById(pet.getId());
            return petEntity != null;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
