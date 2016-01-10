/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package departments.management.system;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author orcl
 */
public class DepartmentsManagementSystem extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DepartmentsManagementSystemController customControl = new DepartmentsManagementSystemController();
        Scene scene = new Scene(customControl);
        scene.getStylesheets().add(this.getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Custom Control");
        stage.setWidth(1280);
        stage.setHeight(400);
        

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                DataSource.closeConnection();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
