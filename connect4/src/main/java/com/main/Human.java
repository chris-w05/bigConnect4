package com.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Human extends Player {

    public int nextColumn(Board board) {
        System.out.println("Enter a column to place a piece in");
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        int output =0;
        try {
            output = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

}
