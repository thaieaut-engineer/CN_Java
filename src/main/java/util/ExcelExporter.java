package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public final class ExcelExporter {
    private ExcelExporter() {
    }

    public static void write(JTable table, Path destination) throws IOException {
        TableModel model = table.getModel();
        List<List<String>> rows = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        for (int column = 0; column < model.getColumnCount(); column++) {
            headers.add(model.getColumnName(column));
        }
        rows.add(headers);
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            List<String> values = new ArrayList<>();
            for (int column = 0; column < model.getColumnCount(); column++) {
                Object value = model.getValueAt(modelRow, column);
                String text = value == null ? "" : value.toString();
                if (text.length() > 32_767) {
                    throw new IOException("Ô " + (modelRow + 1) + ", " + (column + 1)
                            + " vượt giới hạn 32.767 ký tự của Excel.");
                }
                values.add(text);
            }
            rows.add(values);
        }

        Path parent = destination.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(destination),
                StandardCharsets.UTF_8)) {
            addEntry(zip, "[Content_Types].xml", contentTypes());
            addEntry(zip, "_rels/.rels", rootRelationships());
            addEntry(zip, "xl/workbook.xml", workbook());
            addEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelationships());
            addEntry(zip, "xl/worksheets/sheet1.xml", worksheet(rows));
        }
    }

    private static String worksheet(List<List<String>> rows) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
                .append("<sheetData>");
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            xml.append("<row r=\"").append(rowIndex + 1).append("\">");
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                xml.append("<c r=\"").append(columnName(columnIndex)).append(rowIndex + 1)
                        .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                        .append(escapeXml(row.get(columnIndex))).append("</t></is></c>");
            }
            xml.append("</row>");
        }
        return xml.append("</sheetData></worksheet>").toString();
    }

    private static String columnName(int index) {
        StringBuilder result = new StringBuilder();
        for (int value = index + 1; value > 0; value = (value - 1) / 26) {
            result.insert(0, (char) ('A' + (value - 1) % 26));
        }
        return result.toString();
    }

    private static String escapeXml(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (current == '&') escaped.append("&amp;");
            else if (current == '<') escaped.append("&lt;");
            else if (current == '>') escaped.append("&gt;");
            else if (current == '"') escaped.append("&quot;");
            else if (current == '\'') escaped.append("&apos;");
            else if (current == '\t' || current == '\n' || current == '\r' || current >= 0x20) {
                escaped.append(current);
            }
        }
        return escaped.toString();
    }

    private static String contentTypes() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "</Types>";
    }

    private static String rootRelationships() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String workbook() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"PetClinic\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>";
    }

    private static String workbookRelationships() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "</Relationships>";
    }

    private static void addEntry(ZipOutputStream zip, String name, String contents) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(contents.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
