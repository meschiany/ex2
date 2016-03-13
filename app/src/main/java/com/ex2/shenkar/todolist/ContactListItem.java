package com.ex2.shenkar.todolist;

/**
 * Created by shnizle on 1/14/2016.
 */
public class ContactListItem extends Contact{

    private boolean selected = false;

    public ContactListItem(String email, String mobile, String name) {
        super(email, mobile, name);
    }
    public void setSelected(boolean selected){
        this.selected = selected;
    }
    public boolean getSelected(){
        return this.selected;
    }
}
