package bus;

import dao.ReminderDAO;
import java.sql.SQLException;
import java.util.List;
import model.ReminderEntry;

public class ReminderBUS {
    private final ReminderDAO reminderDAO = new ReminderDAO();

    public List<ReminderEntry> getDueReminders() throws SQLException {
        return reminderDAO.findDueReminders();
    }
}
