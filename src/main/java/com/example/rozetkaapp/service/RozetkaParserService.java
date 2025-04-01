package com.example.rozetkaapp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.example.rozetkaapp.model.Product;
import com.example.rozetkaapp.repository.ProductRepository;

@Service
public class RozetkaParserService {

    private static final String ROZETKA_WIRELESS_AP_URL = "https://rozetka.com.ua/ua/wireless-access-points/c80195/";

    private final ProductRepository productRepository;

    public RozetkaParserService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> parseProducts() throws IOException {
        List<Product> products = new ArrayList<>();
        //Using the JSoup library to retrieve an HTML page from a URL and parse it
        Document doc = Jsoup.connect(ROZETKA_WIRELESS_AP_URL).get();

        // Receive product elements
        Elements productElements = doc.select("div.goods-tile");

        for (Element element : productElements) {
            boolean available = !element.select("div.goods-tile__availability--out-of-stock").hasText();
            if (!available) {
                continue;
            }

            Product product = new Product();
            String name = element.select("span.goods-tile__title").text();
            String priceStr = element.select("span.goods-tile__price-value").text().replaceAll("[^\\d.]", "");
            Double price;
            try {
                price = Double.valueOf(priceStr);
            } catch (NumberFormatException e) {
                price = 0.0; // If the price hasn't gone up
            }
            String link = element.select("a").attr("href");

            product.setName(name);
            product.setPrice(price);
            product.setAvailability(available);
            product.setLink(link);

            products.add(product);
        }
        // Save to database
        productRepository.saveAll(products);
        return products;
    }
}
