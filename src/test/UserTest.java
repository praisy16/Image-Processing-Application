
//Test case - Register in SRA

package com.emulator.whatsthatdog;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    //create a user instance
    User newUser = new User ("randomUser", "20", "randomuser99@gmail.com");

    //tests email
    @Test
    public void testGetEmail(){
        String expected =  "randomuser99@gmail.com";
        String actual = newUser.getEmail();
        assertTrue (expected.equals(actual));
    }

    //tests user age
    @Test
    public void testGetAge(){
        String expected =  "20";
        String actual = newUser.getAge();
        assertTrue (expected.equals(actual));
    }

    //tests username
    @Test
    public void testUsername(){
        String expected =  "randomUser";
        String actual = newUser.getUsername();
        assertTrue (expected.equals(actual));
    }
}