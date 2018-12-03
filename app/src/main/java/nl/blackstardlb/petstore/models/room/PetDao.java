package nl.blackstardlb.petstore.models.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PetDao {
    @Query("SELECT * from pet where id = :id LIMIT 1")
    PetEntity loadPetById(String id);

    @Query("SELECT * from pet")
    List<PetEntity> loadPets();

    @Insert
    public void insertLargeNumberOfPets(PetEntity... pets);

    @Delete
    public void delete(PetEntity pet);
}
