module org.mossimo.finalprojectsem3final {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.mossimo.finalprojectsem3final to javafx.fxml;
    opens org.mossimo.finalprojectsem3final.controller to javafx.fxml;
    exports org.mossimo.finalprojectsem3final;
    exports org.mossimo.finalprojectsem3final.controller;
}