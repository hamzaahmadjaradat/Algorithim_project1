package com.example.algorithim_project1;
import java.util.ArrayList;
import java.util.List;

public class City {
    String name;


    List<Adjacent> adjacentList = new ArrayList<>();
    int hotelCost;
    public City(String CityName) {
        name = CityName;
    }

    public String getName() {
        return name;
    }

    public int getHotelCost() {
        return hotelCost;
    }

    public List<Adjacent> getAdjacentList() {
        return adjacentList;
    }
    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", adjacentList=" + adjacentList+
                ", hotelCost=" + hotelCost +
                '}';
    }

}

