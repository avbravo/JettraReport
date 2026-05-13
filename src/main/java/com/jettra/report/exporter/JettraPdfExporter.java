package com.jettra.report.exporter;

import com.jettra.report.Report;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
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

    public static void export(Report report, String path) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            List<PdfObject> objects = new ArrayList<>();
            StringBuilder stream = new StringBuilder();

            // Content Stream (simplified)
            stream.append("BT\n"); // Begin Text
            stream.append("/F1 12 Tf\n"); // Font F1, size 12
            stream.append("50 800 Td\n"); // Move to 50, 800

            // Title
            // stream.append("(").append(escapePdf(report.getTitle())).append(") Tj\n");
            // stream.append("0 -20 Td\n");

            // Header Elements
            for (Report.ReportElement el : report.getHeader().getElements()) {
                addPdfElement(stream, el, null, report.getData());
            }

            // Detail Elements
            for (Report.ReportElement el : report.getDetail().getElements()) {
                addPdfElement(stream, el, null, report.getData());
            }

            stream.append("ET\n"); // End Text

            String streamContent = stream.toString();
            
            // Build PDF Objects
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

            // Write PDF
            long currentOffset = 0;
            String header = "%PDF-1.4\n";
            fos.write(header.getBytes());
            currentOffset += header.length();

            for (PdfObject obj : objects) {
                obj.offset = currentOffset;
                String objHeader = obj.id + " 0 obj\n";
                String objFooter = "\nendobj\n";
                fos.write(objHeader.getBytes());
                fos.write(obj.content.getBytes());
                fos.write(objFooter.getBytes());
                currentOffset += objHeader.length() + obj.content.length() + objFooter.length();
            }

            long xrefOffset = currentOffset;
            StringBuilder xref = new StringBuilder();
            xref.append("xref\n");
            xref.append("0 ").append(objects.size() + 1).append("\n");
            xref.append("0000000000 65535 f \n");
            for (PdfObject obj : objects) {
                xref.append(String.format("%010d 00000 n \n", obj.offset));
            }
            
            fos.write(xref.toString().getBytes());
            
            String trailer = "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF";
            fos.write(trailer.getBytes());

        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addPdfElement(StringBuilder stream, Report.ReportElement el, Object row, List<?> data) {
        if (el instanceof Report.TextElement tel) {
            stream.append("(").append(escapePdf(tel.getExpression())).append(") Tj\n");
            stream.append("0 -15 Td\n");
        } else if (el instanceof Report.Table table) {
            // Very simplified table rendering in PDF stream
            for (Report.Column col : table.getColumns()) {
                stream.append("(").append(escapePdf(col.getHeader())).append(") Tj\n");
                stream.append("80 0 Td\n");
            }
            stream.append("-").append(table.getColumns().size() * 80).append(" -15 Td\n");
            
            if (data != null) {
                for (Object item : data) {
                    for (Report.Column col : table.getColumns()) {
                        Object val = getFieldValue(item, col.getDetailExpression());
                        stream.append("(").append(escapePdf(val != null ? val.toString() : "")).append(") Tj\n");
                        stream.append("80 0 Td\n");
                    }
                    stream.append("-").append(table.getColumns().size() * 80).append(" -12 Td\n");
                }
            }
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
