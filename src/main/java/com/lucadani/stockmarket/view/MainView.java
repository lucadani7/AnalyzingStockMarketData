package com.lucadani.stockmarket.view;

import com.lucadani.stockmarket.entity.Stock;
import com.lucadani.stockmarket.service.StockService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
public class MainView extends VerticalLayout {
    private final StockService stockService;
    private final TextField symbolField = new TextField("Stock Symbol");
    private final Grid<Stock> grid = new Grid<>(Stock.class);
    private final Button analyzeBtn = new Button("Analyze");
    private final VerticalLayout chartCard = new VerticalLayout();
    private final HorizontalLayout statsRow = new HorizontalLayout();

    public MainView(StockService stockService) {
        this.stockService = stockService;
        setupLayout();
        setupHeader();
        setupGrid();
        setupContent();
    }

    private Span createBoldHeader(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "bold")
                .set("color", "#1e293b"); // O culoare închisă, profesională
        return span;
    }

    private void setupLayout() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("background-color", "#f8fafc");
    }

    private void setupHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("background-color", "white")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)");
        H2 logo = new H2("StockSense");
        logo.getStyle().set("color", "#0f172a").set("margin", "0");
        Button themeToggle = new Button(VaadinIcon.MOON.create());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.addClickListener(e -> toggleTheme(themeToggle));
        Span spacer = new Span();
        symbolField.setPlaceholder("Ticker (e.g. AAPL)");
        symbolField.setPrefixComponent(VaadinIcon.SEARCH.create());
        analyzeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        analyzeBtn.addClickListener(e -> onAnalyze());
        header.add(logo, spacer, symbolField, analyzeBtn, themeToggle);
        header.expand(spacer);
        add(header);
    }

    private void setupGrid() {
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.getStyle().set("border-radius", "8px");
        grid.setHeight("300px");
        grid.removeAllColumns();
        grid.addColumn(Stock::getTradeDate)
                .setHeader(createBoldHeader("Date"))
                .setAutoWidth(true);
        grid.addColumn(Stock::getClosePrice)
                .setHeader(createBoldHeader("Close ($)"))
                .setAutoWidth(true);
        grid.addColumn(Stock::getVolume)
                .setHeader(createBoldHeader("Volume"))
                .setAutoWidth(true);
        grid.addColumn(s -> String.format("$%.2f", s.getClosePrice()))
                .setHeader(createBoldHeader("Close"))
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END)
                .setAutoWidth(true);
    }

    private void toggleTheme(Button button) {
        var element = com.vaadin.flow.component.UI.getCurrent().getElement();
        boolean isDark = element.getThemeList().contains("dark");

        if (isDark) {
            element.getThemeList().remove("dark");
            button.setIcon(VaadinIcon.MOON.create());
            updateChartTheme();
        } else {
            element.getThemeList().add("dark");
            button.setIcon(VaadinIcon.SUN_O.create());
            updateChartTheme();
        }
    }

    private void updateChartTheme() {
        String symbol = symbolField.getValue().trim().toUpperCase();
        if (!symbol.isEmpty()) {
            List<Stock> data = stockService.getStocksBySymbol(symbol);
            if (!data.isEmpty()) {
                renderChart(data, symbol);
            }
        }
    }

    private void setupContent() {
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setPadding(true);

        statsRow.setWidthFull();

        chartCard.setWidthFull();
        chartCard.setMinHeight("400px");
        chartCard.getStyle()
                .set("background-color", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 6px -1px rgba(0,0,0,0.1)")
                .set("padding", "20px");

        container.add(statsRow, chartCard, grid);
        add(container);
    }

    private void onAnalyze() {
        String symbol = symbolField.getValue().trim().toUpperCase();
        if (symbol.isEmpty()) {
            return;
        }

        try {
            stockService.fetchAndSaveData(symbol);
            List<Stock> data = stockService.getStocksBySymbol(symbol);

            grid.setItems(data);
            updateStats(data);
            renderChart(data, symbol);

            Notification.show("Analysis complete for " + symbol)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateStats(List<Stock> data) {
        statsRow.removeAll();
        if (data.isEmpty()) {
            return;
        }

        double max = data.stream().mapToDouble(Stock::getClosePrice).max().orElse(0);
        double min = data.stream().mapToDouble(Stock::getClosePrice).min().orElse(0);
        double last = data.getLast().getClosePrice();

        statsRow.add(
                createStatBox("High", String.format("$%.2f", max), VaadinIcon.ARROW_UP, "#059669"),
                createStatBox("Low", String.format("$%.2f", min), VaadinIcon.ARROW_DOWN, "#dc2626"),
                createStatBox("Current", String.format("$%.2f", last), VaadinIcon.CHART_LINE, "#2563eb")
        );
    }

    private VerticalLayout createStatBox(String label, String value, VaadinIcon icon, String color) {
        VerticalLayout box = new VerticalLayout();
        box.getStyle()
                .set("background-color", "var(--lumo-base-color)")
                .set("border-radius", "12px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
                .set("padding", "15px");
        box.setAlignItems(Alignment.CENTER);
        box.setSpacing(false);
        Span l = new Span(label);
        l.getStyle().set("font-size", "0.8rem").set("color", "#64748b");
        H2 v = new H2(value);
        v.getStyle().set("margin", "0").set("color", color);
        box.add(icon.create(), v, l);
        return box;
    }

    private void renderChart(List<Stock> data, String symbol) {
        chartCard.removeAll();
        if (data.isEmpty()) {
            return;
        }
        boolean isDark = com.vaadin.flow.component.UI.getCurrent().getElement().getThemeList().contains("dark");
        XYChart chart = new XYChartBuilder()
                .width(1000).height(400)
                .title(symbol + " Evolution")
                .build();

        java.awt.Color bgColor = isDark ? new java.awt.Color(30, 41, 59) : java.awt.Color.WHITE;
        java.awt.Color textColor = isDark ? java.awt.Color.WHITE : new java.awt.Color(30, 41, 59);
        chart.getStyler().setChartBackgroundColor(bgColor);
        chart.getStyler().setPlotBackgroundColor(bgColor);
        chart.getStyler().setChartFontColor(textColor);
        chart.getStyler().setAxisTickLabelsColor(textColor);
        chart.getStyler().setAxisTickMarksColor(textColor);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setSeriesColors(new java.awt.Color[]{new java.awt.Color(37, 99, 235)});
        List<java.util.Date> x = data.stream().map(s -> java.sql.Date.valueOf(s.getTradeDate())).collect(Collectors.toList());
        List<Double> y = data.stream().map(Stock::getClosePrice).collect(Collectors.toList());
        chart.addSeries("Price", x, y);

        try {
            byte[] bytes = BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            Image img = new Image("data:image/png;base64," + base64, "Chart");
            img.setWidthFull();
            chartCard.add(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
