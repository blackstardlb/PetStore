package nl.blackstardlb.petstore.views;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import nl.blackstardlb.petstore.di.FavoritesService;
import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.viewmodels.BaseViewModel;

public class FavorietViewModel extends BaseViewModel {
    private FavoritesService favoritesService;

    @Inject
    public FavorietViewModel(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    public Single<List<Pet>> getFavorieten() {
        return this.favoritesService.getFavorieten();
    }
}
