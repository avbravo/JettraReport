package com.jettra.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for JettraReport library.
 * Supports multiple sections, charts, and subreports.
 * @author avbravo
 */
public class Report {
    private String title;
    private PageSettings pageSettings = new PageSettings();
    private Header header = new Header();
    private List<Group> groups = new ArrayList<>();
    private Detail detail = new Detail();
    private Summary summary = new Summary();
    private Footer footer = new Footer();

    public Report(String title) {
        this.title = title;
    }

    // Nested classes for report sections

    public static class PageSettings {
        public enum Orientation { PORTRAIT, LANDSCAPE }
        public enum PageSize { A4, LETTER, LEGAL }
        
        private Orientation orientation = Orientation.PORTRAIT;
        private PageSize pageSize = PageSize.A4;
        private int marginLeft = 30, marginRight = 30, marginTop = 30, marginBottom = 30;
    }

    public static class Header {
        private List<ReportElement> elements = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
    }

    public static class Detail {
        private List<ReportElement> elements = new ArrayList<>();
        private List<Report> subreports = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public void addSubreport(Report subreport) { subreports.add(subreport); }
    }

    public static class Footer {
        private List<ReportElement> elements = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
    }

    public static class Summary {
        private List<ReportElement> elements = new ArrayList<>();
        private List<Chart> charts = new ArrayList<>();
        public void addElement(ReportElement element) { elements.add(element); }
        public void addChart(Chart chart) { charts.add(chart); }
    }

    public static class Group {
        private String groupByExpression;
        private Header groupHeader = new Header();
        private Footer groupFooter = new Footer();
        private List<Subtotal> subtotals = new ArrayList<>();
    }

    // Elements

    public interface ReportElement {}

    public static class TextElement implements ReportElement {
        private String expression;
        private String fontName = "SansSerif";
        private int fontSize = 10;
        private boolean bold = false;
    }

    public static class ImageElement implements ReportElement {
        private String path;
    }

    public static class Chart {
        public enum Type { BAR, PIE, LINE }
        private Type type;
        private String datasetExpression;
    }

    public static class Subtotal {
        public enum Function { SUM, AVG, COUNT, MIN, MAX }
        private String fieldExpression;
        private Function function;
    }

    // Export methods

    public void exportToPdf(String path) { /* Implementation */ }
    public void exportToWord(String path) { /* Implementation */ }
    public void exportToExcel(String path) { /* Implementation */ }
    public void exportToCsv(String path) { /* Implementation */ }
    public void exportToTxt(String path) { /* Implementation */ }
}
