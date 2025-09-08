package redirex.shipping.enums;

public enum SizeEnum {
    // Clothes
    SIZE_CLOTHES_PP("Clothes", "PP", null),
    SIZE_CLOTHES_P("Clothes", "P", null),
    SIZE_CLOTHES_M("Clothes", "M", null),
    SIZE_CLOTHES_G("Clothes", "G", null),
    SIZE_CLOTHES_GG("Clothes", "GG", null),
    SIZE_CLOTHES_GGG("Clothes", "GGG", null),
    SIZE_CLOTHES_XS("Clothes", "XS", null),
    SIZE_CLOTHES_S("Clothes", "S", null),
    SIZE_CLOTHES_L("Clothes", "L", null),
    SIZE_CLOTHES_XG("Clothes", "XG", null),

    // Shoes (com valores em cm aproximados)
    SIZE_SHOES_34("Shoes", "34", 22.0),
    SIZE_SHOES_34_5("Shoes", "34.5", 22.5),
    SIZE_SHOES_35("Shoes", "35", 23.0),
    SIZE_SHOES_35_5("Shoes", "35.5", 23.5),
    SIZE_SHOES_36("Shoes", "36", 24.0),
    SIZE_SHOES_36_5("Shoes", "36.5", 24.5),
    SIZE_SHOES_37("Shoes", "37", 25.0),
    SIZE_SHOES_37_5("Shoes", "37.5", 25.5),
    SIZE_SHOES_38("Shoes", "38", 26.0),
    SIZE_SHOES_38_5("Shoes", "38.5", 26.5),
    SIZE_SHOES_39("Shoes", "39", 27.0),
    SIZE_SHOES_39_5("Shoes", "39.5", 27.5),
    SIZE_SHOES_40("Shoes", "40", 28.0),
    SIZE_SHOES_40_5("Shoes", "40.5", 28.5),
    SIZE_SHOES_41("Shoes", "41", 29.0),
    SIZE_SHOES_41_5("Shoes", "41.5", 29.5),
    SIZE_SHOES_42("Shoes", "42", 30.0),
    SIZE_SHOES_42_5("Shoes", "42.5", 30.5),
    SIZE_SHOES_43("Shoes", "43", 31.0),
    SIZE_SHOES_43_5("Shoes", "43.5", 31.5),
    SIZE_SHOES_44("Shoes", "44", 32.0),
    SIZE_SHOES_44_5("Shoes", "44.5", 32.5),
    SIZE_SHOES_45("Shoes", "45", 33.0),
    SIZE_SHOES_45_5("Shoes", "45.5", 33.5),
    SIZE_SHOES_46("Shoes", "46", 34.0),
    SIZE_SHOES_46_5("Shoes", "46.5", 34.5);

    private final String category;
    private final String label;
    private final Double cm;

    SizeEnum(String category, String label, Double cm) {
        this.category = category;
        this.label = label;
        this.cm = cm;
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public Double getCm() {
        return cm;
    }
}
