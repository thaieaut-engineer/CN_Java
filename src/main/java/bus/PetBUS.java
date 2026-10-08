package bus;

import dao.PetDAO;
import java.sql.SQLException;
import java.util.List;
import model.Pet;

public class PetBUS {
    private final PetDAO petDAO = new PetDAO();

    public List<Pet> getAll() throws SQLException {
        return petDAO.getAllPets();
    }

    public void create(Pet pet) throws SQLException {
        validate(pet);
        if (!petDAO.addPet(pet)) {
            throw new SQLException("Không thể thêm thú cưng.");
        }
    }

    public void update(Pet pet) throws SQLException {
        validate(pet);
        if (pet.getPetId() <= 0) {
            throw new IllegalArgumentException("Mã thú cưng không hợp lệ.");
        }
        if (!petDAO.updatePet(pet)) {
            throw new SQLException("Không tìm thấy thú cưng cần cập nhật.");
        }
    }

    public void delete(int petId) throws SQLException {
        if (petId <= 0) {
            throw new IllegalArgumentException("Mã thú cưng không hợp lệ.");
        }
        if (!petDAO.deletePet(petId)) {
            throw new SQLException("Không thể xóa thú cưng.");
        }
    }

    private void validate(Pet pet) {
        if (pet == null || pet.getCustomerId() <= 0 || pet.getName() == null || pet.getName().isBlank()
                || pet.getName().trim().length() > 50 || pet.getAge() < 0) {
            throw new IllegalArgumentException("Chủ nuôi, tên và tuổi thú cưng phải hợp lệ.");
        }
    }
}
