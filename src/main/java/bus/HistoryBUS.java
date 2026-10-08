package bus;

import dao.HistoryDAO;
import java.sql.SQLException;
import java.util.List;
import model.HistoryEntry;

public class HistoryBUS {
    private final HistoryDAO historyDAO = new HistoryDAO();

    public List<HistoryEntry> search(String term) throws SQLException {
        return historyDAO.search(term);
    }
}
