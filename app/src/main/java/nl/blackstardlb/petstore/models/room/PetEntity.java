package nl.blackstardlb.petstore.models.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import nl.blackstardlb.petstore.models.Pet;
import nl.blackstardlb.petstore.models.PetStore;

@Entity(tableName = "pet")
public class PetEntity {
    @PrimaryKey
    @NonNull
    public String id;
    @ColumnInfo(name = "pet_name")
    public String name;
    public String animalType;
    public String description;
    public String petStoreId;
    public String petStoreName;
    public String petStoreAddress;

    public static PetEntity fromPet(Pet pet) {
        PetEntity petEntity = new PetEntity();
        petEntity.description = pet.getDescription();
        petEntity.id = pet.getId();
        petEntity.name = pet.getName();
        petEntity.animalType = pet.getAnimalType();
        petEntity.petStoreId = pet.getPetStoreId();
        petEntity.petStoreAddress = pet.getPetStore().getAddress();
        petEntity.petStoreName = pet.getPetStore().getName();
        return petEntity;
    }

    public Pet toPet() {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setDescription(description);
        pet.setAnimalType(animalType);
        pet.setId(id);

        PetStore petStore = new PetStore();
        petStore.setId(petStoreId);
        petStore.setAddress(petStoreAddress);
        petStore.setName(petStoreName);

        pet.setPetStore(petStore);
        return pet;
    }

    @Override
    public String toString() {
        return "PetEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", animalType='" + animalType + '\'' +
                ", description='" + description + '\'' +
                ", petStoreId='" + petStoreId + '\'' +
                ", petStoreName='" + petStoreName + '\'' +
                ", petStoreAddress='" + petStoreAddress + '\'' +
                '}';
    }
}
