package com.example.android.architectureexample;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * DAOs are always either an interface or an abstract class since we don't write the method bodies.
 * We just create a method, annotate it, and Room does the rest.
 *
 */


/**
 * Use the Dao annotation to specify that this is a Dao. Usually it's a good idea to have one Dao
 * per entity.
 *
 * In this interface, we'll create all the methods we want for the operations we want to perform
 * on our Note table.
 */
@Dao
public interface NoteDao {

    /**
     * insert method
     * @param note
     *
     * A method for inserting new notes into the database.
     * Takes one Note object, note.
     * Returns nothing.
     *
     * NOTE: We don't write the method body; we just annotate it and let Room handle the rest.
     *
     */
    @Insert
    void insert(Note note);


    /**
     * update() method
     * @param note
     *
     * A method for updating note entries in the database.
     *
     */
    @Update
    void update(Note note);


    /**
     * delete() method
     * @param note
     *
     * A method for deleting note entries in the database.
     *
     */
    @Delete
    void delete(Note note);


    /**
     * We need a query for deleting all notes at once. Room doesn't have a convenience annotation
     * for that so we use the @Query annotation instead and define the database operation ourselves
     * in the form of a string.
     *
     * DELETE FROM (with nothing between the two words) means delete EVERYTHING.
     *
     *
     */
    @Query("DELETE FROM note_table")
    void deleteAllNotes();


    /**
     * We need one more query to get ALL the notes from the database to display them in our RecyclerView.
     *
     * Again, there's no convenience annotation for this, we we use the @Query annotation instead and
     * define the query ourselves.
     *
     * We create the query using a string (SELECT * means select ALL columns) and we order them
     * in descending order.
     *
     * The method should return a List of Note objects, i.e. ALL the Notes in the database so
     * that's why we return a List.
     *
     * We are also returning LiveData which means we can observe the list. As soon as there are any changes
     * in the Note table, this LiveData wrapper means the value will be automatically updated and our Activity
     * or Fragment will be notified. Room does the updating of the data itself.
     *
     */
    @Query("SELECT * FROM note_table ORDER BY priority DESC")
    LiveData<List<Note>> getAllNotes();

}
