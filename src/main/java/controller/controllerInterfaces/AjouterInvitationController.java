package controller.controllerInterfaces;

import Gui.Main;
import controller.Methods.GeneralMethods;
import controller.Methods.GeneralMethodsImpl;
import controller.ModelTabs.InvitationTable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public  class AjouterInvitationController  implements Initializable {
    @FXML
    private Button ButtonAnnuler;

    @FXML
    private Button ButtonRetour;

    @FXML
    private Button ButtonValider;


    @FXML
    private ComboBox<String> ComboboxPortefeuille;

    @FXML
    private TextField identifiant;

    @FXML
    private CheckBox roleEcriture;

    @FXML
    private CheckBox roleLecture;

    private Stage dialogStage;
    private InvitationTable invitationTableElement;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public InvitationTable getInvitationTableElement() {
        return invitationTableElement;
    }

    public void setInvitationTableElement(InvitationTable invitationTableElement) {
        this.invitationTableElement = invitationTableElement;
    }

    @FXML
    void ajouterInvitation(ActionEvent event) {
        Main.ajouterInteractionAuClic(ButtonValider);
        String role ;
        boolean ecriture = roleEcriture.isSelected();
        boolean lecture = roleLecture.isSelected();
        role = show("lecture",ecriture);
        if (ecriture && lecture){
            role+=show("-ecriture",ecriture);
        }
        role = show("ecriture",ecriture);
        Date actualDate = new Date();
        JSONObject invitation = new JSONObject();
        invitation.put("name",identifiant.getText());
        invitation.put("dateInvitation",actualDate);
        JSONObject wallet = generalMethods.findUnique("wallet/name/"+ComboboxPortefeuille.getValue());
        if (wallet.isEmpty()){
            JOptionPane.showMessageDialog(null,"Cet identifiant n'existe pas dans le systeme");
        }else{
            invitation.put("wallet",wallet);
            invitation.put("user",Main.currentClient);
            invitation.put("droitAcces",role);
            invitation.put("statutInvitation","envoyee");

            JSONObject json = generalMethods.createObject(invitation,"invite");
            if (json.isEmpty()){
                JOptionPane.showMessageDialog(null,"Envoie de l'invitation echoue");
            }else{
                JOptionPane.showMessageDialog(null,"Creation invitation reussie");
                InvitationController.invitationRecues.add(new InvitationTable(json,true));

            }
        }

    }

    String show(String s , boolean b){
        if (b) return s ;
        return "";
    }
    @FXML
    void annuler(ActionEvent event) {
        Main.ajouterInteractionAuClic(ButtonAnnuler);
        roleEcriture.setSelected(false);
        roleLecture.setSelected(false);
        identifiant.setText("");
    }

    @FXML
    void retour(ActionEvent event) {
        Main.ajouterInteractionAuClic(ButtonRetour);
        Main.newStage.close();
    }
    GeneralMethods generalMethods = new GeneralMethodsImpl();
    @Override
    public void initialize(URL url, ResourceBundle rb){
        JSONArray wallets = generalMethods.find("invite/user/"+ Main.currentClient.getString("identifiant"));
        for (int i = 0 ; i<wallets.length();i++){
            ComboboxPortefeuille.getItems().add(wallets.getJSONObject(i).getString("name"));
        }
        if (invitationTableElement!=null){
            ComboboxPortefeuille.getSelectionModel().select(invitationTableElement.getPortefeuille());
            identifiant.setText(invitationTableElement.getDestinataire());
        }
    }
}