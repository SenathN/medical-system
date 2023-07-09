/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import network.ServerNetworkComponent;

/**
 *
 * @author Sen
 */
public class AppMain {

    private static ServerNetworkComponent networkInstance;

    public static void main(String[] args) {
        try {
            System.out.println("MedicalSystem Running");
            jframes.MainInterface.main(args);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

}
