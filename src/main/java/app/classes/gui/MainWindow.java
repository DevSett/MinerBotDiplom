//package app.classes.gui;
//
//import app.classes.gui.elements.MenuHbox;
//import app.classes.models.Property;
//import javafx.geometry.Insets;
//import javafx.scene.Node;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component("mainWindow")
//public class MainWindow extends BorderPane {
//    @Autowired
//    MenuHbox menuHbox;
//
//    public MainWindow() {
//        Insets insets = new Insets(5, 5, 5, 5);
//        BorderPane.setMargin(menuHbox, insets);
//        this.setTop(menuHbox);
//
//        this.setCenter(getCenterPane());
//        this.setBottom(getAlertBottom());
//    }
//
//}
