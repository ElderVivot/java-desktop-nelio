package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	// implemento meu DepartmentService pra poder pegar os dados dos deptos
	private DepartmentService departmentService;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	// pra cada coluna preciso de um tableColumn
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	// pra poder carregar os elementos da minha lista de Department na TableView
	private ObservableList<Department> listDepartment;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		
		// como estou criando um novo department, então o obj por enquanto vai carregar vazio
		Department obj = new Department();
		
		// preciso injetar meu obj no DepartmentFormController, pra isto, vou passar ele como argumento no createDialog
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	// faço uma injeção de dependência, é uma boa prática, pois não instancio o departmentService na hora que declarei ele
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// só o fato de declarar as colunas não quer dizer que já está pronto, tem que declara este metódo
		initializeNodes();
	}

	// é uma padrão da JavaFX
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// o tableView não está acompanhando a tela inteira. Pra que ele possa acompanhar, eu faço as duas linhas abaixo
		// a primeira acessa a classe principal e pega a janela principal
		Stage stage = (Stage) Main.getMainScene().getWindow();
		
		// a segunda seta no tableView a mesma altura que está na classe principal
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		tableViewDepartment.prefWidthProperty().bind(stage.widthProperty());
	}
	
	public void updateTableView() {
		// caso o programado esqueça de chamar o setDepartmentService
		if(departmentService == null) {
			throw new IllegalStateException("DepartmentService está nulo");
		}
		
		// busco todos os deptos
		List<Department> departmentList = departmentService.findAll();
		
		// seto meu observableList com o todos os deptos buscados
		listDepartment = FXCollections.observableArrayList(departmentList);
		
		// coloco meu observableList no tableView
		tableViewDepartment.setItems(listDepartment);
	}
	
	// quando criamos uma janela de diálogo, temos que informar ela quem criou esta janela. Por isto do argumento Stage
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			
			// o form do Department é um AnchorPane então agora tenho que chamar o Pane em vez do VBox
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch(IOException e) {
			Alerts.showAlert("IOExcepction", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		// quando houver a alteração atualizo minha lista
		updateTableView();
	}

}
