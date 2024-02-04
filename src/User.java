package com.emulator.whatsthatdog;

public class User {

    public String username, age, email;

    public User(){

    }

    public User(String username, String age, String email){
        this.username = username;
        this.age = age;
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail (String email){
        this.email = email;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge (String age){
        this.age = age;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername (String username){
        this.username = username;
    }
}
