package nl.blackstardlb.petstore.views;

import android.support.design.widget.TextInputLayout;

import java8.util.Optional;

public class ViewHelpers {
    public static void linkErrorToTextInputLayout(Optional<String> error, TextInputLayout textInputLayout) {
        if (error.isPresent()) {
            textInputLayout.setError(error.get());
        }
        textInputLayout.setErrorEnabled(error.isPresent());
    }
}
