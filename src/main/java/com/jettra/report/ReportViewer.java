package com.jettra.report;

import io.jettra.wui.complex.*;
import io.jettra.wui.components.*;
import java.lang.reflect.Field;

public class ReportViewer extends Modal {
    
    private Report report;
    private String uniqueId;

    public ReportViewer(Report report, String uniqueId) {
        super("reportModal_" + uniqueId);
        this.report = report;
        this.uniqueId = uniqueId;
        
        setMaxWidth("900px");
        setPadding("0");
        setBackgroundColor("#161b22");
        setZIndex("9999");
        setStyle("display", "none");
        setStyle("border", "1px solid #30363d");
        setStyle("box-shadow", "0 0 50px rgba(0,0,0,0.5)");
        
        buildUI();
    }
    
    private void buildUI() {
        // Toolbar
        Div toolbar = new Div()
            .setStyle("display", "flex")
            .setStyle("flex-wrap", "wrap")
            .setStyle("gap", "8px")
            .setStyle("margin-bottom", "15px")
            .setStyle("padding", "15px")
            .setStyle("background-color", "#0d1117")
            .setStyle("border-bottom", "1px solid #30363d")
            .setStyle("border-radius", "8px 8px 0 0");
        
        Report.ViewerOptions options = report.getViewerOptions();
        
        if (options.isAllowPdf()) {
            toolbar.add(new Button("📄\nPDF")
                .setBackgroundColor("#da3633")
                .setStyle("height", "60px").setStyle("width", "70px")
                .setStyle("display", "flex").setStyle("flex-direction", "column").setStyle("align-items", "center").setStyle("justify-content", "center")
                .setOnclick("location.href='?action=report&format=pdf';"));
        }
        if (options.isAllowExcel()) {
            toolbar.add(new Button("📊\nExcel")
                .setBackgroundColor("#238636")
                .setStyle("height", "60px").setStyle("width", "70px")
                .setStyle("display", "flex").setStyle("flex-direction", "column").setStyle("align-items", "center").setStyle("justify-content", "center")
                .setOnclick("location.href='?action=report&format=excel';"));
        }
        if (options.isAllowCsv()) {
            toolbar.add(new Button("📝\nCSV")
                .setBackgroundColor("#8957e5")
                .setStyle("height", "60px").setStyle("width", "70px")
                .setStyle("display", "flex").setStyle("flex-direction", "column").setStyle("align-items", "center").setStyle("justify-content", "center")
                .setOnclick("location.href='?action=report&format=csv';"));
        }
        if (options.isAllowWord()) {
            toolbar.add(new Button("📘\nWord")
                .setBackgroundColor("#0969da")
                .setStyle("height", "60px").setStyle("width", "70px")
                .setStyle("display", "flex").setStyle("flex-direction", "column").setStyle("align-items", "center").setStyle("justify-content", "center")
                .setOnclick("location.href='?action=report&format=word';"));
        }
        if (options.isAllowPrint()) {
            toolbar.add(new Button("🖨️\nImprimir")
                .setBackgroundColor("#007bff")
                .setStyle("height", "60px").setStyle("width", "100px")
                .setStyle("display", "flex").setStyle("flex-direction", "column").setStyle("align-items", "center").setStyle("justify-content", "center")
                .setOnclick("var content = document.getElementById('previewPanel_" + uniqueId + "').innerHTML; var win = window.open('', '', 'height=700,width=800'); win.document.write('<html><head><title>Reporte</title><style>body{font-family:sans-serif;padding:40px;} table{width:100%;border-collapse:collapse;margin-top:20px;} th,td{border:1px solid #eee;padding:12px;text-align:left;} th{color:#da3633;text-transform:uppercase;font-weight:bold;border-bottom:2px solid #da3633;}</style></head><body>'); win.document.write(content); win.document.write('</body></html>'); win.document.close(); win.print();"));
        }
        
        SelectOne sizeSelect = new SelectOne("sizeSelect")
            .setId("sizeSelect_" + uniqueId)
            .setStyle("height", "60px").setStyle("padding", "0 15px").setStyle("background-color", "#21262d").setStyle("color", "white").setStyle("border", "1px solid #30363d").setStyle("border-radius", "6px")
            .addOption("100%", "Maximizar (100%)")
            .addOption("800px", "Original (800px)")
            .addOption("75%", "75%")
            .addOption("50%", "50%")
            .addOption("25%", "25%")
            .setProperty("onchange", "var size = this.value; var modal = document.getElementById('reportModal_" + uniqueId + "'); if(modal) { if(size === '100%') { modal.style.width = '95vw'; modal.style.height = '95vh'; modal.style.maxWidth = '95vw'; modal.style.maxHeight = '95vh'; } else { modal.style.width = size; modal.style.maxWidth = size; modal.style.height = 'auto'; modal.style.maxHeight = '90vh'; } }");
            
        toolbar.add(sizeSelect);
        
        Button closeBtn = new Button("Cerrar")
            .setBackgroundColor("#30363d")
            .setStyle("height", "60px").setStyle("padding", "0 20px")
            .setOnclick("document.getElementById('reportModal_" + uniqueId + "').style.display='none';");
        toolbar.add(closeBtn);
        
        // Report Preview Panel
        Div previewPanel = new Div()
            .setId("previewPanel_" + uniqueId)
            .setStyle("max-height", "70vh")
            .setStyle("overflow-y", "auto")
            .setStyle("background-color", "white")
            .setStyle("color", "black")
            .setStyle("padding", "40px")
            .setStyle("margin", "0 20px 20px 20px")
            .setStyle("border-radius", "4px")
            .setStyle("border", "1px solid #ddd");
        
        // Render Header
        for (Report.ReportElement el : report.getHeader().getElements()) {
            previewPanel.add(renderElement(el, null));
        }
        
        // Render Detail
        boolean tableRendered = false;
        for (Report.ReportElement el : report.getDetail().getElements()) {
            if (el instanceof Report.Table) {
                previewPanel.add(renderElement(el, null));
                tableRendered = true;
            }
        }
        
        if (!tableRendered && report.getData() != null) {
            for (Object row : report.getData()) {
                for (Report.ReportElement el : report.getDetail().getElements()) {
                    previewPanel.add(renderElement(el, row));
                }
            }
        }
        
        // Render Summary
        for (Report.ReportElement el : report.getSummary().getElements()) {
            previewPanel.add(renderElement(el, null));
        }
        
        // Render Footer
        for (Report.ReportElement el : report.getFooter().getElements()) {
            previewPanel.add(renderElement(el, null));
        }
        
        this.add(toolbar).add(previewPanel);
    }
    
