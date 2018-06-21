package com.adamclmns.pdfboxservice;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.util.Matrix;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFBuilder {

    private PDPageContentStream cos;
    private PDPage page;
    private PDFont font = PDType1Font.HELVETICA;
    private float leftMargin = 50;
    private int marginBetweenYElements = 10;
    private float titleFontSize = 18;
    private static final float FONT_SIZE = 15.0f;
    private static final String outfile = "justify-example.pdf";
    private static final String message = "Hello World. This is a test. ";

    //Really fancy over here....
    public PDDocument getFancyPDF(String docTitle) throws IOException {
        float yPosition = 50;
        float xPosition = 10;
        PDPage myPage = new PDPage(PDRectangle.A4);
        PDDocument mainDocument = new PDDocument();
        PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

        try {
            PDStreamUtils.write(contentStream, docTitle, font, leftMargin, xPosition, yPosition, Color.BLACK);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        contentStream.beginText();
        contentStream.setStrokingColor(Color.BLUE);
        contentStream.addComment("This is a Comment");
        contentStream.drawString("This is Blue Text");
        contentStream.setStrokingColor(Color.CYAN);
        contentStream.drawString("This is CYAN text");
        // drop Y position with default margin between vertical elements
        //yPosition -= marginBetweenYElements;
        //Dummy Table
        float margin = 50;
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        // y position is your coordinate of top left corner of the table
        yPosition = 100;

        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);


        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(100, "Example Header Row");
        table.addHeaderRow(headerRow);


        Row<PDPage> row1 = table.createRow(12);
        cell = row1.createCell(30, "30 Width Column");
        cell = row1.createCell(70, "70 Width Column for longer values");

        Row<PDPage> row2 = table.createRow(15);
        cell = row2.createCell(25, "Quarter Width");
        cell = row2.createCell(25, "Quarter Width");
        cell = row2.createCell(25, "Quarter Width");
        cell = row2.createCell(25, "Quarter Width");



        table.draw();


        contentStream.endText();
        mainDocument.addPage(myPage);
        contentStream.close();
        return mainDocument;
    }
}
