package com.jettra.report.ui;

import io.jettra.wui.complex.Center;
import io.jettra.wui.components.*;
import io.jettra.wui.core.JettraDashboardPage;
import java.util.Map;

/**
 * Visual designer for JettraReport.
 * Integrated with JettraWebDesigner.
 */
public class ReportDesignerPage extends JettraDashboardPage {

    public ReportDesignerPage() {
        super("Report Designer - JettraReport");
    }

    @Override
    protected void initCenter(Center center, String username) {
        Div layout = new Div().setStyle("display", "flex").setStyle("gap", "20px").setStyle("height", "100%");
        
        // Sidebar - Tools
        Div toolbar = new Div().setStyle("width", "250px").setStyle("background", "var(--jettra-bg-secondary)").setStyle("padding", "15px");
        toolbar.add(new Header(4, "Report Elements"));
        toolbar.add(createToolButton("Text Field", "🔤"));
        toolbar.add(createToolButton("Date Field", "📅"));
        toolbar.add(createToolButton("Numeric Field", "🔢"));
        toolbar.add(createToolButton("Table", "▦"));
        toolbar.add(createToolButton("Image", "🖼️"));
        toolbar.add(createToolButton("Chart", "📊"));
        toolbar.add(createToolButton("Subreport", "📋"));
        toolbar.add(createToolButton("Subtotal", "Σ"));
        
        // Canvas - Report Structure
        Card canvas = new Card().setTitle("Report Canvas").setWidth("100%");
        Div sections = new Div().setStyle("padding", "10px");
        sections.add(createSectionBox("Page Header", "#f0f0f0"));
        sections.add(createSectionBox("Column Header", "#e0e0e0"));
        sections.add(createSectionBox("Detail", "#ffffff"));
        sections.add(createSectionBox("Column Footer", "#e0e0e0"));
        sections.add(createSectionBox("Page Footer", "#f0f0f0"));
        sections.add(createSectionBox("Summary", "#d0d0d0"));
        
        canvas.add(sections);
        
        layout.add(toolbar).add(canvas);
        center.add(new Div().setStyle("padding", "20px").setStyle("height", "calc(100vh - 100px)").add(layout));
    }

    private Button createToolButton(String label, String icon) {
        return new Button(icon + " " + label)
            .setStyle("width", "100%")
            .setStyle("margin-bottom", "10px")
            .setStyle("justify-content", "flex-start")
            .addClass("j-btn");
    }

    private Div createSectionBox(String title, String color) {
        return new Div()
            .setStyle("border", "1px dashed #ccc")
            .setStyle("padding", "20px")
            .setStyle("margin-bottom", "5px")
            .setStyle("background-color", color)
            .add(new Span(title).setStyle("font-weight", "bold").setStyle("color", "#333"));
    }

    @Override
    protected void onPost(Map<String, String> params) {
        // Save design logic
    }
}
