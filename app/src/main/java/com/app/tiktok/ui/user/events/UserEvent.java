package com.app.tiktok.ui.user.events;

import com.app.tiktok.model.User;
import com.app.tiktok.ui.user.DataItem;

import java.util.List;

//Below classes are for the purposes of using a multiple item type RecyclerView
public class UserEvent extends DataItem {
    public String eventTitle;
    public String timestamp;
    public List<User> members;
    public String url;

    public UserEvent(String eventTitle, String timestamp, List<User> members, String url){
        this.id = Long.MAX_VALUE;
        this.eventTitle = eventTitle;
        this.timestamp = timestamp;
        this.members = members;
        this.url = url;
    }
}