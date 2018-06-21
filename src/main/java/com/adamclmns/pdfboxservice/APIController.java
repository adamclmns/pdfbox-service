package com.adamclmns.pdfboxservice;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.UUID;

@RestController
public class APIController {

    @RequestMapping(path="/version")
    public String getVersion(){
        Date now = new Date();
        return "Version 1.0.1 as of "+now.toString();
    }


    @RequestMapping(path="/pdf")
    public void getAltPDF(HttpServletResponse response) throws Exception{
        PDFBuilder pdfBuilder = new PDFBuilder();
        PDDocument pdf =  pdfBuilder.getFancyPDF("Documet Title Goes Here");
        pdf.save(response.getOutputStream());
        response.flushBuffer();
        pdf.close();
    }
}
