package nl.blackstardlb.petstore.models;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public class User {
    private String email;
    private String displayName;
    private String id;
    private String picture;

    public User(@NonNull String email, @Nullable String displayName, @NonNull String id, @Nullable String picture) {
        this.email = email;
        this.displayName = displayName;
        this.id = id;
        this.picture = picture;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getPicture() {
        return picture;
    }
}
