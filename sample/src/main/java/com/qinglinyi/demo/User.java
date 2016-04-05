package com.qinglinyi.demo;

import java.io.Serializable;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
public class User implements Serializable{
    public int age;
    public String name;
    public Person person;

    public User(String name) {
        this.name = name;
        person = new Person();
    }
}
