package com.example.android.architectureexample;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * AddEditNoteActivity.
 */
public class AddEditNoteActivity extends AppCompatActivity {


    public static final String TAG = "AddEditNoteActivity";
    /**
     * We need two EditText fields for the note title and description and one NumberPicker for the
     * priority.
     */

    public static final String EXTRA_ID =
            "com.example.android.architectureexample.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.android.architectureexample.EXTA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.android.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.example.android.architectureexample.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        /**
         * Get the references for the EditText fields and the NumberPicker from the XML file.
         */
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        /**
         * Set the minimum and maximum values for the NumberPicker.
         */
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        /**
         * Call the getSupportActionBar() method to get a reference for the Activity's ActionBar.
         *
         * We also call setHomeAsUpIndicator() which sets an alternate drawable to display next to the icon/logo/title when DISPLAY_HOME_AS_UP is enabled.
         * This can be useful if you are using this mode to display an alternate selection for up navigation, such as a sliding drawer.
         *
         * Here, we're using the Close icon from the drawable folder.
         *
         * We also call setTitle() to add a title for the ActionBar.
         *
         */
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        /**
         * We want to set the title of the Activity to Edit Note if we're Editing. Wouldn't make much sense to have
         * Add Note on the Edit Note screen.
         *
         */

        Intent intent = getIntent();

        /**
         * The following will only be executed if the intent extra is an ID, which will be the case
         * for updating a note.
         */
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }
    }


    /**
     * saveNote() method.
     *
     * This method gets called when we hit the Save button on the AddNote Activity.
     *
     */
    private void saveNote() {

        /**
         * The first thing we want to do is get the text from the title and description EditText fields.
         * We create two String variables and call the getText().toString() method to get the text.
         *
         */
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        /**
         * We also create an integer variable and call the getValue() method on the numberPickerPriority object.
         *
         */
        int priority = numberPickerPriority.getValue();

        /**
         * While the NumberPicker will ALWAYS have a value from 1 to 10, the title and description fields might be empty.
         * If either of them IS empty, we don't want to accept this, so we create an if() statement and use the trim().isEmpty()
         * methods.
         *
         * trim() removes the empty spaces at the beginning and end of the input (otherwise we could just type in empty spaces
         * and it wouldn't recognize the input as empty), then isEmpty() checks to see if it's empty.
         *
         * If so we pop up a Toast message, saying that both fields are needed.
         *
         */

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description!", Toast.LENGTH_SHORT).show();
            /**
             * If the above is true, we call return and exit this method, so we don't execute what comes after this.
             *
             */
            return;
        }

        /**
         * Insert the note into the database.
         *
         * One way to do this would be to create a ViewModel variable in this Activity (like we did in MainActivity) and use it to do our database operations.
         *
         *
         */

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id  = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }





    /**
     * onCreateOptionsMenu()
     *
     * @param menu object
     * @return boolean true
     *
     * onCreateOptionsMenu() initialize the contents of the Activity's standard options menu. You should place your menu items into menu.
     *
     * This is only called once, the first time the options menu is displayed. To update the menu every time it is displayed, see onPrepareOptionsMenu(Menu).
     *
     * When you add items to the menu, you can implement the Activity's onOptionsItemSelected(MenuItem) method to handle them there.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /**
         * Create a new MenuInflater object, menuInflater and call getMenuInflater() method, which teturns a MenuInflater with this context,
         * assigning it to menuInflater.
         */
        MenuInflater menuInflater = getMenuInflater();

        /**
         * Now we call the inflate() method on the menuInflater object, passing it the id of the menu we created, add_note_menu.xml, and the
         * menu object which gets passed. This tells the system to use our menu as the menu of this Activity.
         *
         */
        menuInflater.inflate(R.menu.add_note_menu, menu);

        /**
         * We return true if we want to display the menu, false if we don't.
         *
         */
        return true;
    }

    /**
     * onOptionsItemSelected
     *
     * @param item
     * @return Here's where we handle clicks on our menu items.
     *
     * We pass a MenuItem object, menuItem.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /**
         * Even though we only have one item in the menu, we'll set up a switch statement.
         *
         * We check for the value of the MenuItem item by calling getItemId() which returns the item's ID.
         *
         */
        switch (item.getItemId()) {

            /**
             * And if the Save button is pressed, we call the saveNote() method to save the note to the database.
             *
             */
            case R.id.save_note:
                saveNote();
                Toast.makeText(this, "Saving note....", Toast.LENGTH_SHORT).show();
                /**
                 * If this WAS clicked, we should return true.
                 *
                 */
                return true;

            /**
             * Our default case will use the return super.onOptionsItemSelected(item) option.
             *
             */
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
