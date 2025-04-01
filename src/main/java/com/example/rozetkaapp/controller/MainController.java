package com.example.rozetkaapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.rozetkaapp.model.Product;
import com.example.rozetkaapp.repository.ProductRepository;
import com.example.rozetkaapp.service.BitcoinPriceService;
import com.example.rozetkaapp.service.ExcelExportService;
import com.example.rozetkaapp.service.RozetkaParserService;

@Controller
public class MainController {

    private final RozetkaParserService rozetkaParserService;
    private final BitcoinPriceService bitcoinPriceService;
    private final ExcelExportService excelExportService;
    private final ProductRepository productRepository;

    public MainController(RozetkaParserService rozetkaParserService, BitcoinPriceService bitcoinPriceService, ExcelExportService excelExportService, ProductRepository productRepository) {
        this.rozetkaParserService = rozetkaParserService;
        this.bitcoinPriceService = bitcoinPriceService;
        this.excelExportService = excelExportService;
        this.productRepository = productRepository;
    }

    // Home page – displays a list of products and exchange rates
    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        String bitcoinPrice = bitcoinPriceService.getBitcoinUahPrice();
        model.addAttribute("bitcoinPrice", bitcoinPrice);
        return "index";
    }

    // Endpoint for starting parsing
    @GetMapping("/parse")
    public String parseProducts(Model model) {
        try {
            List<Product> products = rozetkaParserService.parseProducts();
            model.addAttribute("products", products);
            model.addAttribute("message", "Парсинг пройшов успішно!");
        } catch (IOException e) {
            model.addAttribute("message", "Помилка при парсингу: " + e.getMessage());
        }
        return "parse";
    }

    // Export data to Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        List<Product> products = productRepository.findAll();
        byte[] bytes = excelExportService.exportProductsToExcel(products);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    // Getting the Bitcoin rate
    @GetMapping("/bitcoinPrice")
    public String getExchange(Model model) {
        String bitcoinPrice = bitcoinPriceService.getBitcoinUahPrice();
        model.addAttribute("bitcoinPrice", bitcoinPrice);
        return "bitcoinPrice";  // Return the name of the HTML template (bitcoinPrice.html)
    }


}
