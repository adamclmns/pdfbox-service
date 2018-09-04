package com.adamclmns.pdfboxservice;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import be.quodlibet.boxable.image.Image;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;


public class PDFBuilder {

    private static Logger logger = LoggerFactory.getLogger(PDFBuilder.class);

    private PDPageContentStream cos;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private float leftMargin = 50;
    private int marginBetweenYElements = 10;
    private float titleFontSize = 18;
    private static final float FONT_SIZE = 15.0f;
    private static final String outfile = "justify-example.pdf";
    private static final String message = "Hello World. This is a test. ";

    // Really fancy over here....
    public PDDocument getFancyPDF(String docTitle) throws IOException {
        float yPos = 840;
        float lxPos = 35;
        float rxPos = 550;
        // Create A4 letter page
        PDPage myPage = new PDPage(PDRectangle.A4);
        PDDocument mainDocument = new PDDocument();
        PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);


        yPos = drawLogo(mainDocument, contentStream, 0, yPos);
        float rightColumn = drawPageTitle(contentStream, "PRODUCT OWNER", 385, yPos);
        // Not updating yPos, so the next line will kinda go beside the Title
        rightColumn = drawPageTitle(contentStream, "MONTHLY PROCESS INVOICE", 300, rightColumn) - 10;

        rightColumn = drawSingleCellTable(mainDocument, myPage,460, rightColumn, "Invoice Date", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        rightColumn = drawSingleCellTable(mainDocument, myPage,460, rightColumn, "Due Date", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        rightColumn = drawSingleCellTable(mainDocument, myPage,460, rightColumn, "Payment Date", LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        yPos = drawAddressField(contentStream, "Magical Services Inc. \n123 Breaker Street \nCircuit City, USA, 12345 \n(123)-456-7890 \nINFO HERE: XXX-YYY-ZZZ", 25, yPos);

        yPos = drawSingleCellTable(mainDocument, myPage, lxPos, yPos, "CustomerID","XYZ123");

        yPos = drawSingleCellTable(mainDocument, myPage, lxPos, yPos, "Customer","X, Y, and Z Customer Inc. 123 Power Drive, Current City, USA, 123434");

        contentStream.setLineDashPattern (new float[]{3,1}, 0);
        yPos-=35;
        contentStream.drawLine(lxPos-10,yPos,rxPos+10,yPos);
        yPos-=35;
        // Here's where the Invoice Summary goes
        // BUILDING SOME DUMMY DATA HERE
        ArrayList<String> headerRow = new ArrayList<>();
        headerRow.add("Invoice Summary");
        headerRow.add("Detail Column 1");
        headerRow.add("Detail Column 2");
        headerRow.add("Detail Column 3");
        headerRow.add("Totals Column");

        ArrayList<ArrayList<String>> dataRows = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 20; i++){
            ArrayList<String> row = new ArrayList<>();
            row.add("THIS IS A ROW ITEM");
            row.add(String.valueOf(rand.ints(0,9999).findAny().getAsInt()));
            row.add(String.valueOf(rand.ints(0,9999).findAny().getAsInt()));
            row.add(String.valueOf(rand.ints(0,9999).findAny().getAsInt()));
            row.add(String.valueOf(rand.ints(0,9999).findAny().getAsInt()));
            dataRows.add(row);
        }

        drawInvoiceSummaryTable(mainDocument, myPage, lxPos,yPos, headerRow, dataRows);

        mainDocument.addPage(myPage);
        contentStream.close();
        return mainDocument;
    }

    private float drawLogo(PDDocument mainDocument, PDPageContentStream cos, float xPos, float yPos) throws IOException {
        Image image = new Image(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("logo.png")));
        float imageWidth = 300;
        image = image.scaleByWidth(imageWidth);
        image.draw(mainDocument, cos, xPos, yPos);

        float height = image.getHeight();
        float width = image.getWidth();
        logger.info("HEIGHT : " + height);

        return yPos - height;
    }

