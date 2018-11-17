package com.example.android.architectureexample;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NoteRepository {

    /**
     * We create an instance of our NoteDao, noteDao and also create a LiveData object of type List
     * Note objects, allNotes.
     */
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;


    /**
     * NoteRepository constructor
     * @param application object
     * THe constructor acccepts an Application object. Later in the ViewModel, we'll also get
     * passed an Application. Since Application is a sub-class of Context, we can use this Application
     * object as the context to create our database instance.
     *
     * We now assign values to the two private variables created above.
     */
    public NoteRepository(Application application){

        /**
         * Create an instance of the database using getInstance() and pass it the Application object
         * we created above as a context. We assign this to the NoteDatabase object, database.
         */
        NoteDatabase database = NoteDatabase.getInstance(application);

        /**
         * The noteDao is the abstract method we created in our database class. We normally can't call
         * abstract methods since they have no body defined. But since created our NoteDatabase instance
         * with the builder, Room automaticall generates all the relevant code for this abstract method so
         * we CAN call it. So, the following returns an NoteDao object and assigns it to noteDao.
         */
        noteDao = database.noteDao();

        /**
         * We call the abstract method getAllNotes() from our NoteDao class. Again, this is an abstract
         * method but Room generates the code for us so we can call it from here on our noteDao object and assign
         * the result to the allNotes LivaData<List<Note>> object.
         */
        allNotes = noteDao.getAllNotes();

    }

        /**
         * Now we create methods for all our database operations. These methods are the API which the
         * Repository exposes to the rest of the app. Later our ViewModel will only need to call the
         * methods below from this Repository without knowing or caring about how the code in the
         * AsyncTask methods is working. This is an abstraction layer.
         *
         * We need to write our own code to
         * perform all of these operations on the bcakground thread ourselves (apart from getAllNotes()).
         *
         *
         * The first one is insert() to which we pass a Note object.
         *
         */

        public void insert (Note note){

            /**
             * Create a new instance of our InsertNoteAsyncTask() method and pass it our noteDao object.
             * Then call the execute() method and pass it the note object.
             *
             */
            new InsertNoteAsyncTask(noteDao).execute(note);
        }

    /**
     * update() method
     * @param note object
     *
     * Method for updating notes in the database
     */
    public void update (Note note) {

        new UpdateNoteAsycnTask(noteDao).execute(note);

        }

    /**
     * delete() method
     * @param note object
     *
     * Method for deleting notes from the database
     */
    public void delete (Note note) {

        new DeleteNoteAsycnTask(noteDao).execute(note);
        }

    /**
     * deleteAllNotes() method
     *
     * Method for deleting ALL notes from the database.
     * Notice that we don't pass it a Note object as we're not deleting a specific note but ALL notes.
     */
    public void deleteAllNotes() {

        new DeleteAllNotesAsycnTask(noteDao).execute();
        }

    /**
     * getAllNotes() method
     * @return LiveData<List<Note>> object.
     *
     * Method for getting all the notes from the database for display in our RecyclerView.
     *
     * This is a LiveData object of type List<Note> which returns a list, referenced by the allNotes
     * LiveData<List<Note>> object we created at the start of this class. This List is retrieved from
     * our noteDao in the line  allNotes = noteDao.getAllNotes(); back up in the constructor.
     *
     * Room will automatically perform the database operation that returns this LiveData object on the background
     * thread. So no need for us to piddle about with Async tasks or anything.
     *
     */

    public LiveData<List<Note>> getAllNotes() {
            return allNotes;

    }

    /**
     * Now we need to create Asynctask methods for all of the database operations we want to perform,
     *
     * We need to make them static so they don't have a reference to the Repository class itself. This could
     * cause a memory leak.
     *
     * First up is the method for inserting a new note to the database.
     *
     * We call is InsertNoteAsyncTask and it must extend the AsyncTask class, obviously.
     *
     * ASync task takes three parameters, representing the data we're passing to it, task progress updates
     * and finally a return type.
     *
     * For this method, we're just passing a Note object and Void since we don't need any progress updates
     * and we're not returning anything.
     *
     */

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void>{

        /**
         * We first of all need to create a NoteDao object that we pass to the constructor below.
         * Remember, all database operations need a NoteDao object; the NoteDao object is the object via
         * which we perform our operations.
         *
         * Because this is a static class, we can't access the NoteDao object of our Repository class directly
         * (the one we declared at the top of this class) so that's why we need another one that we can pass to
         * the constructor below which we then assign to the Repository one).
         *
         */
        private NoteDao noteDao;

        /**
         * InsertNoteSyncTask constructor.
         * @param noteDao object.
         *
         * Our constructor will accept a NoteDao object. We had to create another one within this
         * AsyncTask class since we can't access the Repository class one from here, since this is a static
         * method.
         *
         * Then we assign THIS noteDao object to the noteDao object we get passed from the call to this
         * method back up in the constructor.
         *
         */
        private InsertNoteAsyncTask(NoteDao noteDao) {

            this.noteDao = noteDao;
        }

        /**
         * doInBackground() method
         * @param notes (varargs get passed as is usual for an ASync task. They're *kind of* like Arrays.)
         * @return
         *
         * The only mandatory method we need to override in an AsyncTask method. All other methods
         * are optional.
         *
         */
        @Override
        protected Void doInBackground(Note... notes) {
            /**
             *
             * In the background, we now call the insert method to perform the insert operation.
             * We pass it the 0th index note, i.e. the first index.
             *
             */
            noteDao.insert(notes[0]);
            return null;
        }
    }





    private static class UpdateNoteAsycnTask extends AsyncTask<Note, Void, Void>{

        /**
         * We first of all need to create a NoteDao object that we pass to the constructor below.
         * Remember, all database operations need a NoteDao object; the NoteDao object is the object via
         * which we perform our operations.
         *
         * Because this is a static class, we can't access the NoteDao object of our Repository class directly
         * (the one we declared at the top of this class) so that's why we need another one that we can pass to
         * the constructor below).
         *
         */
        private NoteDao noteDao;

        /**
         * InsertNoteSyncTask constructor.
         * @param noteDao object.
         *
         * Our constructor will accept a NoteDao object. We had to create another one within this
         * AsyncTask class since we can't access the Repository class one from here, since this is a static
         * method.
         *
         * Then we assign THIS noteDao object to the noteDao object we get passed from the call to this
         * method back up in the constructor.
         *
         */
        private UpdateNoteAsycnTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        /**
         * doInBackground() method
         * @param notes (varargs get passed as is usual for an ASync task. They're *kind of* like Arrays.)
         * @return
         *
         * The only mandatory method we need to override in an AsyncTask method. All other methods
         * are optional.
         *
         */
        @Override
        protected Void doInBackground(Note... notes) {
            /**
             *
             * In the background, we now call the update method to perform the update operation.
             * We pass it the 0th index note, i.e. the first index.
             *
             */
            noteDao.update(notes[0]);
            return null;
        }
    }






    private static class DeleteNoteAsycnTask extends AsyncTask<Note, Void, Void>{

        /**
         * We first of all need to create a NoteDao object that we pass to the constructor below.
         * Remember, all database operations need a NoteDao object; the NoteDao object is the object via
         * which we perform our operations.
         *
         * Because this is a static class, we can't access the NoteDao object of our Repository class directly
         * (the one we declared at the top of this class) so that's why we need another one that we can pass to
         * the constructor below).
         *
         */
        private NoteDao noteDao;

        /**
         * DeleteNoteAsycnTask constructor.
         * @param noteDao object.
         *
         * Our constructor will accept a NoteDao object. We had to create another one within this
         * AsyncTask class since we can't access the Repository class one from here, since this is a static
         * method.
         *
         * Then we assign THIS noteDao object to the noteDao object we get passed from the call to this
         * method back up in the constructor.
         *
         */
        private DeleteNoteAsycnTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        /**
         * doInBackground() method
         * @param notes (varargs get passed as is usual for an ASync task. They're *kind of* like Arrays.)
         * @return
         *
         * The only mandatory method we need to override in an AsyncTask method. All other methods
         * are optional.
         *
         */
        @Override
        protected Void doInBackground(Note... notes) {
            /**
             *
             * In the background, we now call the delete method to perform the delete operation.
             * We pass it the 0th index note, i.e. the first index.
             *
             */
            noteDao.delete(notes[0]);
            return null;
        }
    }


    private static class DeleteAllNotesAsycnTask extends AsyncTask<Void, Void, Void>{

        /**
         * We first of all need to create a NoteDao object that we pass to the constructor below.
         * Remember, all database operations need a NoteDao object; the NoteDao object is the object via
         * which we perform our operations.
         *
         * Because this is a static class, we can't access the NoteDao object of our Repository class directly
         * (the one we declared at the top of this class) so that's why we need another one that we can pass to
         * the constructor below).
         *
         */
        private NoteDao noteDao;

        /**
         * DeleteNoteAsycnTask constructor.
         * @param noteDao object.
         *
         * Our constructor will accept a NoteDao object. We had to create another one within this
         * AsyncTask class since we can't access the Repository class one from here, since this is a static
         * method.
         *
         * Then we assign THIS noteDao object to the noteDao object we get passed from the call to this
         * method back up in the constructor.
         *
         */
        private DeleteAllNotesAsycnTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        /**
         * doInBackground() method
         *
         * The only mandatory method we need to override in an AsyncTask method. All other methods
         * are optional.
         *
         */
        @Override
        protected Void doInBackground(Void... voids) {
            /**
             *
             * In the background, we now call the delete method to perform the delete operation.
             * We pass it the 0th index note, i.e. the first index.
             *
             */
            noteDao.deleteAllNotes();
            return null;
        }
    }

}
