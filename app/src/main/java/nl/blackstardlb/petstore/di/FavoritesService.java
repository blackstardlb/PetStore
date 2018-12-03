package nl.blackstardlb.petstore.di;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import nl.blackstardlb.petstore.models.Pet;

public interface FavoritesService {
    Completable addFavoriet(Pet pet);

    Completable deleteFavoriet(Pet pet);

    Single<List<Pet>> getFavorieten();

    Single<Boolean> isFavorited(Pet pet);
}
