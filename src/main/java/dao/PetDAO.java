/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import model.Pet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public List<Pet> getAllPets() throws SQLException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM Pet";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Pet(
                    rs.getInt("pet_id"),
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("species"),
                    rs.getString("breed"),
                    rs.getInt("age")
                ));
            }
        }
        return list;
    }

    public boolean addPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO Pet (customer_id, name, species, breed, age) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pet.getCustomerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setInt(5, pet.getAge());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE Pet SET customer_id = ?, name = ?, species = ?, breed = ?, age = ? WHERE pet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pet.getCustomerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setInt(5, pet.getAge());
            ps.setInt(6, pet.getPetId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePet(int petId) throws SQLException {
        String sql = "DELETE FROM Pet WHERE pet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petId);
            return ps.executeUpdate() > 0;
        }
    }
}