package nl.blackstardlb.petstore.di;

import com.google.firebase.auth.FirebaseAuth;
import dagger.Module;
import dagger.Provides;
import nl.blackstardlb.petstore.services.ImageManager;
import nl.blackstardlb.petstore.services.UserManager;
import nl.blackstardlb.petstore.services.UserManagerImpl;

import javax.inject.Singleton;

@Module(includes = DataModule.class)
public class UserModule {
    @Provides
    @Singleton
    UserManager providesUserManager(FirebaseAuth firebaseAuth, ImageManager imageManager) {
        return new UserManagerImpl(firebaseAuth, imageManager);
    }

    @Provides
    @Singleton
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
