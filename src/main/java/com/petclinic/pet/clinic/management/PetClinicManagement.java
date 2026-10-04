/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.petclinic.pet.clinic.management;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import view.LoginFrame;

/**
 *
 * @author Administrator
 */
public class PetClinicManagement {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
