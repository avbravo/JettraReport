# JettraReport - Java Reporting Library

`JettraReport` is a powerful and flexible reporting engine for the Jettra stack. It is built with Java 25 and provides a fluent API for building complex reports including subreports, charts, and multiple export formats.

## Key Features

- **Multi-section support**: Header, Footer, Detail, Summary, and Groups.
- **Advanced Components**: Charts (Bar, Pie, Line), Subreports, and automatic Subtotals.
- **Rich Export Formats**: PDF, Word (Docx), Excel (Xlsx), CSV, and TXT.
- **Visual Designer**: Integrated `ReportDesignerPage` for web-based visual design.
- **Java 25 Ready**: Optimized for modern Java features.

## Getting Started

Add the following dependency to your project:

```xml
<dependency>
    <groupId>com.jettra</groupId>
    <artifactId>JettraReport</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Creating a Simple Report

```java
import com.jettra.report.Report;

Report report = new Report("Sales Summary 2026");

// Add elements to sections
report.getHeader().addElement(new Report.TextElement("Global Sales Report"));

// Add a chart to the summary
Report.Chart chart = new Report.Chart(Report.Chart.Type.BAR);
report.getSummary().addChart(chart);

// Export to PDF
report.exportToPdf("reports/sales_summary.pdf");
```

## Report Sections

The `Report` class structure is hierarchical:

- **Header**: Appears at the top of every page.
- **Detail**: The main content area, supports subreports for nested data.
- **Group**: Allows grouping data with specific `GroupHeader` and `GroupFooter`.
- **Summary**: Appears at the end of the report, ideal for charts and totals.
- **Footer**: Appears at the bottom of every page (pagination info).

## Visual Designer

The library includes `ReportDesignerPage`, which can be registered in your Jettra application to allow users to design reports visually.

```java
jettraServer.addHandler("/report-designer", ReportDesignerPage.class);
```

## Exporting Data

You can export the same report object to multiple formats:

```java
report.exportToExcel("output.xlsx");
report.exportToCsv("output.csv");
report.exportToWord("output.docx");
```
