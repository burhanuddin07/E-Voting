package com.husain.e_voting;

public class User {

    String ID;
    String Name;
    String Aadhar;
    String Phone;
    String Address;
    String Gender;
    Integer Age;

    public User()
    {

    }

    public User(String ID, String name, String aadhar, String phone, String address, String gender, Integer age) {
        this.ID = ID;
        Name = name;
        Aadhar = aadhar;
        Phone = phone;
        Address = address;
        Gender = gender;
        Age = age;
    }

}