    private io.jettra.wui.core.UIComponent renderElement(Report.ReportElement el, Object row) {
        if (el instanceof Report.TextElement tel) {
            String text = tel.getExpression();
            if (row != null) {
                text = resolveExpression(text, row);
            }
            Paragraph p = new Paragraph(text);
            if (tel.isBold()) p.setStyle("font-weight", "bold");
            if (tel.getFontSize() > 0) p.setStyle("font-size", tel.getFontSize() + "px");
            if (tel.getFontColor() != null) p.setStyle("color", tel.getFontColor());
            return p;
        } else if (el instanceof Report.DateElement del) {
            String text = resolveExpression(del.getExpression(), row);
            Paragraph p = new Paragraph(text);
            if (del.isBold()) p.setStyle("font-weight", "bold");
            if (del.getFontColor() != null) p.setStyle("color", del.getFontColor());
            return p;
        } else if (el instanceof Report.NumericElement nel) {
            String text = resolveExpression(nel.getExpression(), row);
            Paragraph p = new Paragraph(text);
            p.setStyle("text-align", "right");
            if (nel.isBold()) p.setStyle("font-weight", "bold");
            if (nel.getFontColor() != null) p.setStyle("color", nel.getFontColor());
            return p;
        } else if (el instanceof Report.Table table) {
            Datatable dtable = new Datatable();
            dtable.setStyle("width", "100%");
            Row headerRow = new Row();
            for (Report.Column col : table.getColumns()) {
                // Image mockup shows red uppercase headers
                TD td = new TD(col.getHeader().toUpperCase())
                        .setStyle("font-weight", "bold")
                        .setStyle("color", "#da3633")
                        .setStyle("font-size", "1.2rem")
                        .setStyle("padding", "15px")
                        .setStyle("border-bottom", "1px solid #eee")
                        .setStyle("text-align", "left");
                if (col.getWidth() > 0) td.setStyle("width", col.getWidth() + "px");
                headerRow.add(td);
            }
            dtable.addHeaderRow(headerRow);
            
            if (report.getData() != null) {
                for (Object item : report.getData()) {
                    Row dataRow = new Row();
                    for (Report.Column col : table.getColumns()) {
                        Object val = getFieldValue(item, col.getDetailExpression());
                        TD td = new TD(val != null ? val.toString() : "").setStyle("border-bottom", "1px solid #eee");
                        if (col.isBold()) td.setStyle("font-weight", "bold");
                        if (col.getFontColor() != null) td.setStyle("color", col.getFontColor());
                        if (col.getFontSize() > 0) td.setStyle("font-size", col.getFontSize() + "px");
                        dataRow.add(td);
                    }
                    dtable.addRow(dataRow);
                }
            }
            return dtable;
        } else if (el instanceof Report.ImageElement img) {
            return new Image(img.getPath(), "Image");
        }
        return new Div();
    }

    private String resolveExpression(String expression, Object row) {
        if (expression == null) return "";
        if (row == null) return expression;
        
        // Basic resolution for $F{field}
        if (expression.contains("$F{")) {
            String fieldName = expression.substring(expression.indexOf("$F{") + 3, expression.indexOf("}"));
            Object val = getFieldValue(row, fieldName);
            return expression.replace("$F{" + fieldName + "}", val != null ? val.toString() : "");
        }
        
        // If it's just a field name and we are in detail
        Object val = getFieldValue(row, expression);
        return val != null ? val.toString() : expression;
    }
    
    private Object getFieldValue(Object obj, String expression) {
        try {
            Field field = obj.getClass().getDeclaredField(expression);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return expression;
        }
    }
}
