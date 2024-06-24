package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import server.EchoServer;
import server.ServerUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerController implements Initializable {
	
    @FXML
    private Button btnConnect;

    @FXML
    private TextField dbhost;

    @FXML
    private Button btnDisonnect;

    @FXML
    private TextField dbusername;

    @FXML
    private PasswordField dbpass;

    @FXML
    private TextField dbscheme;

    @FXML
    private TextField dbport;

    @FXML
    private Button btnStart;

    @FXML
    private Button stopServer;

    @FXML
    private TextField serverport;
    
    @FXML
    private TextArea logUpdate;

    @FXML
    void connect(ActionEvent event) {
		ArrayList<String> str = new ArrayList<String>();
		str.add(getHost());
		str.add(getDBport());
		str.add(getScheme());
		str.add(getUsername());
		str.add(getPassword());
		ServerUI.connectToDB(str);
		if(checkLog()) {
			btnDisonnect.setDisable(false);
			btnConnect.setDisable(true);
		}
    }

    @FXML
    void disonnect(ActionEvent event) {
    	ServerUI.disconnectFromDB();
    	if(checkLog()) {
			btnDisonnect.setDisable(true);
			btnConnect.setDisable(false);
		}
    }

    @FXML
    void startServerBtn(ActionEvent event) {
    	EchoServer sv = new EchoServer(Integer.parseInt(getServerPort()),null,this);
    	ServerUI.runServer(sv);
    	
    	if(checkLog()) {
    	stopServer.setDisable(false);
    	btnStart.setDisable(true);
    	
    	}
    	
    }

    @FXML
    void stopServerBtn(ActionEvent event) {
    	ServerUI.stopServer();
    	if(checkLog()) {
    		stopServer.setDisable(true);
    		btnStart.setDisable(false);
		}
    }
    
    public void logIt(String str) {
    	logUpdate.appendText(str+"\n");
    }
    
    public boolean checkLog() {
    	String str = ServerUI.getMsg();
    	if (!str.equals("")) {
    		logIt(str);
    		if(str.contains("succsefuly"))
    			return true;
    		return false;
    	}
    	return true;
    }
	
	public String getHost() {
		return dbhost.getText();
	}
	
	public String getUsername() {
		return dbusername.getText();
	}
	
	public String getPassword() {
		return dbpass.getText();
	}
	
	public String getDBport() {
		return dbport.getText();
	}
	
	public String getScheme() {
		return dbscheme.getText();
	}
	
	public String getServerPort() {
		return serverport.getText();
	}
	
	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Server.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				System.out.println("emergency exit");
				if(ServerUI.isServerUP())
					ServerUI.stopServer();
				if(ServerUI.isDBup())
					ServerUI.disconnectFromDB();
				Platform.exit();
				System.exit(0);
			}
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		dbhost.setText("localhost"); 
		dbusername.setText("root");
		dbpass.setText("Salman16!");
		dbscheme.setText("project");
		dbport.setText("3306");
		serverport.setText("5555");
		btnDisonnect.setDisable(true);
		stopServer.setDisable(true);
	}
	
	
}
