package nl.blackstardlb.petstore.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pet {
    @Exclude
    private String id;
    private String name;
    private String animalType;
    private String petStoreId;
    @Exclude
    private PetStore petStore;
    private String description;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        if (images == null) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
    }

    private List<String> images = new ArrayList<>();

    public Pet(String id, String name, String animalType, PetStore petStore, String description) {
        this.name = name;
        this.animalType = animalType;
        this.petStore = petStore;
        this.description = description;
        this.id = id;
    }

    public Pet() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType.toLowerCase();
    }

    public String getPetStoreId() {
        return petStoreId;
    }

    @Exclude
    public PetStore getPetStore() {
        return petStore;
    }

    @Exclude
    public void setPetStore(PetStore petStore) {
        this.petStore = petStore;
        if (petStore != null) {
            this.petStoreId = petStore.getId();
        } else {
            petStoreId = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", animalType='" + animalType + '\'' +
                ", petStoreId='" + petStoreId + '\'' +
                ", petStore=" + petStore +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
