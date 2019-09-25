package ru.allformine.afmuf.alert;

public enum AlertMod {
    UNKNOWN("Unknown mod"),

    FURNITURE("Crayfish Furniture");

    private String url;

    AlertMod(String modName) {
        this.url = modName;
    }

    public String getModName() {
        return url;
    }
}
