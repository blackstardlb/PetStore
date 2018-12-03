package nl.blackstardlb.petstore.di.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import nl.blackstardlb.petstore.viewmodels.AddPetViewModel;
import nl.blackstardlb.petstore.viewmodels.LoginViewModel;
import nl.blackstardlb.petstore.viewmodels.MainActivityViewModel;
import nl.blackstardlb.petstore.viewmodels.PetDetailsViewModel;
import nl.blackstardlb.petstore.viewmodels.PetStoreViewModel;
import nl.blackstardlb.petstore.viewmodels.PetViewModel;
import nl.blackstardlb.petstore.viewmodels.ProfileImageViewModel;
import nl.blackstardlb.petstore.viewmodels.UserNameViewModel;
import nl.blackstardlb.petstore.viewmodels.UserProfileViewModel;
import nl.blackstardlb.petstore.views.FavorietViewModel;

@Module()
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(PetViewModel.class)
    abstract ViewModel bindPetViewModel(PetViewModel petViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel bindUserProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileImageViewModel.class)
    abstract ViewModel bindProfileImageViewModel(ProfileImageViewModel profileImageViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserNameViewModel.class)
    abstract ViewModel bindUserNameViewModel(UserNameViewModel userNameViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PetStoreViewModel.class)
    abstract ViewModel bindPetStoreViewModel(PetStoreViewModel petStoreViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel bindMainActivityViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddPetViewModel.class)
    abstract ViewModel bindAddPetViewModel(AddPetViewModel addPetViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PetDetailsViewModel.class)
    abstract ViewModel bindPetDetailsViewModel(PetDetailsViewModel petDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavorietViewModel.class)
    abstract ViewModel bindFavorietViewModel(FavorietViewModel favorietViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory viewModelFactory);
}

