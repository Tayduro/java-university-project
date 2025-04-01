package com.example.rozetkaapp.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rozetkaapp.model.Product;

@Service
public class ExcelExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExportService.class);

    public byte[] exportProductsToExcel(List<Product> products) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Назва");
            headerRow.createCell(2).setCellValue("Ціна");
            headerRow.createCell(3).setCellValue("Наявність");
            headerRow.createCell(4).setCellValue("Посилання");

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getAvailability() ? "В наявності" : "Немає");
                row.createCell(4).setCellValue(product.getLink());
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("Помилка під час експорту продуктів у Excel", e);
            throw new RuntimeException("Не вдалося експортувати продукти у файл Excel", e);
        }
    }
}
