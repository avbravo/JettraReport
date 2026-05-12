package com.jettra.report;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for JettraReport library.
 * Supports multiple sections, charts, and subreports.
 */
public class Report {
    public static final String PAGINATION_VAR = "$V{PAGE_NUMBER}";
    
    private String title;
    private List<?> data = new ArrayList<>();
    private PageSettings pageSettings = new PageSettings();
    private Header header = new Header();
    private List<Group> groups = new ArrayList<>();
    private Detail detail = new Detail();
    private Summary summary = new Summary();
    private Footer footer = new Footer();
    private ViewerOptions viewerOptions = new ViewerOptions();

    public Report(String title) {
        this.title = title;
    }

    public List<?> getData() { return data; }
    public void setData(List<?> data) { this.data = data; }

    public PageSettings getPageSettings() { return pageSettings; }
    public Header getHeader() { return header; }
    public List<Group> getGroups() { return groups; }
    public Detail getDetail() { return detail; }
    public Summary getSummary() { return summary; }
    public Footer getFooter() { return footer; }
    public ViewerOptions getViewerOptions() { return viewerOptions; }

    public ReportViewer createViewer(String uniqueId) {
        return new ReportViewer(this, uniqueId);
    }

    // Nested classes for report sections
    
    public static class ViewerOptions {
        private boolean showViewer = true;
        private boolean allowPrint = true;
        private boolean allowPdf = true;
        private boolean allowExcel = true;
        private boolean allowCsv = true;

        public ViewerOptions setShowViewer(boolean showViewer) { this.showViewer = showViewer; return this; }
        public ViewerOptions setAllowPrint(boolean allowPrint) { this.allowPrint = allowPrint; return this; }
        public ViewerOptions setAllowPdf(boolean allowPdf) { this.allowPdf = allowPdf; return this; }
        public ViewerOptions setAllowExcel(boolean allowExcel) { this.allowExcel = allowExcel; return this; }
        public ViewerOptions setAllowCsv(boolean allowCsv) { this.allowCsv = allowCsv; return this; }

        public boolean isShowViewer() { return showViewer; }
        public boolean isAllowPrint() { return allowPrint; }
        public boolean isAllowPdf() { return allowPdf; }
        public boolean isAllowExcel() { return allowExcel; }
        public boolean isAllowCsv() { return allowCsv; }
    }

    public static class PageSettings {
        public enum Orientation { PORTRAIT, LANDSCAPE }
        public enum PageSize { A4, LETTER, LEGAL }
        
        private Orientation orientation = Orientation.PORTRAIT;
        private PageSize pageSize = PageSize.A4;
        private int marginLeft = 30, marginRight = 30, marginTop = 30, marginBottom = 30;

        public PageSettings setOrientation(Orientation orientation) { this.orientation = orientation; return this; }
        public PageSettings setPageSize(PageSize pageSize) { this.pageSize = pageSize; return this; }
        public PageSettings setMargins(int left, int right, int top, int bottom) {
            this.marginLeft = left; this.marginRight = right;
            this.marginTop = top; this.marginBottom = bottom;
            return this;
        }
        
        public Orientation getOrientation() { return orientation; }
        public PageSize getPageSize() { return pageSize; }
        public int getMarginLeft() { return marginLeft; }
        public int getMarginRight() { return marginRight; }
        public int getMarginTop() { return marginTop; }
        public int getMarginBottom() { return marginBottom; }
    }

    public static class Header {
        private List<ReportElement> elements = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public List<ReportElement> getElements() { return elements; }
    }

    public static class Detail {
        private List<ReportElement> elements = new ArrayList<>();
        private List<Report> subreports = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public void addSubreport(Report subreport) { subreports.add(subreport); }
        public List<ReportElement> getElements() { return elements; }
        public List<Report> getSubreports() { return subreports; }
    }

    public static class Footer {
        private List<ReportElement> elements = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public List<ReportElement> getElements() { return elements; }
    }

