package departments.management.system;

import departments.management.system.dto.Section;
import departments.management.system.dto.Unit;
import departments.management.system.dto.UnitSection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
import javafx.scene.control.ButtonBar.ButtonData;
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
    private final static String BUTTON_UNIT_SECTION = "BUTTON_UNIT_SECTION";

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
    private Button refreshButton;

    @FXML
    private Button addNewRecordButton;
    @FXML
    private Button deleteRecodButton;

    private Button currentlySelectedSection;

    private Unit currentlySelectedUnit;

    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button editSaveButton;

    @FXML
    private Button searchButton;

    @FXML
    private BorderPane borderPane;

    private boolean editMode;

    ButtonType yes = new ButtonType("نعم");
    ButtonType no = new ButtonType("لا");
    ButtonType cancel = new ButtonType("الغاء الامر", ButtonData.CANCEL_CLOSE);

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
            setInputAreasReadOnly();

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
        setComboBoxUnits(units);

    }

    private void setComboBoxUnits(List<Unit> units) {
        final ObservableList<Unit> data = FXCollections.observableArrayList(units);
        unitsComboBox.setItems(data);
        if (units.size() > 0) {
            unitsComboBox.getSelectionModel().select(0);
            currentlySelectedUnit = units.get(0);

        } else {
            currentlySelectedUnit = null;
        }
    }

    private void setCurrentlySelectedUnit(Unit currentlySelectedUnit) {

        this.currentlySelectedUnit = currentlySelectedUnit;
    }

    private void attachNewUnitSectionsToButtons() {
        if (currentlySelectedUnit != null) {
            Business business = new Business();
            for (Node node : rightSidePanel.getChildren()) {
                if (node instanceof Button) {
                    Section section = (Section) ((Button) node).getProperties().get(SECTION_ID);

                    UnitSection unitSection = business.getUnitSection(currentlySelectedUnit.getId(), section.getId());

                    ((Button) node).getProperties().put(BUTTON_UNIT_SECTION, unitSection);
                }
            }
        }

    }

    private UnitSection loadUnitSection(Integer unitId, Integer sectionId) {
        return new Business().getUnitSection(unitId, sectionId);

        //arrearsTextArea.setText(selectedUnitSection.getArrears() != null ? selectedUnitSection.getArrears() : "");
        // actionsTextArea.setText(selectedUnitSection.getActions() != null ? selectedUnitSection.getActions() : "");
    }

    private void loadSections() {

        Business business = new Business();
        List<Section> sections = business.loadAllSections();

        Unit unit = unitsComboBox.getSelectionModel().getSelectedItem();
        for (Section section : sections) {
            Button button = new Button();
            button.setText(section.getSectionName());
            if (unit != null) {
                UnitSection unitSection = business.getUnitSection(unit.getId(), section.getId());

                button.getProperties().put(BUTTON_UNIT_SECTION, unitSection);
            }
            button.getProperties().put(SECTION_ID, section);

            button.setDisable(unit == null);
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

                    if (currentlySelectedSection != null) {
                        backupCurrentlySelectedButtonData();
                    }

                    currentlySelectedSection = ((Button) event.getSource());
                    displayCurrentlySelectedButtonData();

                    refreshEditSaveButton();

                }

            });
            rightSidePanel.getChildren().add(button);
        }

    }

    private void backupCurrentlySelectedButtonData() {

        UnitSection unitsection = (UnitSection) currentlySelectedSection.getProperties().get(BUTTON_UNIT_SECTION);
        unitsection.setArrears(arrearsTextArea.getText());
        unitsection.setActions(actionsTextArea.getText());

        currentlySelectedSection.getProperties().put(BUTTON_UNIT_SECTION, unitsection);

    }

    private void displayCurrentlySelectedButtonData() {
        if (currentlySelectedSection != null) {
            UnitSection unitsection = (UnitSection) currentlySelectedSection.getProperties().get(BUTTON_UNIT_SECTION);
            arrearsTextArea.setText(unitsection.getArrears() != null ? unitsection.getArrears() : "");
            actionsTextArea.setText(unitsection.getActions() != null ? unitsection.getActions() : "");
        }
    }

    private void assignListeners() {

        addNewRecordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (editMode) {

                    Optional<ButtonType> result = displayChangeWhileEditModeDialog();
                    if (result.get() == yes) {
                        saveAllChanges();
                        reverseMode();

                    } else if (result.get() == no) {
                        reverseMode();

                    } else if (result.get() == cancel) {
                        return;

                    }

                }

                String unitName = displayInputDialog("اضافة وحدة جديدة", "اضافة وحدة جديدة", "فضلا ادخل اسم الوحدة");
                if (unitName != null) {
                    Business business = new Business();
                    currentlySelectedUnit = business.addNewUnit(unitName);
                    attachNewUnitSectionsToButtons();
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
                    clearTextAreas();

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

                    setInputAreasEditable();

                } else {
                    saveAllChanges();

                }

            }

        });

        editSaveButton.getStyleClass().add("edit");

        printButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {

                List<UnitSection> unitSections = new LinkedList<>();
                for (Node node : rightSidePanel.getChildren()) {
                    if (node instanceof Button) {
                        UnitSection unitSection = (UnitSection) ((Button) node).getProperties().get(BUTTON_UNIT_SECTION);
                        if (unitSection != null) {
                            unitSections.add(unitSection);
                        }
                    }
                }
                
                
                Utils.print(currentlySelectedUnit , unitSections);

            }
        });

        unitsComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Unit>() {

            @Override
            public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {

                if (editMode) {

                    Optional<ButtonType> result = displayChangeWhileEditModeDialog();
                    if (result.get() == yes) {
                        saveAllChanges();
                        reverseMode();

                    } else if (result.get() == no) {
                        reverseMode();

                    } else if (result.get() == cancel) {
                        unitsComboBox.getSelectionModel().select(oldValue);
                        return;

                    }
                }

                setCurrentlySelectedUnit(newValue);
                attachNewUnitSectionsToButtons();
                displayCurrentlySelectedButtonData();

                refreshNextAndBackButtons();
                refreshEditSaveButton();

            }

        });

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                loadUnits();
            }
        });

        searchButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String unitName = displayInputDialog("بحث", "بحث", "من فضلك ادخل اسم الوحدة المراد البحث عنها");
                if (unitName != null) {
                    List<Unit> units = new Business().getUnit(unitName);
                    clearTextAreas();
                    setComboBoxUnits(units);
                    refreshControls();
                }
            }
        });

    }

    private Optional<ButtonType> displayChangeWhileEditModeDialog() {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("تأكيد");
        alert.setHeaderText("تأكيد");
        alert.setContentText("انت الان علي وضع التعديل و لم تقم بحفظ التعديلات.\n هل ترغب في حفظ التعديلات");

        alert.getButtonTypes().setAll(yes, no, cancel);
        return alert.showAndWait();
    }

    private void saveAllChanges() {
        saveChangesInUnitSection();

        setInputAreasReadOnly();

    }

    private void saveChangesInUnitSection() {

        backupCurrentlySelectedButtonData();

        Business business = new Business();
        for (Node node : rightSidePanel.getChildren()) {
            if (node instanceof Button) {
                UnitSection unitSection = (UnitSection) ((Button) node).getProperties().get(BUTTON_UNIT_SECTION);
                if (unitSection != null) {
                    business.updateUnitSection(unitSection);
                }
            }

        }

    }

    private boolean isEditMode() {

        return editMode;
    }

    private void reverseMode() {

        editMode = !editMode;

        if (editMode) {
            editSaveButton.getStyleClass().remove("edit");
            editSaveButton.getStyleClass().add("save");

        } else {
            editSaveButton.getStyleClass().add("edit");
            editSaveButton.getStyleClass().remove("save");
        }
    }

    private void deleteCurrentlySelectedUnit() {
        new Business().deleteUnit(unitsComboBox.getSelectionModel().getSelectedItem());
        currentlySelectedUnit = unitsComboBox.getSelectionModel().getSelectedItem();

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

        refreshControls();

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

    private String displayInputDialog(String title, String headerText, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("اضافة وحدة جديدة");
        dialog.setHeaderText("اضافة وحدة جديدة");
        dialog.setContentText("فضلا ادخل اسم الوحدة");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String unitName = result.get();

            if (unitName == null && unitName.trim().length() == 0) {
                return displayInputDialog(title, headerText, content);
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
        editSaveButton.setDisable(unitsComboBox.getSelectionModel().isEmpty() || currentlySelectedSection == null);
        printButton.setDisable(unitsComboBox.getSelectionModel().isEmpty() || currentlySelectedSection == null);
    }

    private void refreshRightSideMenuButtons() {
        for (Node node : rightSidePanel.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(currentlySelectedUnit == null);
            }
        }

    }

    private void refreshControls() {
        deleteRecodButton.setDisable(unitsComboBox.getSelectionModel().isEmpty());
        printButton.setDisable(unitsComboBox.getSelectionModel().isEmpty());

        refreshNextAndBackButtons();
        refreshEditSaveButton();
        refreshRightSideMenuButtons();
    }

    private void clearTextAreas() {
        actionsTextArea.setText("");
        arrearsTextArea.setText("");
    }

}
