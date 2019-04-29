package src.controller.employee;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import src.controller.Notification;
import src.model.CarInfo;


import java.util.Arrays;
import java.util.ResourceBundle;

public class PopUpWindow extends Notification {

    private TextField field;
    private Stage window;
    private Boolean changesWereSet = false;

    private ResourceBundle actualLanguage;

    /**
     * Show new window where employee can add new item.
     * @param brand brand of car
     * @param operation
     * @param text text of new item
     * @param observableList
     * @param resources actual language
     * @return
     */
    public String display(String brand, String operation, String text, ObservableList<String> observableList, ResourceBundle resources) {

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(400);
        window.setHeight(200);
        window.setResizable(false);

        actualLanguage = resources;

        Label label = new Label(text);
        field = new TextField();
        Button btnAdd = new Button(actualLanguage.getString("addButton"));


        btnAdd.setOnAction(e -> {
            do_operation(brand, operation,observableList, resources);
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,field,btnAdd);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return changesWereSet.equals(true) ? getText() : null;
    }

    /**
     * Connect to server and add new brand.
     * @param brand brand of car
     * @param operation
     * @param observableList
     * @param resources actual language
     */
    public void do_operation(String brand, String operation,  ObservableList<String> observableList, ResourceBundle resources) {

        actualLanguage = resources;

        if(getText().trim().isEmpty()) {

            showError(actualLanguage.getString("notificationNoEnterData"));

            return;
        }

        if(observableList.contains(getText())){

            showError(actualLanguage.getString("notificationAlreadyExist"));

            return;
        } else {
            CarInfo carInfo = new CarInfo();

            if (operation.equals("brand")) {
                changesWereSet = true;
                window.close();
                return;
            }

            if (brand != null){
                carInfo.setBrand(brand);
                carInfo.setModel(getText());
            }
            else {
                carInfo.setColor(getText());
            }

            String resourceURL = "http://localhost:8080/api/carInfo/";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            //headers.set("Content-Type","application/json");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<CarInfo> entity = new HttpEntity<>(carInfo,headers);

            ResponseEntity<Boolean> result;

            try {
                result = restTemplate.exchange(resourceURL, HttpMethod.POST, entity, Boolean.class);
            } catch (Exception e) {
                //LOG.log(Level.WARNING, actualLanguage.getString("notificationNoResponseServer"));
                //showError(actualLanguage.getString("notificationNoResponseServer"));
                return;
            }

            showInformation(actualLanguage.getString("notificationAdd"));

            window.close();

            changesWereSet = true;
            return;
        }
    }

    public String getText() {
        return field.getText();
    }
}
