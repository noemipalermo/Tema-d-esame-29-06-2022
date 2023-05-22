/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.itunes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.BilancioAlbum;
import it.polito.tdp.itunes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnAdiacenze"
    private Button btnAdiacenze; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbA1"
    private ComboBox<Album> cmbA1; // Value injected by FXMLLoader

    @FXML // fx:id="cmbA2"
    private ComboBox<Album> cmbA2; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML
    void doCalcolaAdiacenze(ActionEvent event) {
    	
    	Album a1 = cmbA1.getValue();
    	
    	if(a1==null) {
    		this.txtResult.setText("Seleziona un Album dalla comboBox");
    		return;
    	}
    	
    	List<BilancioAlbum> bilanci = this.model.getAdiacenti(a1);
    	
    	this.txtResult.setText("Successori del nodo "+a1+"\n");
    	
    	for(BilancioAlbum b : bilanci)
    		this.txtResult.appendText(b+"\n");
    	
    	
    	
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	
    	if (!(model.getNumVertices()>0)) {
    		txtResult.setText("Graph not created.");
    		return;
    	}
    	
    	String input = this.txtX.getText();
    	
    	if(input == "") {
    		this.txtResult.setText("Inserire un numero");
    		return;
    	}
    	
    	try {
    		int inputNum = Integer.parseInt(input);
    		
    		Album source = cmbA1.getValue();
    		Album target = this.cmbA2.getValue();
    		
    		List<Album> path = this.model.getPath(source, target, inputNum);
    		if(source==null  || target ==null) {
        		this.txtResult.setText("Seleziona Album dalle comboBox");
        		return;
        	}
    		
    		if(path.isEmpty()) {
    			this.txtResult.appendText("Non esiste un percorso tra "+source+" e "+target+"\n");
    			return;
    		}
    		
    		this.txtResult.setText("Percorso tra "+source+" e "+target+"\n");
    		
    		for(Album a: path) {
    			this.txtResult.appendText(a+"\n");
    		}
    		
    	}catch(NumberFormatException e){
    		this.txtResult.setText("Inserire un numero valido");
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
   
    	String input = this.txtN.getText();
    	
    	if(input == "") {
    		this.txtResult.setText("Inserire un numero");
    		return;
    	}
    	
    	try {
    		int inputNum = Integer.parseInt(input);
    		model.buldGraph(inputNum);
    		int numV = model.getNumVertices();
    		int numE = model.getNumEdges();
    		
    		this.cmbA1.getItems().addAll(model.getVertices());
    		this.cmbA2.getItems().addAll(model.getVertices());
    		
    		this.txtResult.setText("Grafo crato correttamente \n");
    		this.txtResult.appendText("Numero di verici: "+numV+"\nNumero di archi: "+numE);
    		
    	}catch(NumberFormatException e){
    		this.txtResult.setText("Inserire un numero valido");
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnAdiacenze != null : "fx:id=\"btnAdiacenze\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbA1 != null : "fx:id=\"cmbA1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbA2 != null : "fx:id=\"cmbA2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";

    }

    
    public void setModel(Model model) {
    	this.model = model;
    }
}
