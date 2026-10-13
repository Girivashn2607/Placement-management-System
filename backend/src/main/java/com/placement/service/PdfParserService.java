package com.placement.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfParserService {
    private static final Logger logger = LoggerFactory.getLogger(PdfParserService.class);

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        // Validate content type and extension
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        if ((contentType != null && !contentType.equalsIgnoreCase("application/pdf")) &&
            (originalFilename != null && !originalFilename.toLowerCase().endsWith(".pdf"))) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        // Apache PDFBox 3.x Loader.loadPDF(byte[]) or Loader.loadPDF(RandomAccessRead)
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new IllegalArgumentException("PDF is password-protected or encrypted.");
                }

                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String text = stripper.getText(document);

                if (text == null || text.trim().isEmpty()) {
                    return "NOTICE: Scanned or image-only PDF detected. Direct text layer was empty. Text extraction unavailable.";
                }

                return text.trim();
            }
        } catch (Exception e) {
            logger.error("Error parsing PDF file: {}", e.getMessage());
            throw new IOException("Failed to parse PDF document: " + e.getMessage(), e);
        }
    }
}
