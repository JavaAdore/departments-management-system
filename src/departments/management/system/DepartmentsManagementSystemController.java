package departments.management.system;

import departments.management.system.dto.Section;
import departments.management.system.dto.Unit;
import departments.management.system.dto.UnitSection;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Sample custom control hosting a text field and a button.
 */
public class DepartmentsManagementSystemController extends VBox {

    private final static String SECTION_ID = "SECTION_ID";
    @FXML
    private Button printButton;

    @FXML
    private Label unitNameLabel;
    @FXML
    private Label sectionLabel;
    @FXML
    private Label arrearsLabel;
    @FXML
    private Label actionLabel;

    @FXML
    private ComboBox<Unit> unitsComboBox;

    @FXML
    VBox rightSidePanel;

    @FXML
    TextArea arrearsTextArea;
    @FXML
    TextArea actionsTextArea;

    @FXML
    private Button addNewRecordButton;
    @FXML
    private Button deleteRecodButton;

    private Integer currentlySelectedSectionId;

    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button editSaveButton;
    
    @FXML
    private BorderPane borderPane;

    private boolean editMode;

    UnitSection selectedUnitSection;
    public DepartmentsManagementSystemController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home_Page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            setComponentsNames();
            assignListeners();
            loadUnits();
            loadSections();
            refreshNextAndBackButtons();
            refreshEditSaveButton();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    @FXML
    protected void doSomething() {
        System.out.println("The button was clicked!");
    }

    private void setComponentsNames() {

        sectionLabel.setText("الاقسام");

        arrearsLabel.setText("متأخرات");

        actionLabel.setText("اجراءات");;

        unitNameLabel.setText("اسم الوحدة");

    }

