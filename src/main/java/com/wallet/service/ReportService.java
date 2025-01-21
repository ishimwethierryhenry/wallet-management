package com.wallet.service;

import com.wallet.model.Transaction;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public byte[] generateTransactionReport(LocalDateTime startDate, LocalDateTime endDate, String format) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        if ("csv".equalsIgnoreCase(format)) {
            return generateCSV(transactions);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return generatePDF(transactions);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            return generateExcel(transactions);
        }

        throw new IllegalArgumentException("Unsupported format: " + format);
    }

    private byte[] generateCSV(List<Transaction> transactions) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        // Write header
        csvWriter.writeNext(new String[]{"Date", "Type", "Account", "Category", "Amount", "Description"});

        // Write data
        for (Transaction t : transactions) {
            csvWriter.writeNext(new String[]{
                    t.getDateTime().toString(),
                    t.getType().toString(),
                    t.getAccount().getName(),
                    t.getCategory() != null ? t.getCategory().getName() : "",
                    t.getAmount().toString(),
                    t.getDescription()
            });
        }

        return writer.toString().getBytes();
    }

    private byte[] generatePDF(List<Transaction> transactions) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Create fonts
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.COURIER);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(boldFont, 12);
            contentStream.newLineAtOffset(25, 700);
            contentStream.showText("Transaction Report");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(regularFont, 10);

            // Write data
            for (Transaction t : transactions) {
                String line = String.format("%s %s %s %s %s %s",
                        t.getDateTime().toString(),
                        t.getType().toString(),
                        t.getAccount().getName(),
                        (t.getCategory() != null ? t.getCategory().getName() : ""),
                        t.getAmount().toString(),
                        t.getDescription());

                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -15);
            }

            contentStream.endText();
            contentStream.close();
            document.save(outputStream);
            document.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private byte[] generateExcel(List<Transaction> transactions) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transaction Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Type", "Account", "Category", "Amount", "Description"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getDateTime().toString());
                row.createCell(1).setCellValue(t.getType().toString());
                row.createCell(2).setCellValue(t.getAccount().getName());
                row.createCell(3).setCellValue(t.getCategory() != null ? t.getCategory().getName() : "");
                row.createCell(4).setCellValue(t.getAmount().doubleValue());
                row.createCell(5).setCellValue(t.getDescription());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel report", e);
        }
    }
}