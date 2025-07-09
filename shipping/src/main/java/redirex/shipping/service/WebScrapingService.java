package redirex.shipping.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebScrapingService {
    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);

    private static final Pattern PRICE_PATTERN = Pattern.compile("[0-9]+([.,][0-9]{1,2})?");

    public BigDecimal scrapeProductPrice(String productUrl) {
        try {
            logger.debug("Conectando à URL para capturar preço: {}", productUrl);
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(15000)
                    .ignoreHttpErrors(true)
                    .get();

            // Seletores para Weidian e Taobao
            String selector = ".goods-price__value, span.price-current, [class*='price-current'], [class*='price']:not([class*='original']), .tm-price, .price.g_price, [class*='price-strong']";
            Element priceElement = doc.selectFirst(selector);

            if (priceElement == null) {
                logger.error("Elemento de preço não encontrado na URL: {}", productUrl);
                throw new RuntimeException("Elemento de preço não encontrado");
            }

            String priceText = priceElement.text().trim();
            logger.debug("Texto bruto do preço extraído: {}", priceText);

            String cleanedPrice = extractPrice(priceText);
            logger.debug("Texto do preço limpo e normalizado: {}", cleanedPrice);

            BigDecimal price = new BigDecimal(cleanedPrice);
            logger.info("Preço extraído com sucesso: {} para URL: {}", price, productUrl);
            return price;

        } catch (Exception e) {
            logger.error("Falha ao capturar preço do produto na URL: {}", productUrl, e);
            throw new RuntimeException("Não foi possível obter o preço do produto: " + e.getMessage(), e);
        }
    }

    /**
     * @param priceText O texto extraído do elemento HTML.
     * @return Uma string representando o preço, pronta para ser convertida para BigDecimal.
     */
    private String extractPrice(String priceText) {
        // Remove caracteres não numéricos, exceto ponto e vírgula, para lidar com formatos como ¥ ou outros símbolos
        String cleanedText = priceText.replaceAll("[^0-9,.]", "");
        Matcher matcher = PRICE_PATTERN.matcher(cleanedText);

        if (matcher.find()) {
            // Pega a primeira ocorrência que corresponde ao padrão
            String foundPrice = matcher.group(0);
            return foundPrice.replace(',', '.');
        } else {
            String errorMessage = "Nenhum padrão de preço válido encontrado no texto: " + priceText;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}