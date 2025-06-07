package com.example.gametracker;

public class Message {
    private String typ;
    private String addon;
    private String message;

    public Message(String typ, String addon, String message) {
        this.typ = typ;
        this.addon = addon;
        this.message = message;
    }

    public String getTyp() {
        return typ;
    }

    public String getAddon() {
        return addon;
    }

    public String getMessage() {
        return message;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
