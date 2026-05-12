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
        
        setMaxWidth("800px");
        setPadding("20px");
        setZIndex("9999");
        setStyle("display", "none");
        
        buildUI();
    }
    
    private void buildUI() {
        // Toolbar
        Div toolbar = new Div()
            .setStyle("display", "flex")
            .setStyle("gap", "10px")
            .setStyle("margin-bottom", "15px")
            .setStyle("border-bottom", "1px solid #ccc")
            .setStyle("padding-bottom", "10px");
        
        Report.ViewerOptions options = report.getViewerOptions();
        
        if (options.isAllowPdf()) {
            toolbar.add(new Button("📄 PDF")
                .setBackgroundColor("#da3633")
                .setOnclick("location.href='?action=report&format=pdf'; document.getElementById('reportModal_" + uniqueId + "').style.display='none';"));
        }
        if (options.isAllowExcel()) {
            toolbar.add(new Button("📊 Excel")
                .setBackgroundColor("#238636")
                .setOnclick("location.href='?action=report&format=excel'; document.getElementById('reportModal_" + uniqueId + "').style.display='none';"));
        }
        if (options.isAllowCsv()) {
            toolbar.add(new Button("📝 CSV")
                .setBackgroundColor("#8957e5")
                .setOnclick("location.href='?action=report&format=csv'; document.getElementById('reportModal_" + uniqueId + "').style.display='none';"));
        }
        if (options.isAllowPrint()) {
            toolbar.add(new Button("🖨️ Imprimir")
                .setBackgroundColor("#007bff")
                .setOnclick("location.href='?action=report&format=pdf&print=true'; document.getElementById('reportModal_" + uniqueId + "').style.display='none';"));
        }
        
        SelectOne sizeSelect = new SelectOne("sizeSelect")
            .setId("sizeSelect_" + uniqueId)
            .addOption("100%", "Maximizar (100%)")
            .addOption("800px", "Original (800px)")
            .addOption("75%", "75%")
            .addOption("50%", "50%")
            .addOption("25%", "25%")
            .setProperty("onchange", "var size = this.value; var modal = document.getElementById('reportModal_" + uniqueId + "'); if(modal) { if(size === '100%') { modal.style.width = '95vw'; modal.style.height = '95vh'; modal.style.maxWidth = '95vw'; modal.style.maxHeight = '95vh'; } else { modal.style.width = size; modal.style.maxWidth = size; modal.style.height = 'auto'; modal.style.maxHeight = '90vh'; } }");
            
        toolbar.add(sizeSelect);
        
        Button closeBtn = new Button("Cerrar")
            .setBackgroundColor("#30363d")
            .setOnclick("document.getElementById('reportModal_" + uniqueId + "').style.display='none';");
        toolbar.add(closeBtn);
        
        // Report Preview Panel
        Div previewPanel = new Div()
            .setStyle("max-height", "60vh")
            .setStyle("overflow-y", "auto")
            .setStyle("background-color", "white")
            .setStyle("color", "black")
            .setStyle("padding", "20px")
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
        if (el instanceof Report.TextElement) {
            Report.TextElement tel = (Report.TextElement) el;
            Paragraph p = new Paragraph(tel.getExpression());
            try {
                Field boldField = Report.TextElement.class.getDeclaredField("bold");
                boldField.setAccessible(true);
                if ((Boolean) boldField.get(tel)) p.setStyle("font-weight", "bold");
            } catch (Exception e) {}
            return p;
        } else if (el instanceof Report.Table) {
            Report.Table rtable = (Report.Table) el;
            Datatable table = new Datatable();
            Row headerRow = new Row();
            for (Report.Column col : rtable.getColumns()) {
                headerRow.add(new TD(col.getHeader()).setStyle("font-weight", "bold").setStyle("border-bottom", "2px solid #ccc"));
            }
            table.addHeaderRow(headerRow);
            
            if (report.getData() != null) {
                for (Object item : report.getData()) {
                    Row dataRow = new Row();
                    for (Report.Column col : rtable.getColumns()) {
                        Object val = getFieldValue(item, col.getDetailExpression());
                        dataRow.add(new TD(val != null ? val.toString() : "").setStyle("border-bottom", "1px solid #eee"));
                    }
                    table.addRow(dataRow);
                }
            }
            return table;
        }
        return new Div();
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
