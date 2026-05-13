package com.jettra.report;

import com.jettra.report.exporter.*;
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
        private boolean allowWord = true;

        public ViewerOptions setShowViewer(boolean showViewer) { this.showViewer = showViewer; return this; }
        public ViewerOptions setAllowPrint(boolean allowPrint) { this.allowPrint = allowPrint; return this; }
        public ViewerOptions setAllowPdf(boolean allowPdf) { this.allowPdf = allowPdf; return this; }
        public ViewerOptions setAllowExcel(boolean allowExcel) { this.allowExcel = allowExcel; return this; }
        public ViewerOptions setAllowCsv(boolean allowCsv) { this.allowCsv = allowCsv; return this; }
        public ViewerOptions setAllowWord(boolean allowWord) { this.allowWord = allowWord; return this; }

        public boolean isShowViewer() { return showViewer; }
        public boolean isAllowPrint() { return allowPrint; }
        public boolean isAllowPdf() { return allowPdf; }
        public boolean isAllowExcel() { return allowExcel; }
        public boolean isAllowCsv() { return allowCsv; }
        public boolean isAllowWord() { return allowWord; }
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
        public boolean isBold() { return bold; }
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
        JettraPdfExporter.export(this, path);
    }

    public void exportToWord(String path) {
        JettraWordExporter.export(this, path);
    }

    public void exportToExcel(String path) {
        JettraExcelExporter.export(this, path);
    }

    public void exportToCsv(String path) {
        JettraCsvExporter.export(this, path);
    }

    public void exportToTxt(String path) { /* Implementation */ }
}