    /**
     * Writes the Title Line to the PDF File
     *
     * @param cos
     * @param title
     * @throws IOException
     */
    private float drawPageTitle(PDPageContentStream cos, String title, float xPos, float yPos) throws IOException {
        int titleFontSize = 16;
        PDFont titleFont = HELVETICA_BOLD;
        /*
            NOTES:
            The Top of the page is 840y
            the Left of the page is exactly 0.

            The Y coordinate seems to be where the center line of the text is displayed, with the height of the
             text sometimes bleeding off the top edge or bottom edge depending.
         */
        PDStreamUtils.write(cos, title, titleFont, titleFontSize, xPos, yPos, Color.BLACK);

        return yPos - titleFontSize;
    }

    private float drawAddressField(PDPageContentStream cos, String addressPayLoadString, float xPos, float yPos) throws IOException {
        float fontSize = 11;
        PDFont font = PDType1Font.HELVETICA;
        PDFont boldFont = HELVETICA_BOLD;
        boolean first = true;
        String[] addressLines = addressPayLoadString.split("\n");
        for (String line : addressLines) {
            if (first) {
                PDStreamUtils.write(cos, line, boldFont, fontSize, xPos, yPos, Color.BLACK);
                first = false;
            } else {
                PDStreamUtils.write(cos, line, font, fontSize, xPos, yPos, Color.BLACK);
            }
            yPos -= (fontSize * 1.2);
        }

        return yPos;
    }

    private float drawSingleCellTable(PDDocument mainDocument, PDPage myPage, float xPos, float yPos, String heading, String value) throws IOException {
        float margin = 15;
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
        BaseTable invoiceDateTable = new BaseTable(yPos, yStartNewPage, margin, 85, xPos, mainDocument, myPage, true, true);

        Row<PDPage> headerRow = invoiceDateTable.createRow(15);
        Cell<PDPage> cell = headerRow.createCell(100,heading);
        cell.setFillColor(Color.LIGHT_GRAY);
        cell.setFont(HELVETICA_BOLD);
        invoiceDateTable.addHeaderRow(headerRow);

        Row<PDPage> row = invoiceDateTable.createRow(12);
        cell = row.createCell(100, value);
        invoiceDateTable.draw();

        return yPos - 12 - 15 - 25;
    }

    private float drawInvoiceSummaryTable(PDDocument mainDocument, PDPage myPage, float xPos, float yPos, ArrayList<String> headings, ArrayList<ArrayList<String>> valueRows) throws IOException {
        float margin = 15;
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
        BaseTable invoiceDateTable = new BaseTable(yPos, yStartNewPage, margin, 450, xPos, mainDocument, myPage, true, true);


        Row<PDPage> headerRow = invoiceDateTable.createRow(15);
        int cellWidth = 75 / (headings.size()-1);
        boolean first = true;
        for(String heading : headings) {
            if(first){
                Cell<PDPage> cell = headerRow.createCell(40, heading);
                cell.setFillColor(Color.LIGHT_GRAY);
                cell.setFont(HELVETICA_BOLD);
                invoiceDateTable.addHeaderRow(headerRow);
                first=false;
            } else {
                Cell<PDPage> cell = headerRow.createCell(cellWidth, heading);
                cell.setFillColor(Color.LIGHT_GRAY);
                cell.setFont(HELVETICA_BOLD);
                invoiceDateTable.addHeaderRow(headerRow);
            }

        }

        for(ArrayList<String> row : valueRows) {
            boolean firstCell = true;
            Row<PDPage> tableRow = invoiceDateTable.createRow(12);
            for(String value : row) {
                if(firstCell) {
                    firstCell = false;
                    Cell<PDPage> cell = tableRow.createCell(40, value);
                }else{
                    Cell<PDPage> cell = tableRow.createCell(cellWidth, value);
                }
            }
        }
        invoiceDateTable.draw();

        return yPos - 12 - 15 - 25;
    }
}
