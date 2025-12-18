package com.emperium.enums;

/**
 * Enum used to represent DOM elements scraped from {@link <a href="https://www.meteo.gr/">Meteo</a>}
 */
public enum DomElementsEnum {
    DATE ("//span[@class='dayNumbercf']"),
    MONTH("//span[@class='monthNumbercf']"),
    TIME("//td[@class='innerTableCell fulltime']"),
    TEMPERATURE("//td[@class='innerTableCell temperature tempwidth']"),
    EXTREME_TEMPERATURE("//td[@class='innerTableCell temperature tempwidth']"),
    HUMIDITY("//td[@class='innerTableCell temperature tempwidth']"),
    WIND("//td[@class='innerTableCell anemosfull']"),
    PHONOMENON("//td[@class='phenomeno-nameXS ']");

    private String element;

    DomElementsEnum(String element) {
        this.element = element;
    }

    public String getElement() {
        return element;
    }
}
