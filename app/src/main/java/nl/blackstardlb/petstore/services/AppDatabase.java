package nl.blackstardlb.petstore.services;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import nl.blackstardlb.petstore.models.room.PetDao;
import nl.blackstardlb.petstore.models.room.PetEntity;
import nl.blackstardlb.petstore.models.room.PetImageDao;
import nl.blackstardlb.petstore.models.room.PetImageEntity;

@Database(entities = {PetEntity.class, PetImageEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PetDao petDao();

    public abstract PetImageDao petImageDao();
}