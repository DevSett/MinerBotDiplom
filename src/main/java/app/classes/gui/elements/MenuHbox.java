//package app.classes.gui.elements;
//
//import com.jfoenix.controls.JFXButton;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import lombok.Getter;
//
//public class MenuHbox extends HBox {
//    @Getter
//    private JFXButton buttonMainMenu;
//    @Getter
//    private JFXButton buttonRigMenu;
//
//    public MenuHbox() {
//        super(10);
//
//        buttonMainMenu = new JFXButton("Главное меню");
//        buttonMainMenu.setMaxWidth(Double.MAX_VALUE);
//        buttonMainMenu.setButtonType(JFXButton.ButtonType.RAISED);
//        HBox.setHgrow(buttonMainMenu, Priority.ALWAYS);
//        buttonMainMenu.setDisable(true);
//
//
//        buttonRigMenu = new JFXButton("Редактирование риг");
//        buttonRigMenu.setMaxWidth(Double.MAX_VALUE);
//        buttonRigMenu.setButtonType(JFXButton.ButtonType.RAISED);
//        HBox.setHgrow(buttonRigMenu, Priority.ALWAYS);
//    }
//
//
//}
