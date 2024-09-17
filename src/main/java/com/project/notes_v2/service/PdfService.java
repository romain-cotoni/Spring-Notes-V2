package com.project.notes_v2.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    public ByteArrayOutputStream convertHtmlToPdf(Map<String, String> request) {
        String htmlContent = request.get("htmlContent");
        String xhtmlContent = this.convertHtmlToXhtml(htmlContent);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(xhtmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream;
    }


    private String convertHtmlToXhtml(String htmlContent) {
        Document document = Jsoup.parse(htmlContent, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return document.html();
    }
}
