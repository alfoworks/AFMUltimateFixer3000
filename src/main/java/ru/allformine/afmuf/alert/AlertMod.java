package ru.allformine.afmuf.alert;

public enum AlertMod {
    UNKNOWN("Unknown mod"),

    FURNITURE("Crayfish Furniture"),
    AFMSM("AFMSpaceUnionMod");

    private String url;

    AlertMod(String modName) {
        this.url = modName;
    }

    public String getModName() {
        return url;
    }
}
