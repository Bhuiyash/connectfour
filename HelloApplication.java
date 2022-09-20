package com.example.connectfour;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application
{
    private HelloController helloController;//hellocontroller
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        GridPane rootnode= fxmlLoader.load();
        helloController =fxmlLoader.getController();
        helloController.createPlayground();
        //MENU BAR METHOD CALL
        MenuBar menuBar=createmenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        Pane menuPane  = (Pane) rootnode.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootnode);
        stage.setTitle("CONNECT 4!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    //CREATE THE MENU FOR THE USER
    private MenuBar createmenu() {
        //CREATE MENUS
        Menu file=new Menu("file");
        Menu help=new Menu("help");
        //DECLARE ITEMS TO FILE MENU
        MenuItem newgame=new MenuItem("New Game");
        MenuItem resetgame= new MenuItem("Reset Game");
        SeparatorMenuItem separatorMenuItem= new SeparatorMenuItem();
        MenuItem quitgame=new MenuItem("Exit Game");
        //DECLARE ITEMS TO HELP MENU
        MenuItem aboutapp=new MenuItem("About Connect 4");
        MenuItem contact=new MenuItem("Customer Care");
        //get and add
        file.getItems().addAll(newgame,resetgame,separatorMenuItem,quitgame);
        help.getItems().addAll(aboutapp,contact);
        //click events on items
        resetgame.setOnAction(actionEvent -> {
            resetGame();
            System.out.println("resetgame clicked...");});


        newgame.setOnAction(actionEvent -> {helloController.resetGame();System.out.println("new game item clicked...");});
        quitgame.setOnAction(actionEvent ->{Platform.exit();System.out.println("game has stopped...");});
        aboutapp.setOnAction(actionEvent -> aboutapp());
        contact.setOnAction(actionEvent -> {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contact help centre");
            alert.setHeaderText("Customer care support");
            alert.setContentText("Email at bhuiyashkr@gmail.com " +
                    " Will reach to you shortly..");
            alert.show();
        });
        //create menubar
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(file,help);
        return menuBar;
    }
    //SET RESET GAME
    private void resetGame()
    {
        this.helloController.resetGame();
    }
    //ABOUT APP METHOD
    private void aboutapp()
    {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect four");
        alert.setHeaderText("CONNECT FOUR");
        alert.setContentText("Connect Four (also known as Connect 4," +
                " Four Up, Plot Four, Find Four, Captain's Mistress, \n Four in a Row, Drop Four, and Gravitrips in the Soviet Union) is a two-player connection board game, in which the players choose a color and then take turns dropping colored tokens into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the lowest available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own tokens. Connect Four is a solved game. The first player can always win by playing the right moves.\n" +
                "\n");
        alert.show();
    }
    //MAIN LAUNCH METHOD
    public static void main(String[] args)
    {
        launch();
    }
}