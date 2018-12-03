package nl.blackstardlb.petstore.models.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.reactivex.Flowable;

@Dao
public interface PetImageDao {
    @Query("SELECT * from pet_image where petId = :id LIMIT 1")
    Flowable<PetImageEntity> loadPetByPetId(String id);

    @Insert
    public void insertLargeNumberOfPetImages(PetImageEntity... petImages);

    @Delete
    public void delete(PetImageEntity petImageEntity);
}
