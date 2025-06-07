package com.example.gametracker;

import java.util.Date;
import java.util.List;

public class Game {
    private String title;
    private String desc;
    private String platform;
    private String mode;
    private Date relaseDate;
    private List<String> genre;
    private String producer;
    private String publisher ;
    private String background;
    private String front;
    private String trailer;

    private boolean accepted;

    public Game() {
    }

    public Game(String title, String desc, String platform, String mode, Date relaseDate, List<String> genre, String producer, String publisher, String background, String front, String trailer) {
        this.title = title;
        this.desc = desc;
        this.platform = platform;
        this.mode = mode;
        this.relaseDate = relaseDate;
        this.genre = genre;
        this.producer = producer;
        this.publisher = publisher;
        this.background = background;
        this.front = front;
        this.trailer = trailer;
        this.accepted=false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getRelaseDate() {
        return relaseDate;
    }

    public void setRelaseDate(Date relaseDate) {
        this.relaseDate=relaseDate;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
