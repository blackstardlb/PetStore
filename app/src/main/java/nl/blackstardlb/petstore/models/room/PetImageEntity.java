package nl.blackstardlb.petstore.models.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"petId", "imageUrl"}, tableName = "pet_image",
        foreignKeys = {
                @ForeignKey(entity = PetEntity.class, parentColumns = "id", childColumns = "petId", onDelete = ForeignKey.CASCADE)
        }
)
public class PetImageEntity {
    @NonNull
    public String petId;
    @NonNull
    public String imageUrl;
}
