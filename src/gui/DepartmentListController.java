package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	// fa�o uma inje��o de depend�ncia, � uma boa pr�tica, pois n�o instancio o departmentService na hora que declarei ele
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// s� o fato de declarar as colunas n�o quer dizer que j� est� pronto, tem que declara este met�do
		initializeNodes();
	}

	// � uma padr�o da JavaFX
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// o tableView n�o est� acompanhando a tela inteira. Pra que ele possa acompanhar, eu fa�o as duas linhas abaixo
		// a primeira acessa a classe principal e pega a janela principal
		Stage stage = (Stage) Main.getMainScene().getWindow();
		
		// a segunda seta no tableView a mesma altura que est� na classe principal
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		tableViewDepartment.prefWidthProperty().bind(stage.widthProperty());
	}
	
	public void updateTableView() {
		// caso o programado esque�a de chamar o setDepartmentService
		if(departmentService == null) {
			throw new IllegalStateException("DepartmentService est� nulo");
		}
		
		// busco todos os deptos
		List<Department> departmentList = departmentService.findAll();
		
		// seto meu observableList com o todos os deptos buscados
		listDepartment = FXCollections.observableArrayList(departmentList);
		
		// coloco meu observableList no tableView
		tableViewDepartment.setItems(listDepartment);
	}

}