    public static class Summary {
        private List<ReportElement> elements = new ArrayList<>();
        private List<Chart> charts = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public void addChart(Chart chart) { charts.add(chart); }
        public List<ReportElement> getElements() { return elements; }
        public List<Chart> getCharts() { return charts; }
    }

    public static class Group {
        private String groupByExpression;
        private Header groupHeader = new Header();
        private Footer groupFooter = new Footer();
        private List<Subtotal> subtotals = new ArrayList<>();
        
        public String getGroupByExpression() { return groupByExpression; }
        public void setGroupByExpression(String groupByExpression) { this.groupByExpression = groupByExpression; }
        public Header getGroupHeader() { return groupHeader; }
        public Footer getGroupFooter() { return groupFooter; }
        public List<Subtotal> getSubtotals() { return subtotals; }
    }

    // Elements

    public interface ReportElement {}

    public static class TextElement implements ReportElement {
        private String expression;
        private String fontName = "SansSerif";
        private int fontSize = 10;
        private boolean bold = false;
        
        public TextElement(String expression) { this.expression = expression; }
        public String getExpression() { return expression; }
        public void setBold(boolean bold) { this.bold = bold; }
    }

    public static class DateElement implements ReportElement {
        private String expression;
        private String pattern = "dd/MM/yyyy";
        
        public DateElement(String expression) { this.expression = expression; }
        public DateElement(String expression, String pattern) { 
            this.expression = expression; 
            this.pattern = pattern;
        }
        public String getExpression() { return expression; }
        public String getPattern() { return pattern; }
    }

    public static class NumericElement implements ReportElement {
        private String expression;
        private String format = "#,##0.00";
        
        public NumericElement(String expression) { this.expression = expression; }
        public NumericElement(String expression, String format) { 
            this.expression = expression; 
            this.format = format;
        }
        public String getExpression() { return expression; }
        public String getFormat() { return format; }
    }

    public static class Table implements ReportElement {
        private List<Column> columns = new ArrayList<>();
        private String datasourceExpression;
        
        public void addColumn(Column column) { columns.add(column); }
        public List<Column> getColumns() { return columns; }
    }

    public static class Column {
        private String header;
        private String detailExpression;
        private int width;
        
        public Column(String header, String detailExpression, int width) {
            this.header = header;
            this.detailExpression = detailExpression;
            this.width = width;
        }
        public String getHeader() { return header; }
        public String getDetailExpression() { return detailExpression; }
        public int getWidth() { return width; }
    }

    public static class ImageElement implements ReportElement {
        private String path;
        public ImageElement(String path) { this.path = path; }
        public String getPath() { return path; }
    }

    public static class Chart {
        public enum Type { BAR, PIE, LINE }
        private Type type;
        private String datasetExpression;
        public Chart(Type type) { this.type = type; }
    }

    public static class Subtotal {
        public enum Function { SUM, AVG, COUNT, MIN, MAX }
        private String fieldExpression;
        private Function function;
    }

    // Export methods

