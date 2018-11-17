package com.example.android.architectureexample;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Our Note model, where we define our table, its name and its columns.
 *
 * We use the @Entity annotation and set the table_name to note_table.
 *
 * By default, Room will just call the table the same thing as the class, in this case, Note.
 * We want something a little more inline with SQL naming conventions though.
 *
 */
@Entity(tableName = "note_table")
public class Note {

    /**
     * The following four variables represent the columns in our table.
     *
     * We use the @Primary annotation for the id field to specify that it's a the Primary Key field.
     * We set the auto-increment property to true.
     *
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String description;

    private int priority;

    /**
     *  Constructor for Note object. We're not passing it id as Room will generate that automatically.
     */
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    /**
     * We need a setter method for id as it's the only value we're not passing to the constructor. Room will
     * use this method to set an id on a Note object. (Why no setters for the others? Is it because the user
     * will be setting those themselves when they add entries?)
     *
     */

    public void setId(int id) {
        this.id = id;
    }

    /**
     * We need Getter methods for our variables as we've made them private. This is something we should
     * do as it's inline with one of the pillars of O.O., i.e. encapsulation.
     *
     */

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
