package bus;

import dao.CrudDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import model.CrudRow;
import model.LookupOption;
import model.ModuleDefinition;
import model.ModuleDefinition.Field;

public class CrudBUS {
    private final CrudDAO crudDAO = new CrudDAO();

    public List<CrudRow> getAll(ModuleDefinition definition) throws SQLException {
        return crudDAO.findAll(definition);
    }

    public List<LookupOption> getOptions(ModuleDefinition definition, Field field) throws SQLException {
        return crudDAO.findOptions(definition, field);
    }

    public void create(ModuleDefinition definition, Map<Field, Object> values, byte[] photo,
            boolean photoColumnAvailable) throws SQLException {
        crudDAO.insert(definition, values, photo, photoColumnAvailable);
    }

    public void update(ModuleDefinition definition, Object id, Map<Field, Object> values,
            byte[] photo, boolean photoChanged) throws SQLException {
        crudDAO.update(definition, id, values, photo, photoChanged);
    }

    public int delete(ModuleDefinition definition, Object id) throws SQLException {
        return crudDAO.delete(definition, id);
    }

    public int completeMedicalRecord(Object recordId) throws SQLException {
        return crudDAO.completeMedicalRecord(recordId);
    }

    public boolean hasPhotoColumn(ModuleDefinition definition) throws SQLException {
        return crudDAO.hasPhotoColumn(definition);
    }

    public byte[] getPhoto(ModuleDefinition definition, Object id) throws SQLException {
        return crudDAO.findPhoto(definition, id);
    }
}
