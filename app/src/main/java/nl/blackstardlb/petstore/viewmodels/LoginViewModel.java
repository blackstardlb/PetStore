package nl.blackstardlb.petstore.viewmodels;


import io.reactivex.subjects.BehaviorSubject;

import javax.inject.Inject;

public class LoginViewModel extends BaseViewModel {
    private BehaviorSubject<LoginModel> loginModelBehaviorSubject = BehaviorSubject.createDefault(new LoginModel());

    @Inject
    public LoginViewModel() {
    }

    public BehaviorSubject<LoginModel> getLoginModelBehaviorSubject() {
        return loginModelBehaviorSubject;
    }

    public void setLoginModel(LoginModel loginModel) {
        loginModelBehaviorSubject.onNext(loginModel);
    }

    public class LoginModel {
        private String email;
        private String password;

        public LoginModel(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public LoginModel() {
            this("", "");
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}