    private void loadUnits() {

        List<Unit> units = new Business().loadAllUnits();
        final ObservableList<Unit> data = FXCollections.observableArrayList(units);
        unitsComboBox.setItems(data);
        if (units.size() > 0) {
            unitsComboBox.getSelectionModel().select(0);

        }

        unitsComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Unit>() {

            @Override
            public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {

                if (currentlySelectedSectionId != null) {
                    loadUnitSection(newValue.getId(), currentlySelectedSectionId);
                }
                refreshNextAndBackButtons();
                refreshEditSaveButton();

            }
        });

    }

    private void loadUnitSection(Integer unitId, Integer sectionId) {
        selectedUnitSection = new Business().getUnitSection(unitId, sectionId);

        arrearsTextArea.setText(selectedUnitSection.getArrears() != null ? selectedUnitSection.getArrears() : "");

        actionsTextArea.setText(selectedUnitSection.getActions() != null ? selectedUnitSection.getActions() : "");

    }

    private void loadSections() {

        List<Section> sections = new Business().loadAllSections();

        for (Section section : sections) {
            Button button = new Button();
            button.setText(section.getSectionName());
            button.getProperties().put(SECTION_ID, section.getId());
            button.getStyleClass().add("rightSideMenuButton");
            button.setPrefSize(100, 50);
            button.setPadding(new Insets(5));
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    for (Node node : rightSidePanel.getChildren()) {
                        if (node instanceof Button) {
                            ((Button) (node)).getStyleClass().remove("selected");
                        }
                    }
                    ((Button) event.getSource()).getStyleClass().add("selected");

                    currentlySelectedSectionId = (Integer) ((Button) event.getSource()).getProperties().get(SECTION_ID);

                    Unit selectedUnit = unitsComboBox.getSelectionModel().getSelectedItem();
                    if (selectedUnit != null) {
                        loadUnitSection(selectedUnit.getId(), currentlySelectedSectionId);

                    }
                    refreshEditSaveButton();

                }

            });
            rightSidePanel.getChildren().add(button);
        }

    }

    private void assignListeners() {

        addNewRecordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String unitName = displayEnterUnitNameDialog();
                if (unitName != null) {
                    Business business = new Business();
                    business.addNewUnit(unitName);
                    refreshUI();
                    unitsComboBox.getSelectionModel().selectLast();
                }

            }
        });

        deleteRecodButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                boolean deleteUnit = displayDeleteUnitDialog();
                if (deleteUnit) {
                    deleteCurrentlySelectedUnit();
                    refreshUI();
                }

            }

        });

        nextButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                int currentlySelectedIndex = unitsComboBox.getSelectionModel().getSelectedIndex();
                unitsComboBox.getSelectionModel().select(++currentlySelectedIndex);
                refreshNextAndBackButtons();
            }
        });

        previousButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                int currentlySelectedIndex = unitsComboBox.getSelectionModel().getSelectedIndex();
                unitsComboBox.getSelectionModel().select(--currentlySelectedIndex);
                refreshNextAndBackButtons();
            }
        });

        editSaveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                reverseMode();
                if (isEditMode() == true) {

                    editSaveButton.getStyleClass().remove("edit");
                    editSaveButton.getStyleClass().add("save");
                    setInputAreasEditable();

                } else {
                    saveChangesInUnitSection();

                    editSaveButton.getStyleClass().add("edit");
                    editSaveButton.getStyleClass().remove("save");

                    setInputAreasReadOnly();

                }

            }

        });

        editSaveButton.getStyleClass().add("edit");
        
        printButton.setOnAction(new EventHandler<ActionEvent>() {

          

        public void handle(ActionEvent t) {
            try {
                // getting screen coordinates
                Bounds b = borderPane.getBoundsInParent();
                int x = (int)Math.round( b.getMinX());
                int y = (int)Math.round(b.getMaxX());
                int w = (int)Math.round(b.getWidth());
                int h = (int)Math.round(b.getHeight());
                // using ATW robot to get image
                java.awt.Robot robot = new java.awt.Robot();
                java.awt.image.BufferedImage bi = robot.createScreenCapture(new java.awt.Rectangle(x, y, w, h));
                // convert BufferedImage to javafx.scene.image.Image
                java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
                ImageIO.write(bi, "png", stream);
                Image image = new Image(new java.io.ByteArrayInputStream(stream.toByteArray()), w, h, true, true);
                // put it to clipboard
                ClipboardContent cc = new ClipboardContent();
                cc.putImage(image);
                Clipboard.getSystemClipboard().setContent(cc);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            
            }
        });
        

    }

    private void saveChangesInUnitSection() {
        selectedUnitSection.setActions(actionsTextArea.getText());
        selectedUnitSection.setArrears(arrearsTextArea.getText());

        new Business().updateUnitSection(selectedUnitSection);

    }

    private boolean isEditMode() {

        return editMode;
    }

    private void reverseMode() {
        editMode = !editMode;
    }

    private void deleteCurrentlySelectedUnit() {
        new Business().deleteUnit(unitsComboBox.getSelectionModel().getSelectedItem());

    }

    private boolean displayDeleteUnitDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("تأكيد حذف وحدة");
        alert.setHeaderText("تأكيد حذف وحدة ");
        String unitName = unitsComboBox.getSelectionModel().getSelectedItem().getUnitName();
        alert.setContentText(String.format("انت علي وشك ان تقوم بحف الوحدة (%s) \n هل تريد المتابعة", unitName));

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;

    }

    private void refreshUI() {
        loadUnits();

        deleteRecodButton.setDisable(unitsComboBox.getSelectionModel().isEmpty());
        printButton.setDisable(unitsComboBox.getSelectionModel().isEmpty());

        refreshNextAndBackButtons();
        refreshEditSaveButton();

    }

    private void refreshNextAndBackButtons() {
        boolean isEmptySelectionModel = unitsComboBox.getSelectionModel().isEmpty();
        previousButton.setDisable(isEmptySelectionModel);
        nextButton.setDisable(isEmptySelectionModel);

        if (isEmptySelectionModel == false) {
            int selectedIndex = unitsComboBox.getSelectionModel().getSelectedIndex();
            int lastIndex = unitsComboBox.getItems().size() - 1;

            previousButton.setDisable(selectedIndex == 0);
            nextButton.setDisable(selectedIndex == lastIndex);

        }

    }

    private String displayEnterUnitNameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("اضافة وحدة جديدة");
        dialog.setHeaderText("اضافة وحدة جديدة");
        dialog.setContentText("فضلا ادخل اسم الوحدة");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String unitName = result.get();

            if (unitName == null && unitName.trim().length() == 0) {
                return displayEnterUnitNameDialog();
            } else {

                return unitName;
            }

        } else {
            return null;
        }

    }

    private void setInputAreasReadOnly() {
        arrearsTextArea.setEditable(false);
        actionsTextArea.setEditable(false);
    }

    private void setInputAreasEditable() {
        arrearsTextArea.setEditable(true);
        actionsTextArea.setEditable(true);
    }

    private void refreshEditSaveButton() {
        editSaveButton.setDisable(unitsComboBox.getSelectionModel().isEmpty() || selectedUnitSection == null);
    }

}
