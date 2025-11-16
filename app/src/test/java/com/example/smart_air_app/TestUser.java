package com.example.smart_air_app;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.smart_air_app.user_classes.User;

public class TestUser {
    @Test
    public void TestConstructorParent() {
        User user = new User("parent", "Joe", "Random", 4);
        assertEquals("parent", user.getType());
        assertEquals("Joe", user.getFirstName());
        assertEquals("Random", user.getLastName());
        assertEquals(4, user.getUserID());
    }

    @Test
    public void TestConstructorDoctor() {
        User user = new User("doctor", "Joe", "Random", 4);
        assertEquals("doctor", user.getType());
        assertEquals("Joe", user.getFirstName());
        assertEquals("Random", user.getLastName());
        assertEquals(4, user.getUserID());
    }

    @Test
    public void TestConstructorIllegalTypeChild() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("child", "Joe", "Random", 4);
        });
        assertEquals("Initialize a child object if type is 'child'", exception.getMessage());
    }

    @Test
    public void TestConstrctorIllegalTypeUnspecified() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("alien", "Joe", "Random", 4);
        });
        assertEquals("Invalid user type: alien", exception.getMessage());
    }

    @Test
    public void TestConstrctorEmptyFirstName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("doctor", "", "Random", 4);
        });
        assertEquals("Invalid first name: can not be empty", exception.getMessage());
    }

    @Test
    public void TestConstrctorEmptyLastName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("doctor", "Joe", "", 4);
        });
        assertEquals("Invalid last name: can not be empty", exception.getMessage());
    }

    @Test
    public void TestSetFirstNameValid() {
        User user = new User("doctor", "Joe", "Random", 4);
        user.setFirstName("Mark");
        assertEquals("Mark", user.getFirstName());
    }

    @Test
    public void TestSetFirstNameInvalid() {
        User user = new User("doctor", "Joe", "Random", 4);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            user.setFirstName("");
        });
        assertEquals("Invalid first name: can not be empty", exception.getMessage());
    }

    @Test
    public void TestSetLastNameValid() {
        User user = new User("doctor", "Joe", "Random", 4);
        user.setLastName("Marcus");
        assertEquals("Marcus", user.getLastName());
    }

    @Test
    public void TestSetLastNameInvalid() {
        User user = new User("doctor", "Joe", "Random", 4);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            user.setLastName("");
        });
        assertEquals("Invalid last name: can not be empty", exception.getMessage());
    }

    @Test
    public void TestGetFirstName() {
        User user = new User("doctor", "Joe", "Random", 4);
        assertEquals("Joe", user.getFirstName());
    }
    @Test
    public void TestGetLastName() {
        User user = new User("doctor", "Joe", "Random", 4);
        assertEquals("Random", user.getLastName());
    }

    @Test
    public void TestGetType() {
        User user = new User("doctor", "Joe", "Random", 4);
        assertEquals("doctor", user.getType());
    }

    @Test
    public void TestGetUserID() {
        User user = new User("doctor", "Joe", "Random", 4);
        assertEquals(4, user.getUserID());
    }
}