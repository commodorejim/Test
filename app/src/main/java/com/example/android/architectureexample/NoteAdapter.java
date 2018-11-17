package com.example.android.architectureexample;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Our custom adapter is called NoteAdapter. It MUST extend the RecyclerView.Adapter class.
 *
 * Note how we pass our inner class, NoteAdapter.NoteHolder (outer class followed by inner class),
 * as an argument to the Adapter as the datatype.
 *
 */
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {


    /**
     * Let's make a List of Note objects which will contain our notes.
     *
     * This MUST be a new ArrayList<>. If it weren't a new ArrayList, this variable would be null BEFORE we got our first LiveData update.
     * If it is null, we'd need to make sure we never call methods on a null object in the adapter. Normally, we'd have to check
     * to ensure objects aren't null, but we'll make life easier by just creating it as a new ArrayList.
     *
     *  private List<Note> notes = new ArrayList<>();
     *
     */

    private OnItemClickListener listener;

    /**
     * Constructor
     * @param
     *
     */
    protected NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(Note oldItem, Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Note oldItem, Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getPriority() == newItem.getPriority();
        }
    };

    /**
     * Now we auto-generate the three override methods we need for our RecyclerView adapter.
     *
     * Note how passing NoteHolder as a dataype to the RecyclerView.Adapter<> above means that the methods
     * created below will automatically use NoteHolder.
     *
     */


    /**
     * onCreateView() method.
     *
     * This is where we try to create a NoteHolder, the layout we want to use for the list items in
     * our RecyclerView.
     *
     * Note: We MUST override onCreateViewHolder(), onBindViewHolder() and getItemCount() in a RecyclerView adapter.
     * @param parent
     * @param viewType
     *
     * @return view
     *
     */
    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /**
         * Create a View object, view, and call the LayoutInflater.from() method.
         * We pass it a context, which we get by calling parent.getContext(), the parent ViewGroup (i.e.
         * the RecyclerView itself and false for the attachToRoot parameter.
         *
         * Note how we used parent.getContext() to get the context as the RecyclerView is the parent
         * ViewGroup for all the NoteHolders.
         *
         */
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        /**
         * Now return a new NoteHolder item, passing it itemView as an argument.
         */
        return new NoteHolder(itemView);
    }

    /**
     * onBindViewHolder()
     * @param holder
     * @param position
     *
     * Here's where we handle getting the data FROM our single Note objects into the views of
     * each single NoteHolder. So we want to get the title into the title TextView, the description into
     * the description TextView and the priority into the Priority TextView.
     *
     */
    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        /**
         * We'll create a Note object, currentNote, which will represent the current Note object
         * we're getting the values for. We call the get() method and pass it the integer position
         * which is passed into onBindViewHolder(). This gets the position of the current Note object
         * in the ArrayList.
         */
        Note currentNote = getItem(position);

        /**
         * Now we call the NoteHolder object, holder, which also gets passed into onBindViewHolder()
         * and call the setText() method on it. We pass it the currentNote and call the getter method
         * getTitle() to get that Note object's title.
         *
         * We do the same for the Note description.
         */
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());

        /**
         * Because the priority for each Note is an integer, not a String, we need to convert that
         * integer value to a String, since we can't pass an integer to a TextView directly.
         *
         * To convert the integer to a String, we call the String.valueOf() method on currentNote.getPriority().
         * This will turn the integer into a String which we can display in the TextView.
         *
         */
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));

    }

    /**
     * getItemCount()
     *
     * This method will return how many items we want to display in our RecyclerView.
     * We always want to display as many items as there are Note objects in our ArrayList.
     * We simply return notes.size() which will return this value for us.
     * @return
     *
     *     @Override
     *     public int getItemCount() {
     *         return notes.size();
     *     }
     *
     *
     */


    /**
     * setNotes()
     * @param notes
     *
     * In MainActivity, we're observing the LiveData and in the onChanged() call back we are passed a List of Note objects.
     * This method will get that List of Notes into our RecyclerView.
     */


    public Note getNoteAt(int position){
        return getItem(position);
    }


    /**
     * This is our ViewHolder class, which must extend the RecyclerView.ViewHolder class.
     *
     * Remember, ViewHolders literally "hold" views in memory. We'll be passing this class as an argument
     * to the RecyclerView.Adapter<> in the main class definition at the top of this file.
     *
     * We need three TextViews for the Note items we'll be displaying.
     *
     */

    class NoteHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;


        /**
         * NoteHolder constructor
         * @param itemView
         *
         * We assign the three TextViews we created above. We pass an itemView (the card irself)
         * as an argument to the constructor.
         *
         *
         */
        public NoteHolder(View itemView) {
            super(itemView);

            /**
             * Get references for our three TextView from the note_item.xml layout file.
             *
             */
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            /**
             * We want to set an onClickListener on the whole item view, i.e. the card view for each
             * note in the list.
             *
             * Call the setOnClickListener() method on our NoteHolder itemView object.
             * We also need to get the position of the note we're clicking on so that we know which
             * note to open up when it's clicked.
             *
             * We'll do this by creating an integer variable, position, and assiging it to the
             * results of getAdapterPosition() which returns the Adapter position of the item represented
             * by this ViewHolder.
             *
             * So in a nutshell, we click on a card view, we get the adapter position of the clicked item
             * and then we call onItemClick() on our OnItemClickListener, which we'll implement in MainActivity
             * later, and then we pass the note at that position to it.
             *
             * This way we get the clicked event together with the clicked note to MainActivity.
             *
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    /**
                     * If the listener is NOT null, because there's no guarantee we ever actually
                     * called setOnClickListener() and it MIGHT be null as a result, we should check for this
                     * to avoid a crash due to a null object reference.
                     *
                     * We also check to make sure we're not clicking on an item with an invalid position (RecyclerView.NO_POSITION),
                     * which could happen if we delete an item but it's still in its delete animation.
                     *
                     * In this case, the position of this item would be NO_POSITION (-1). If we were to pass this to our notes ArrayList,
                     * we'd crash the app since -1 is an invalid position for an index in a list.
                     *
                     */
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }

                    /**
                     * Now call the onItemClick() method (our interface method) on the listener object
                     * and pass it our notes ArrayList, notes, and call the get() method on it, passing
                     * it the position we just obtained from getAdapterPosition().
                     *
                     */

                }
            });


        }
    }

    /**
     * We're going to implement an OnClickListener for the note items in the RecyclerView, so that we can
     * enter an Edit screen to edit the Note entries.
     *
     * To do this, we need an Interface, which we'll call OnItemClickListener. Note that this is in the NoteAdapter.
     *
     *
     */
    public interface OnItemClickListener{

        /**
         * OnItemClickListener interface
         * @param note
         *
         * We have one member method for our Interface, onItemClick. It accepts a Note object, note.
         * We don't provide the implementation for an interface method, we just declare the method. Whatever
         * implements this interface will implement its member method, onItemClick, as well.
         *
         * We can call methods from this adapter on the OnItemClickListener with ANOTHER fucking method, setOnClickListener().
         * These are interfaces. You knew they were going to be hard. Suck it up.
         *
         */
        void onItemClick(Note note);
    }

    /**
     *
     * setOnClickListener() method.
     * @param listener
     *
     * If you've worked with ListViews before, you should know that the ListView class provides similar
     * methods by default, although in the RecyclerView, we have to create them ourselves.
     *
     * We pass an OnItemClickListener interface, listener, as an argument to this method. It's important to
     * take the one with our own package name.
     *
     * We will later use this listener variable to call our onItemClick() method on it and this way we can forward
     * our Note object to whatever implements this interface. To do this we save our listener object in a member variable.
     *
     */
    public void setOnItemClickListener(OnItemClickListener listener) {

        /**
         * Assign our member variable listener to the listener we've been passed.
         *
         */
        this.listener = listener;

    }
}
