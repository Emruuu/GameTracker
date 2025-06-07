package com.example.gametracker;

import java.util.Arrays;
import java.util.List;

public class User{
    private String Nick;
    private String Email;
    private String Avatar;
    private String MyList;
    private String FriendList;
    private Boolean Ban;
    private Integer UserRang;
    private List<String> FavList;

    public User() {
    }

    public User(String nick, String email) {
        Nick = nick;
        Email = email;
        Avatar = "avatar1.png";
        MyList = "";
        FriendList = "";
        Ban=false;
        UserRang=1;
        FavList= Arrays.asList("".split(" "));
    }

    public User(String nick, String email, String avatar, String myList ,String friendList,Integer userRang) {
        Nick = nick;
        Email = email;
        Avatar = avatar;
        MyList = myList;
        FriendList = friendList;
        Ban=false;
        UserRang=userRang;
        FavList= Arrays.asList("".split(" "));
    }

    public String getNick() {
        return Nick;
    }

    public void setNick(String nick) {
        Nick = nick;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getMyList() {
        return MyList;
    }

    public void setMyList(String myList) {
        MyList = myList;
    }

    public String getFriendList() {
        return FriendList;
    }

    public void setFriendList(String friendList) {
        FriendList = friendList;
    }

    public Boolean getBan() {
        return Ban;
    }

    public void setBan(Boolean ban) {
        Ban = ban;
    }

    public Integer getUserRang() {
        return UserRang;
    }

    public void setUserRang(Integer userRang) {
        UserRang = userRang;
    }

    public List<String> getFavList() {
        return FavList;
    }

    public void setFavList(List<String> favList) {
        FavList = favList;
    }
}
