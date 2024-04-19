package com.example.algorithim_project1;
public class Adjacent {
    City city;

    @Override
    public String toString() {
        return "Adjacent{" +
                "city=" + city +
                ", petrol=" + petrol +
                ", hotel=" + hotel +
                '}';
    }

    int petrol;
    int hotel;

    public Adjacent(City found, int petroll, int hotell) {
        city = found;
        petrol = petroll;
        hotel = hotell;

    }

    public City getCity() {
        return city;
    }

    public int getPetrol() {
        return petrol;
    }
}

