package com.jettra.report.exporter;

import com.jettra.report.Report;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JettraPdfExporter {

    private static class PdfObject {
        int id;
        long offset;
        String content;

        PdfObject(int id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    private static class Cursor {
        float x = 0;
        float y = 0;

        void move(StringBuilder sb, float dx, float dy) {
            sb.append(dx).append(" ").append(dy).append(" Td\n");
            x += dx;
            y += dy;
        }

        void moveTo(StringBuilder sb, float targetX, float targetY) {
            move(sb, targetX - x, targetY - y);
        }
    }

    public static void export(Report report, String path) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            List<PdfObject> objects = new ArrayList<>();
            StringBuilder stream = new StringBuilder();
            Cursor cursor = new Cursor();

            stream.append("BT\n");
            
            // Header: Date and Time
            stream.append("/F1 10 Tf\n");
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            cursor.moveTo(stream, 430, 810);
            stream.append("(").append(escapePdf("Fecha: " + now)).append(") Tj\n");

            // Footer: Page Number
            cursor.moveTo(stream, 280, 30);
            stream.append("(").append(escapePdf("Página 1")).append(") Tj\n");

            // Body Content
            stream.append("/F1 12 Tf\n");
            cursor.moveTo(stream, 50, 780);

            // Header Elements
            for (Report.ReportElement el : report.getHeader().getElements()) {
                addPdfElement(stream, cursor, el, null, report.getData());
            }

            // Detail Elements
            for (Report.ReportElement el : report.getDetail().getElements()) {
                addPdfElement(stream, cursor, el, null, report.getData());
            }

            // Summary Section
            for (Report.ReportElement el : report.getSummary().getElements()) {
                addPdfElement(stream, cursor, el, null, report.getData());
            }

            // Footer Section
            for (Report.ReportElement el : report.getFooter().getElements()) {
                addPdfElement(stream, cursor, el, null, report.getData());
            }

            stream.append("ET\n");

            String streamContent = stream.toString();
            
            int nextId = 1;
            PdfObject catalog = new PdfObject(nextId++, "<< /Type /Catalog /Pages " + (nextId) + " 0 R >>");
            objects.add(catalog);
            
            PdfObject pages = new PdfObject(nextId++, "<< /Type /Pages /Kids [ " + (nextId) + " 0 R ] /Count 1 >>");
            objects.add(pages);
            
            PdfObject page = new PdfObject(nextId++, "<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 " + (nextId + 1) + " 0 R >> >> /Contents " + (nextId) + " 0 R /MediaBox [ 0 0 595 842 ] >>");
            objects.add(page);
            
            PdfObject contents = new PdfObject(nextId++, "<< /Length " + streamContent.length() + " >>\nstream\n" + streamContent + "endstream");
            objects.add(contents);
            
            PdfObject font = new PdfObject(nextId++, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
            objects.add(font);

            long currentOffset = 0;
            String header = "%PDF-1.4\n";
            fos.write(header.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            currentOffset += header.length();

            for (PdfObject obj : objects) {
                obj.offset = currentOffset;
                String objHeader = obj.id + " 0 obj\n";
                String objFooter = "\nendobj\n";
                fos.write(objHeader.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                fos.write(obj.content.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                fos.write(objFooter.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
                currentOffset += objHeader.length() + obj.content.length() + objFooter.length();
            }

            long xrefOffset = currentOffset;
            StringBuilder xref = new StringBuilder();
            xref.append("xref\n0 ").append(objects.size() + 1).append("\n0000000000 65535 f \n");
            for (PdfObject obj : objects) {
                xref.append(String.format("%010d 00000 n \n", obj.offset));
            }
            fos.write(xref.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            
            String trailer = "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF";
            fos.write(trailer.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addPdfElement(StringBuilder stream, Cursor cursor, Report.ReportElement el, Object row, List<?> data) {
        if (el instanceof Report.TextElement tel) {
            stream.append("(").append(escapePdf(tel.getExpression())).append(") Tj\n");
            cursor.move(stream, 0, -15);
        } else if (el instanceof Report.Table table) {
            float startX = cursor.x;
            // Table Header
            stream.append("/F1 10 Tf\n");
            for (Report.Column col : table.getColumns()) {
                stream.append("(").append(escapePdf(col.getHeader())).append(") Tj\n");
                cursor.move(stream, 100, 0);
            }
            cursor.moveTo(stream, startX, cursor.y - 15);
            
            // Table Data
            stream.append("/F1 10 Tf\n");
            if (data != null) {
                for (Object item : data) {
                    for (Report.Column col : table.getColumns()) {
                        Object val = getFieldValue(item, col.getDetailExpression());
                        stream.append("(").append(escapePdf(val != null ? val.toString() : "")).append(") Tj\n");
                        cursor.move(stream, 100, 0);
                    }
                    cursor.moveTo(stream, startX, cursor.y - 12);
                    // Basic Page Break protection (very simple)
                    if (cursor.y < 50) break; 
                }
            }
            cursor.move(stream, 0, -10); // Spacing after table
        }
    }

    private static String escapePdf(String s) {
        if (s == null) return "";
        return s.replace("(", "\\(").replace(")", "\\)").replace("\\", "\\\\");
    }

    private static Object getFieldValue(Object obj, String expression) {
        try {
            Field field = obj.getClass().getDeclaredField(expression);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return expression;
        }
    }
}
