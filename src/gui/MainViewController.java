package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	// quando chamos ele vai abrir uma nova tela
	// o synchoronized serve pra tela não apresentar algum erro inesperado, pois como telas são multithreading pode ocorrer
	// dela começar a executar e ser interrompida por outras
	private synchronized void loadView(String absoluteName) {
		try {
			// instancia uma nova tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// chama cena pra abrir o about dentro da janela principal
			Scene mainScene = Main.getMainScene();
			
			// o getRoot pega a tag ScrollPane, por isto existe um casting pra isto
			// o getContent pega o conteúdo do ScroolPane, que no caso é um VBox, por isto o casting
			VBox mainVBox = (VBox)( (ScrollPane) mainScene.getRoot() ).getContent();
			
			// getChildren.get(0) pega o MenuBar
			Node mainMenu = mainVBox.getChildren().get(0);
			
			// limpa todos os filhos do mainVBox
			mainVBox.getChildren().clear();
			
			// adiciona somente o MenuBar no children
			mainVBox.getChildren().add(mainMenu);
			
			// adiciona todos os filhos do newVBox no children
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
