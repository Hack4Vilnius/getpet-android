package lt.getpet.getpet.persistence

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import lt.getpet.getpet.data.Pet
import lt.getpet.getpet.data.PetChoice

@Dao
interface PetDao {

    @Query("SELECT Pets.* FROM Pets LEFT JOIN PetChoices ON Pets.id = PetChoices.pet_id WHERE PetChoices.is_favorite IS NULL")
    fun getRemainingPets(): Single<List<Pet>>

    @Query("SELECT Pets.* FROM Pets INNER JOIN PetChoices ON Pets.id = PetChoices.pet_id AND PetChoices.is_favorite = 1 ORDER BY PetChoices.created_at DESC")
    fun getFavoritePets(): Flowable<List<Pet>>

    @Query("SELECT Pets.id FROM Pets INNER JOIN PetChoices ON Pets.id = PetChoices.pet_id AND PetChoices.is_favorite = 1")
    fun getLikedPetIds(): Single<List<Long>>

    @Query("SELECT Pets.id FROM Pets INNER JOIN PetChoices ON Pets.id = PetChoices.pet_id AND PetChoices.is_favorite = 0")
    fun getDislikedPetIds(): Single<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPetChoice(petChoice: PetChoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPets(pets: List<Pet>): List<Long>

    @Update
    fun updatePets(pets: List<Pet>): Int


}