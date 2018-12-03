package nl.blackstardlb.petstore.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetStore {
    @Exclude
    private String id;
    private String name;
    private String address;
    @Exclude
    private List<Pet> pets = new ArrayList<>();

    public PetStore() {
    }

    public PetStore(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public List<Pet> getPets() {
        return Collections.unmodifiableList(pets);
    }

    @Exclude
    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    @Override
    public String toString() {
        return "PetStore{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", pets=" + pets +
                '}';
    }
}
