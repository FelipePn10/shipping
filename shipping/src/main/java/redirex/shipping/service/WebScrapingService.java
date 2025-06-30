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
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\d+([.,]\\d{1,2})?");

    public BigDecimal scrapeWeidianProductPrice(String productUrl) {
        try {
            logger.debug("Conectando à URL para capturar preço: {}", productUrl);
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            // Seletor mais específico para o preço no Weidian
            Element priceElement = doc.selectFirst("span.price, .goods-price__value, .price-value, [class*='price']");

            if (priceElement == null) {
                logger.error("Elemento de preço não encontrado na URL: {}", productUrl);
                throw new RuntimeException("Elemento de preço não encontrado");
            }

            String priceText = priceElement.text().trim();
            logger.debug("Texto bruto do preço extraído: {}", priceText);

            // Limpar e normalizar o texto do preço
            String cleanedPrice = cleanPriceText(priceText);
            logger.debug("Texto do preço limpo: {}", cleanedPrice);

            // Validar e converter para BigDecimal
            BigDecimal price = parsePrice(cleanedPrice);
            logger.info("Preço extraído com sucesso: {} para URL: {}", price, productUrl);
            return price;

        } catch (Exception e) {
            logger.error("Falha ao capturar preço do produto na URL: {}", productUrl, e);
            throw new RuntimeException("Não foi possível obter o preço do produto: " + e.getMessage(), e);
        }
    }

    private String cleanPriceText(String priceText) {
        // Remover símbolos de moeda, espaços e outros caracteres não numéricos, exceto ponto/vírgula
        String cleaned = priceText.replaceAll("[^0-9,.]", "");
        // Substituir vírgula por ponto para padronizar o formato decimal
        cleaned = cleaned.replace(",", ".");
        // Remover múltiplos pontos, mantendo apenas o último
        int lastDotIndex = cleaned.lastIndexOf(".");
        if (lastDotIndex != -1) {
            String beforeDot = cleaned.substring(0, lastDotIndex).replaceAll("\\.", "");
            cleaned = beforeDot + cleaned.substring(lastDotIndex);
        }
        return cleaned;
    }

    private BigDecimal parsePrice(String cleanedPrice) {
        // Validar o formato do preço com regex
        Matcher matcher = PRICE_PATTERN.matcher(cleanedPrice);
        if (!matcher.matches()) {
            logger.error("Formato de preço inválido: {}", cleanedPrice);
            throw new IllegalArgumentException("Formato de preço inválido: " + cleanedPrice);
        }

        try {
            return new BigDecimal(cleanedPrice);
        } catch (NumberFormatException e) {
            logger.error("Erro ao converter preço para BigDecimal: {}", cleanedPrice, e);
            throw new IllegalArgumentException("Não foi possível converter o preço: " + cleanedPrice, e);
        }
    }
}