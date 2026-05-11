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

## Practical Examples

### 1. Complete Report with Collection (Frutas)

This example shows how to generate a report from a collection of 25 objects of type `Fruta`.

```java
import com.jettra.report.Report;
import java.util.ArrayList;
import java.util.List;

public class FrutaReportExample {
    
    // 1. Define the data model
    public record Fruta(int id, String nombre, String categoria, double precio) {}

    public static void main(String[] args) {
        // 2. Create the data list (25 elements)
        List<Fruta> listaFrutas = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            listaFrutas.add(new Fruta(i, "Fruta " + i, "Cítricos", 1.5 * i));
        }

        // 3. Configure the Report
        Report report = new Report("Inventario de Frutas");
        report.setData(listaFrutas); 
        
        // 4. Page Settings
        report.getPageSettings()
              .setPageSize(Report.PageSettings.PageSize.A4)
              .setOrientation(Report.PageSettings.Orientation.PORTRAIT);

        // 5. Define Table Structure
        Report.Table table = new Report.Table();
        table.addColumn(new Report.Column("ID", "id", 30));
        table.addColumn(new Report.Column("Nombre", "nombre", 150));
        table.addColumn(new Report.Column("Categoría", "categoria", 100));
        table.addColumn(new Report.Column("Precio", "precio", 80));

        // 6. Add elements to sections
        report.getHeader().addElement(new Report.TextElement("REPORTE DE INVENTARIO"));
        report.getDetail().addElement(table);
        report.getFooter().addElement(new Report.TextElement("Pag: " + Report.PAGINATION_VAR));

        // 7. Export to multiple formats
        report.exportToPdf("inventario_frutas.pdf");
        System.out.println("Reporte generado exitosamente.");
    }
}
```

### 2. Complete Grouped Report (Deportes por Categoría)

This example demonstrates how to use the `Group` section to organize sports by their category with subtotals.

```java
import com.jettra.report.Report;
import java.util.List;

public class DeportesGroupedExample {
    
    // 1. Data Models
    public record Deporte(String nombre, String categoria) {}

    public static void main(String[] args) {
        // 2. Create sample data
        List<Deporte> deportes = List.of(
            new Deporte("Fútbol", "Equipo"),
            new Deporte("Baloncesto", "Equipo"),
            new Deporte("Voleibol", "Equipo"),
            new Deporte("Tenis", "Individual"),
            new Deporte("Natación", "Individual"),
            new Deporte("Atletismo", "Individual")
        );

        // 3. Configure Grouped Report
        Report report = new Report("Catálogo de Deportes por Categoría");
        report.setData(deportes);

        // 4. Define a Group by 'categoria'
        Report.Group group = new Report.Group();
        group.setGroupByExpression("categoria");

        // Add Header to the Group
        group.getGroupHeader().addElement(new Report.TextElement("CATEGORÍA: $F{categoria}"));

        // Add a Subtotal (COUNT) to the Group Footer
        Report.Subtotal count = new Report.Subtotal();
        count.setFieldExpression("nombre");
        count.setFunction(Report.Subtotal.Function.COUNT);
        group.getSubtotals().add(count);
        
        group.getGroupFooter().addElement(new Report.TextElement("Total de deportes en esta categoría: " + count));

        report.getGroups().add(group);

        // 5. Standard Detail
        report.getDetail().addElement(new Report.TextElement(" -> Sport: $F{nombre}"));

        // 6. Global Summary
        report.getSummary().addElement(new Report.TextElement("FIN DEL REPORTE"));

        // 7. Export
        report.exportToPdf("deportes_agrupados.pdf");
    }
}
```

### Implementation Details

- **Fluent API**: Use `getDetail()`, `getHeader()`, etc., to add elements to specific zones.
- **Expressions**: The `$F{fieldName}` syntax is used by the engine to map list properties (from Records or POJOs) to report cells.
- **Groups**: When a `Group` is added, the engine automatically detects changes in the `groupByExpression` value to trigger group headers and footers.
- **Subtotals**: They are attached to groups to provide automatic calculations like `SUM`, `COUNT`, or `AVG` over the grouped data.
- **Page Configuration**: Accessible via `report.getPageSettings()`.

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
