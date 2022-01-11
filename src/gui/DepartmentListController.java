package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
	
	private DepartmentService service;
	
	@FXML
	private Button btNew;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		criarFormulario(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Macete para fazer a TableView acompanhar a altura da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void atualizarTabela() {
		if(service == null)
			throw new IllegalStateException("Service was null!");
		
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		
		tableViewDepartment.setItems(obsList);
		
		initEditButtons();
		initRemoveButtons();
	}
	
	// -Como parametro, uma referencia para o Stage da janela que criou a janela de dialogo
	private void criarFormulario(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.atualizarDadosDoForm();
			
			// criar uma outra janela
			Stage dialogStage = new Stage();
			dialogStage.setScene(new Scene(pane));
			
			// Impedir a tela de ser Rendimencionada "false"
			dialogStage.setResizable(false);
			
			// Indicar quem é o Stage pai dessa janela
			dialogStage.initOwner(parentStage);
			
			// Dizer se a janela vai ser modal ou vai ter outro comportamento
			/* ...Modality.WINDOW_MODAL... mantém a tela travada.
			 *  enquanto não for fechada, não poderá acessar a janela anterior/ que está atrás*/
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}catch(IOException e) {
			Alerts.mostrarAlerta("IO Exception", "Erro ao Carregar tela!", e.getMessage(), AlertType.ERROR);
			//e.printStackTrace();
		}
	}

	@Override
	public void onDadosAlterados() {
		atualizarTabela();
		
	}
	
	// Metodo que gera butões para a coluna "tableColumnEDIT" e cria a ação dos butões
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
					event -> criarFormulario(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event))
				);
			}
		});
	}
	
	// Metodo que gera butões para a coluna "tableColumnREMOVE" e cria a ação dos butões
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removerEntidade(obj));
			}
		});
	}

	private void removerEntidade(Department obj) {
		Optional<ButtonType> result = Alerts.mostrarConfirmacao("Confirmação", "Tem certeza que deseja deletar?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null)
				throw new IllegalStateException("Service was null");
			try {
				service.remove(obj);
				atualizarTabela();
			//}catch(DbIntegrityException e){...
			}catch(DbException e) {
				Alerts.mostrarAlerta("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	

}
