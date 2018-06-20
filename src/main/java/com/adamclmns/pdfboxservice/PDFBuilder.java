package com.adamclmns.pdfboxservice;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.util.Matrix;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFBuilder {
    private static final float FONT_SIZE = 20.0f;
    private static final String outfile = "justify-example.pdf";
    private static final String message = "Hello World. This is a test. ";
    public PDDocument showTextWithPositioningDemo() throws Exception {
        // the document
        PDDocument doc = new PDDocument();
        try (InputStream is = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")) {
            // Page 1
            PDFont font = PDType0Font.load(doc, is, true);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // Get the non-justified string width in text space units.
            float stringWidth = font.getStringWidth(message) * FONT_SIZE;

            // Get the string height in text space units.
            float stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() * FONT_SIZE;

            // Get the width we have to justify in.
            PDRectangle pageSize = page.getMediaBox();

            try (PDPageContentStream contentStream = new PDPageContentStream(doc,
                    page, PDPageContentStream.AppendMode.OVERWRITE, false)) {
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE);

                // Start at top of page.
                contentStream.setTextMatrix(
                        Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f));

                // First show non-justified.
                contentStream.showText(message);

                // Move to next line.
                contentStream.setTextMatrix(
                        Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 2));

                // Now show word justified.
                // The space we have to make up, in text space units.
                float justifyWidth = pageSize.getWidth() * 1000f - stringWidth;

                List<Object> text = new ArrayList<>();
                String[] parts = message.split("\\s");

                float spaceWidth = (justifyWidth / (parts.length - 1)) / FONT_SIZE;

                for (int i = 0; i < parts.length; i++) {
                    if (i != 0) {
                        text.add(" ");
                        // Positive values move to the left, negative to the right.
                        text.add(-spaceWidth);
                    }
                    text.add(parts[i]);
                }
                contentStream.showText( String.valueOf(text.toArray()));
                contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 3));

                // Now show letter justified.
                text = new ArrayList<>();
                justifyWidth = pageSize.getWidth() * 1000f - stringWidth;
                float extraLetterWidth = (justifyWidth / (message.codePointCount(0, message.length()) - 1)) / FONT_SIZE;

                for (int i = 0; i < message.length(); i += Character.charCount(message.codePointAt(i))) {
                    if (i != 0) {
                        text.add(-extraLetterWidth);
                    }

                    text.add(String.valueOf(Character.toChars(message.codePointAt(i))));
                }

                contentStream.showText(String.valueOf(text.toArray()));

                // PDF specification about word spacing:
                // "Word spacing shall be applied to every occurrence of the single-byte character
                // code 32 in a string when using a simple font or a composite font that defines
                // code 32 as a single-byte code. It shall not apply to occurrences of the byte
                // value 32 in multiple-byte codes.
                // TrueType font with no word spacing
                contentStream.setTextMatrix(
                        Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 4));
                font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream(
                        "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), WinAnsiEncoding.INSTANCE);
                contentStream.setFont(font, FONT_SIZE);
                contentStream.showText(message);

                float wordSpacing = (pageSize.getWidth() * 1000f - stringWidth) / (parts.length - 1) / 1000;

                // TrueType font with word spacing
                contentStream.setTextMatrix(
                        Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 5));
                font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream(
                        "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), WinAnsiEncoding.INSTANCE);
                contentStream.setFont(font, FONT_SIZE);
                contentStream.setWordSpacing(wordSpacing);
                contentStream.showText(message);

                // Type0 font with word spacing that has no effect
                contentStream.setTextMatrix(
                        Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 6));
                font = PDType0Font.load(doc, PDDocument.class.getResourceAsStream(
                        "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"));
                contentStream.setFont(font, FONT_SIZE);
                contentStream.setWordSpacing(wordSpacing);
                contentStream.showText(message);

                // Finish up.
                contentStream.endText();
            }


        }
        return doc;
    }
    }
