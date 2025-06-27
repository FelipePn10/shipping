package redirex.shipping.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WebScrapingService {
    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);

    public BigDecimal scrapeWeidianProductPrice(String productUrl) {
        try {
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // Seletor específico do Weidian (pode precisar de ajustes depopis)
            Element priceElement = doc.selectFirst("span[class*=price], .goods-price, .price-value");

            if (priceElement == null) {
                throw new RuntimeException("Price element is null");
            }

            String priceText = priceElement.text()
                    .replaceAll("[^0-9.,]", "")
                    .replace(",", ".");
            return new BigDecimal(priceText);
        }  catch (Exception e) {
            logger.error("Falha ao capturar preço do produto: {}", productUrl, e);
            throw new RuntimeException("Não foi possível obter o preço do produto", e);
        }
    }
}
