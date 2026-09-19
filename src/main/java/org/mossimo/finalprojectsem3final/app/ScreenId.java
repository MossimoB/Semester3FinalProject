package org.mossimo.finalprojectsem3final.app;

public enum ScreenId {
    /**
     * The first thing the player sees
     */
    MAIN_MENU("main-menu.fxml", "StockSim"),

    /**
     * The main playing screen
     */
    DASHBOARD("dashboard.fxml", "StockSim - Trading");

    private static final String VIEW_FOLDER = "/org/mossimo/finalprojectsem3final/view/";

    private final String fileName;
    private final String windowTitle;

    ScreenId(String fileName, String windowTitle) {
        this.fileName = fileName;
        this.windowTitle = windowTitle;
    }

    /**
     * The full classpath path, e.g. {/com/stocksim/view/dashboard.fxml}
     */
    public String getResourcePath() {
        return VIEW_FOLDER + fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
