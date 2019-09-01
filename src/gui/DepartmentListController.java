package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	// pra cada coluna preciso de um tableColumn
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
	}

}
