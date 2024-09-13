package com.nistores.awesomeurch.nistores.folders.helpers;

/**
 * Created by Awesome Urch on 06/08/2018.
 * Chat binder helper class
 */

public class Chat {
    private String fto;
    private String firstname;
    private String surname;
    private String picture;
    private String last_message;
    private String comment_id;
    private String comment;
    private String comment_user;
    private String comment_date;

    public String getFto(){
        return fto;
    }
    public String getFirstname(){
        return firstname;
    }
    public String getSurname(){
        return surname;
    }
    public String getPicture(){
        return picture;
    }
    public String getLast_message(){
        return last_message;
    }

    public String getComment_id(){ return comment_id; }
    public String getComment(){ return comment; }
    public String getComment_user(){ return comment_user; }
    public String getComment_date(){ return comment_date; }

    public void setFto(String fto){
        this.fto = fto;
    }
    public void setFirstname(String firstname){
        this.firstname = firstname;
    }
    public void setSurname(String surname){
        this.surname = surname;
    }
    public void setPicture(String picture){
        this.picture = picture;
    }
    public void setLast_message(String last_message){
        this.last_message = last_message;
    }


}
