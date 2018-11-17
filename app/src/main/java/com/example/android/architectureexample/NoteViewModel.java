package com.example.android.architectureexample;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;


/**
 * Our NoteView model which extends AndroidViewModel.
 *
 * AndroidViewModel is a sub-class of ViewModel. The difference between ViewModel and AndroidViewModel
 * is that an Application object gets passed to AndroidViewModel's constructor, which we can use whenever
 * we need a context for the application. (We did something similar with...
 *
 * NEVER store a context for an Activity or a View that references an Activity in the ViewModel because
 * the ViewModel is designed to outlive an Activity (Activities are destroyed and recreated when a device
 * is rotated/language is changed, etc.). If we hold a reference to an Activity that's been destroyed, we have
 * a memory leak. We still need a context for our repository though, since we need to instantiate a database
 * instance. This is why we extend AndroidViewModel; because we get passed an Application object in the constructor
 * which we can pass down to the database.
 *
 */
public class NoteViewModel extends AndroidViewModel {

    /**
     * We create an instance of our NoteRepository, repository.
     */
    private NoteRepository repository;

    /**
     * We create a LiveData object of type List<Note>, allNotes.
     */
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        /**
         * In our constructor, we assign the repository to a new NoteRepository, passing it the application.
         *
         * We assign allNotes to repository.getAllNotes();
         *
         */
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    /**
     * insert() method
     * @param note object
     *
     * Later, our activities will only have a reference to the ViewModel, NOT to the repository.
     * So we create wrapper methods for our database operation methods from the repository
     *
     * We pass a Note object to the method.
     *
     */

    public void insert(Note note){

        /**
         * And then call the insert() method on the repository object, passing it the Note object.
         *
         */
        repository.insert(note);
    }

    public void delete(Note note){

        /**
         * And then call the delete() method on the repository object, passing it the Note object.
         *
         */
        repository.delete(note);
    }

    public void update(Note note){

        /**
         * And then call the update() method on the repository object, passing it the Note object.
         *
         */
        repository.update(note);
    }

    /**
     *
     * Note we don't pass an argument such as a Note object to deleteAllNotes() since we're deleting ALL
     * notes, not one specific note.
     */
    public void deleteAllNotes(){

        /**
         * And then call the deleteAll() method on the repository object. Note that we don't pass
         * any arguments to deleteAllNotes() since we're not deleting a specific note but ALL notes.
         *
         */
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes(){

        /**
         * And then returns the allNotes List.
         *
         */
        return allNotes;
    }



}
