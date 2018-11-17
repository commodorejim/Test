package com.example.android.architectureexample;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {




    public static final int ADD_NOTE_REQUEST = 1;

    /**
     * We need a constant for the request for editing notes, as opposed to adding them. This
     * MUST be different to the one above.
     *
     */
    public static final int EDIT_NOTE_REQUEST = 2;


    /**
     * We need to get an instance of our NoteViewModel so we can attach the observer to the LiveData
     * object.
     *
     */
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        /**
         * We create a RecyclerView object and get its reference from the activity_main.xml file.
         *
         */
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        /**
         * Every RecyclerView needs a LayoutManager so we call setLayoutManager() on the recyclerView object
         * and as an argument, pass it a call to a new LinearLayoutManager() object, passing it this as a context,
         *
         */
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /**
         * We call the setHasFixedSize() method and pass it true, which we should always do if we know the
         * RecyclerView won't change size. This makes it more efficient.
         *
         */
        recyclerView.setHasFixedSize(true);

        /**
         * Finally we need to create a new instance of our NoteAdapter.
         * Remember, the adapter adapts the list of data (in this case, our List of Note objects) for display in our RecyclerView.
         *
         */
        final NoteAdapter adapter = new NoteAdapter();

        /**
         * Then we call setAdapter() on the RecyclerView object, passing it our NoteAdapter adapter.
         *
         */
        recyclerView.setAdapter(adapter);

        /**
         * Now we want to create a new ViewModel, based on our NoteViewModel class.
         *
         * We don't say noteViewModel = new noteViewModel(); as this would create a new instance
         * of the noteViewModel for every new Activity, since MainActivity is our entry point into the app.
         * Instead we ask the Android system to give us a ViewModel, since Android knows when it has to create a NEW ViewModel instance
         * and when it has to provide an instance of an ALREADY existing ViewModel.
         *
         * So we call a class called ViewModelProviders.of() and pass it the current Activity/Fragment, this.
         * This way, the ViewModel knows which Activity/Fragment lifecycle it needs to be scoped to. By passing this,
         * i.e. in this case MainActivity, Android will destroy this ViewModel when MainActivity is finished.
         *
         * Then we call get() and pass it the classname of our NoteViewModel class, as this is the ViewModel we
         * want to get an instance of.
         *
         */
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        /**
         * With a new instance of our ViewModel created, we can now attach the observer to the LiveData object.
         *
         * To do this, we call the getAllNotes() getter method, which returns LiveData. As a result, we
         * then call the observe() method (which is a LiveData method_ and pass it the current Activity/Fragment, this.
         *
         * We need to pass two arguments to observe():
         * 1. The life-cycle owner, so we pass the current Activity/Fragment, this. Remember, LiveData is life-cycle aware
         * and it will only update the Activity if it's in the foreground. When our Activity is destroyed, it will automatically
         * clean up any references to the Activity, so we should avoid memory-leaks/crashes.
         * 2. Secondly we pass an Observer object, which we pass as an annonymous inner class. We create a new Observer
         * of type <List<Note>>.
         *
         * Note that creating the new Observer object will generate the below onChanged() callback method. This method will be triggered
         * whenever the data in our LiveData object (i.e. our Notes table in the database) changes. It gets passed our List of Note objects.
         *
         * This is where we will update the RecyclerView. Again, the onChanged() method will ONLY
         * be called if this Activity is in the foreground. If we, for example, rotate the device and the Activity is destroyed,
         * this method will no longer hold a reference to that Activity.
         *
         */
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {

                /**
                 * By default, the List in our NoteAdapter adapter is empty so we update it in onChanged().
                 * Note that when we use the NoteAdapter adapter in onChanged(), it automatically changes our
                 * NoteAdapter to final, because we're accessing it from an inner class. This is okay.
                 *
                 * Call the setNotes() method we created in NoteAdapter and pass it the notes List of Note objects.
                 *
                 * So every time onChange() is triggered, which should be every time the data in the Notes
                 * table changes, our adapter will be updated with this new list of Note objects and refresh its status.
                 * 
                 */
                adapter.submitList(notes);
            }
        });

        /**
         * Set up a new ItemTouchHelper class to handle swiping to delete notes.
         *
         * We pass it a new ItemTouchHelper.SimpleCallback method call and pass this the following parameters:
         * 0 for drag-and-drop, which we're not using here.
         *
         * Then we pass the directions we want to implement. We're going to say swiping left and right will delete a note.
         *
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            /**
             * onMove() method.
             *
             * @param recyclerView
             * @param viewHolder
             * @param target
             * @return
             *
             * Only for drag-and-drop operations, we're not using it.
             *
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * onSwiped() method
             *
             * @param viewHolder
             * @param direction
             *
             * When an item is swiped left or right, this method is called.
             *
             * We pass it the ViewHolder we swiped and the direction (as an integer) we swiped.
             *
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                /**
                 * We need to get the position of our note in the ArrayList, but in our NoteDao class, we don't
                 * pass a position to our delete method. We pass a Note object directly. So we need a way of figuring out
                 * where in the ArrayList our Note object is before can delete it.
                 *
                 * We create a new method in NoteAdapter called getNoteAt() which will return that position.
                 *
                 *
                 */
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
               Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);


        /**
         * Here's where we implement the interface we created in NoteAdapter for the onClickListener for
         * listening for clicks on our notes in the ArrayList (for when we want to edit notes).
         *
         * This needs to be in onCreate().
         *
         * To implement the interface, we call the setOnItemClickListener() method on our adapter.
         * We need to pass an OnItemClickListener instance, which means we need to pass anything which
         * actually implements this interface.
         *
         * We could pass this (meaning this Activity, i.e. MainActivity) and then override the onClick()
         * method somewhere in our MainActivity class. But we cak also do it the way we often do it for
         * Button presses, i.e. by passing an annonymous inner class.
         *
         * So we pass a new NoteAdapter.OnItemClickListener() which will automatically generate a new
         * onItemClick() call back, within which we'll place the code for handling what happens when
         * we click.
         */

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override

            /**
             * Note that the onItemClick() callback accepts a Note object.
             *
             */
            public void onItemClick(Note note) {
                /**
                 * We want to open up the AddEditNoteActivity class when an item is pressed.
                 * To do this, we create a new Intent and pass it the current Activity, and the
                 * Activity we want to open.
                 *
                 */
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);

                /**
                 * We also need to send the ID of the note, since Room needs the primary key value to figure
                 * out which entry it's supposed to update (the primary key is the unique identifier).
                 *
                 * We just call the putExtra() method and pass it the constant we created in AddEditNoteActivity
                 * for the ID.
                 *
                 */
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());

                /**
                 * Now we need to populate the EditText fields of the AddEditNoteActivity with
                 * the relevant title, description and priority values for that note.
                 *
                 * We use the putExtra() method of the Intent class to do this, passing the
                 * constants for the title, description and priority as declared at the top of the
                 * AddEditNoteActivity class.
                 *
                 * We also pass a call to the getter methods for the note's
                 * title, description and priority. Note that the note we call these methods on is the note
                 * we got passed in our onItemClick() above, i.e. the note we actually clicked in our RecyclerView.
                 * Since this is the note we want to update, these are the values we're sending over.
                 *
                 *
                 */

                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());

                /**
                 * With all the Intent data put in, we now call startActivityForResult() and pass it the intent
                 * object, and the EDIT_NOTE_REQUEST flag as we want to go to this activity for editing, NOT adding.
                 * We'd also get a wrong callback if we put in the wrong request here.
                 *
                 * Strictly speaking, we don't bneed the EXTRA_ID in the Intent extras as we do the update operation
                 * in our MainActivity, but we still need to pass it in order to accept it back from AddEditNoteActivity
                 * to MainActivity in onActivityResult().
                 */
                startActivityForResult(intent, EDIT_NOTE_REQUEST);

            }
        });
    }


    /**
     * onActivityResult()
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * Called when an Activity you launched exits, in this case AddEditNoteActivity,
     * this method gives us the requestCode we started it with, the resultCode it returned, and any additional data from it.
     *
     */
        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If the REQUEST_CODE is equal to the ADD_NOTE_REQUEST (this is how we know which request we're handling)
         * and the RESULT_CODE is OK (RESULT_CODE should be okay from AddEditNoteActivity after we take our input if nothing
         * has gone wrong), then we extract the extras sent over from the AddNotes Activity.
         *
         * Note that we call getStringExtra() on the title and description on the data Intent, passing it the class name AddEditNoteActivity
         * and the .EXTRA_TITLE and .EXTRA_DESCRIPTION
         *
         */
            if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
                String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);

                /**
                 * We also want to get the integer value from the intent but we need to use getIntExtra() on this
                 * as it's an int. Note that integer values are not nullable so we have to pass a default value, in case
                 * this extra is missing. This can't happen in our example as we're setting a value for the number picker from
                 * 1 to 10 so it's always going to have a value, but we still need to pass one. You can choose any default value you like; we're
                 * using 1 here.
                 *
                 */
                int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                /**
                 * Now that we have the data extracted from the intent, we can put it in the database.
                 *
                 * We create a new Note object, note, and pass it the title, descrtiption and prioroity values we obtained above.
                 *
                 */
                Note note = new Note(title, description, priority);

                /**
                 * Then we call the insert() method on our noteViewModel object and pass it our note object.
                 *
                 */
                noteViewModel.insert(note);

                /**
                 * Pop up a Toast message to let the user know the note was added successfully.
                 *
                 */

                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();

            } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
                int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

                if (id == -1) {
                    Toast.makeText(this, "Note cannot be updated!", Toast.LENGTH_SHORT).show();
                    return;
                }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this, "Note successfully updated!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted!", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
