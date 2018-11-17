package com.example.android.architectureexample;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * We use the @Database annotation to specify that this will be the
 * database class.
 *
 * We need to provide the entities and version arguments:
 * The entities, i.e. the tables we want to put in (as defined in our Note.class)
 * The version number of our database.
 */
@Database(entities = Note.class, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    /**
     *
     * We create a static variable to represent an instance of our database.
     * We need to turn this class into a singleton, i.e. we can't create multiple instances of this
     * database, i.e. we use the same instance of the database throughout our app, which we access
     * via this static variable.
     *
     */
    private static NoteDatabase instance;

    /**
     * We create an abstract method (we don't provide the method body) which returns a NoteDao object.
     * We use this method to access our NoteDao.
     *
     * @return
     */
    public abstract NoteDao noteDao();


    /**
     * getInstance() method creates our database.
     * @param context
     * @return
     *
     * Again we want this to be a singleton so we make it static.
     * synchronized means only one thread at a time can access this method. If two threads tried to call this
     * method, they might end up creating two instances of the database. synchronized avoids this.
     *
     * We need to pass the method a Context object and it will return a NoteDatabase instance.
     *
     *
     */
    public static synchronized NoteDatabase getInstance(Context context){

        /**
         * Now in this method we create the ONLY instance of the database for use by this app.
         * We then call this method from any other part of the app we need to and get a handle to this
         * instance.

         * First up we check to see if we already HAVE an instance of the db. Remember, we only want
         * to instantiate the db if we DON'T have an instance of it already.
         *
         */
        if (instance == null) {

            /**
             * Normally we'd go something like "instance = new NoteDatabase". We can't do this as
             * it's an abstract class so we need to use the databaseBuilder() method.
             *
             * We call the Room.databaseBuilder() method and pass it the context, the name of the class
             * (i.e. NoteDatabase.class) and the name we want our database file to have, as a String.
             *
             * Then we call the fallbackToDestructiveMigration() method. In the event of us incrementing
             * the version number of our database, we need to tell Room how to migrate to the new schema.
             * If we don't do this and try to increase the version number, our app will crash.
             * fallbackToDestructiveMigration() will avoid this deleting the database with all its tables
             * and create it from scratch.
             *
             * So in short, if you increment the version number and you're invoking fallbackToDestructiveMigration(),
             * which we are here, you'll start with a new database.
             *
             * Finally we call .build() which returns a new instance of this database.
             *
             */
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        /**
         * Finally we return the database instance. If an instance of the database was already created,
         * i.e. the above if statement was false, then this means we're just returning the database instance
         * that currently exists.
         *
         */
        return instance;

    }

    /**
     * We're going to populate the database with some data when it's created.
     *
     * To do this, we need to access the onCreate() method in the Room Callback class.
     * So we create a new instance of RoomDatabase.Callback called roomCallBack.
     *
     * This method must be static as we'll want to call it later from our getInstance() method, which is
     * also static.
     *
     * Then we override the onCreate() method which takes ones argument, a SupportSQLiteDatabase,
     * here called db. We pass this to the super() method for the parent class' functionality.
     *
     * Then we create a new instance of our PopulateDBAsyncTask class and pass it the instance of
     * our database. Remember, all database operations must be done on a background thread.
     */

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };


    /**
     * PopulateDBAsyncTask sub-class.
     *
     * This class must be static and extend the AsyncTask class.
     * Our three parameters will be Void here.
     *
     */
    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        /**
         * We need an instance of our NoteDao class.
         */
        private NoteDao noteDao;


        /**
         * PopulateDBAsyncTask constructor.
         * @param db, a NoteDatabase object.
         *
         * In this class we don't have a member variable for NoteDao, so we pass our NoteDatabase
         * instance, and get the noteDao from there instead. (???) This is possible because onCreate()
         * is called AFTER the database is created.
         *
         * NOTE: We have to obtain our NoteDao object in this way because, unlike the NoteRepository class,
         * the NoteDatabase class does NOT have a NoteDao object declared at the top. So, in order to get our NoteDao
         * we need to pass a database instance to the constructor, db, and then assign the private NoteDao variable we
         * declared above, noteDao, to the result of db.noteDao().
         *
         * This is instead of this.noteDao = noteDao; which we were able to do in the ASynctask methods in the NotesRepository class
         * which DOES have a private NoteDao noteDao; at the beginning of the class.
         */
        private PopulateDBAsyncTask(NoteDatabase db) {
            noteDao = db.noteDao();
        }

        /**
         * doInBackground() method
         * @param voids
         * @return
         *
         * We then call the insert() method on our noteDao object and pass it some data to have something
         * to show when the database is newly-created, instead of a blank table.
         */
        @Override
        protected Void doInBackground(Void... voids) {

            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            return null;
        }
    }

}