    public void exportToPdf(String path) {
        Document document = new Document(
            pageSettings.pageSize == PageSettings.PageSize.LETTER ? PageSize.LETTER : PageSize.A4,
            pageSettings.marginLeft, pageSettings.marginRight, pageSettings.marginTop, pageSettings.marginBottom
        );
        if (pageSettings.orientation == PageSettings.Orientation.LANDSCAPE) {
            document.setPageSize(document.getPageSize().rotate());
        }

        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Header Section
            for (ReportElement el : header.getElements()) {
                addPdfElement(document, el, null);
            }

            // Detail Section
            // Logic: If there is a Table, we render it once (as it contains its own data loop)
            // If there are other elements, we render them per row.
            boolean tableRendered = false;
            for (ReportElement el : detail.getElements()) {
                if (el instanceof Table) {
                    addPdfElement(document, el, null);
                    tableRendered = true;
                }
            }
            
            if (!tableRendered) {
                for (Object row : data) {
                    for (ReportElement el : detail.getElements()) {
                        addPdfElement(document, el, row);
                    }
                }
            }

            // Summary Section
            for (ReportElement el : summary.getElements()) {
                addPdfElement(document, el, null);
            }

            // Footer Section
            for (ReportElement el : footer.getElements()) {
                addPdfElement(document, el, null);
            }

            document.close();
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addPdfElement(Document doc, ReportElement el, Object row) throws Exception {
        if (el instanceof TextElement tel) {
            Font font = new Font(Font.HELVETICA, 12, tel.bold ? Font.BOLD : Font.NORMAL);
            doc.add(new Paragraph(tel.getExpression(), font));
        } else if (el instanceof Table table) {
            PdfPTable pdfTable = new PdfPTable(table.getColumns().size());
            pdfTable.setWidthPercentage(100);
            
            // Headers
            for (Column col : table.getColumns()) {
                pdfTable.addCell(new Phrase(col.getHeader(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            
            // If we are in the detail section and el is a table, 
            // the user probably wants to render the WHOLE data list in this table.
            // But usually detail section renders ONE row at a time.
            // In JettraReport, Table element in Detail section means "Render all data here".
            if (data != null && !data.isEmpty()) {
                for (Object item : data) {
                    for (Column col : table.getColumns()) {
                        Object val = getFieldValue(item, col.getDetailExpression());
                        pdfTable.addCell(new Phrase(val != null ? val.toString() : "", new Font(Font.HELVETICA, 10)));
                    }
                }
            }
            doc.add(pdfTable);
        }
    }

    private Object getFieldValue(Object obj, String expression) {
        try {
            Field field = obj.getClass().getDeclaredField(expression);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return expression; // Return as literal if field not found
        }
    }

    public void exportToWord(String path) { /* Implementation with POI */ }

    public void exportToExcel(String path) {
        try (org.apache.poi.xssf.streaming.SXSSFWorkbook workbook = new org.apache.poi.xssf.streaming.SXSSFWorkbook();
             java.io.FileOutputStream out = new java.io.FileOutputStream(path)) {
            
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Report");
            Table table = null;
            for (ReportElement el : detail.getElements()) {
                if (el instanceof Table) {
                    table = (Table) el;
                    break;
                }
            }

            if (table != null) {
                int rowNum = 0;
                // Header
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                for (int i = 0; i < table.getColumns().size(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(table.getColumns().get(i).getHeader());
                    cell.setCellStyle(headerStyle);
                }

                // Data
                if (data != null) {
                    for (Object item : data) {
                        org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                        for (int i = 0; i < table.getColumns().size(); i++) {
                            Object val = getFieldValue(item, table.getColumns().get(i).getDetailExpression());
                            row.createCell(i).setCellValue(val != null ? val.toString() : "");
                        }
                    }
                }
            }
            workbook.write(out);
            workbook.dispose();
        } catch (Exception e) {
            System.err.println("Error generating Excel: " + e.getMessage());
        }
    }

    public void exportToCsv(String path) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileOutputStream(path))) {
            Table table = null;
            for (ReportElement el : detail.getElements()) {
                if (el instanceof Table) {
                    table = (Table) el;
                    break;
                }
            }

            if (table != null) {
                // Headers
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < table.getColumns().size(); i++) {
                    sb.append("\"").append(table.getColumns().get(i).getHeader().replace("\"", "\"\"")).append("\"");
                    if (i < table.getColumns().size() - 1) sb.append(",");
                }
                writer.println(sb.toString());

                // Data
                if (data != null) {
                    for (Object item : data) {
                        sb = new StringBuilder();
                        for (int i = 0; i < table.getColumns().size(); i++) {
                            Object val = getFieldValue(item, table.getColumns().get(i).getDetailExpression());
                            sb.append("\"").append(val != null ? val.toString().replace("\"", "\"\"") : "").append("\"");
                            if (i < table.getColumns().size() - 1) sb.append(",");
                        }
                        writer.println(sb.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error generating CSV: " + e.getMessage());
        }
    }

    public void exportToTxt(String path) { /* Implementation */ }
}
