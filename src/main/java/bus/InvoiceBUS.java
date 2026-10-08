package bus;

import dao.InvoiceDAO;
import java.sql.SQLException;
import java.util.List;
import model.InvoicePrintData;
import model.InvoiceRecordOption;
import model.InvoiceSummary;

public class InvoiceBUS {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public List<InvoiceSummary> getInvoices() throws SQLException {
        return invoiceDAO.findInvoices();
    }

    public List<InvoiceRecordOption> getCandidates() throws SQLException {
        return invoiceDAO.findInvoiceCandidates();
    }

    public void create(int recordId, String paymentMethod) throws SQLException {
        if (recordId <= 0 || paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn phiếu khám và phương thức thanh toán.");
        }
        invoiceDAO.createInvoice(recordId, paymentMethod.trim());
    }

    public void markPaid(int invoiceId, String paymentMethod) throws SQLException {
        if (invoiceId <= 0 || paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn hóa đơn và phương thức thanh toán.");
        }
        invoiceDAO.markPaid(invoiceId, paymentMethod.trim());
    }

    public InvoicePrintData getPrintData(int invoiceId) throws SQLException {
        if (invoiceId <= 0) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }
        return invoiceDAO.findPrintData(invoiceId);
    }
}
