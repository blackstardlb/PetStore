package nl.blackstardlb.petstore.di;

import javax.inject.Singleton;

import dagger.Component;
import nl.blackstardlb.petstore.di.viewmodels.ViewModelModule;
import nl.blackstardlb.petstore.views.LoginFragment;
import nl.blackstardlb.petstore.views.base.BaseActivity;
import nl.blackstardlb.petstore.views.base.BaseDialogFragment;
import nl.blackstardlb.petstore.views.base.BaseFragment;

@Singleton
@Component(modules = {ApplicationModule.class, DataModule.class, UserModule.class, ViewModelModule.class})
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    void inject(LoginFragment loginFragment);

    void inject(BaseFragment baseFragment);

    void inject(BaseDialogFragment baseDialogFragment);
}
