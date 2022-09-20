package com.example.connectfour;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
//class HelloCounter starts
public class HelloController implements Initializable
{
    //constants values throughout the game...
    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=80;
    private static final String Discone="Red";
    private static final String Disctwo="Yellow";
    //declare values that might change...
    private static String PLAYER_ONE="Player One's";
    private static String PLAYER_TWO="Player Two's";
    private static boolean is_playerone_turn=true;
    private boolean isallowedtoinsert=true;//flag to avoid double click events...
    private Disc[][] insertedDiscsarray= new Disc[ROWS][COLUMNS];  //for structural changes...
    //declare all the fx id's
    @FXML
    public GridPane rootgridpane;
    @FXML
    public Pane newpaneup;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public VBox newvboxdiscs;
    @FXML
    public Label playeronename;
    @FXML
    public TextField playeronetextfield;
    @FXML
    public TextField playertwotextfield;
    @FXML
    public Button Enternameplayers;

    //CREATE PLAYGROUND FUNCTION
    public void createPlayground()
    {
        Shape rectanglewithhoels=Creategamestructuralgrid();
        rootgridpane.add(rectanglewithhoels,0,1);
        ArrayList<Rectangle> rectangleArrayList=Clickablecolumns();
        for (Rectangle rectangle:rectangleArrayList
             ) {
            rootgridpane.add(rectangle,0,1);
        }
    }
    //STRUCTURE MAKING OF THE GAME
    private Shape Creategamestructuralgrid()
    {
        Shape rectanglewithhoels= new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for (int row=0;row<ROWS;row++)
        {
            for(int col=0;col<COLUMNS;col++)
            {
                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);
                circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row *(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                rectanglewithhoels=Shape.subtract(rectanglewithhoels,circle);
            }
        }
        rectanglewithhoels.setFill(Color.GREEN);
        return rectanglewithhoels;
    }
    //CLICKABLE COLUMNS
    private ArrayList<Rectangle> Clickablecolumns()
    {
        ArrayList<Rectangle> rectangleArrayList=new ArrayList<>();
        for (int col = 0; col < COLUMNS; col++)
        {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));
            rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+ CIRCLE_DIAMETER / 4);
            final int x=col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if (isallowedtoinsert) {
                    isallowedtoinsert = false;//double click can't be used while a disc is falling
                    insertDisc(new Disc(is_playerone_turn), x);
                }
            });

            rectangleArrayList.add(rectangle);
        }
        return rectangleArrayList;
    }
    //METHOD TO INSERT DISCS
    private void insertDisc(Disc disc,int column)
    {
        int row = ROWS-1;
        while (row >= 0){
            if(getDiscIfPresent(row,column)==null)
                break;
            row--;
        }
        if (row<0)// if row equals zero we can't insert in that column.
        return;
        int currentrow=row;
        insertedDiscsarray[row][column]=disc; // for structural changes; for developers
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
            translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
            translateTransition.setOnFinished(actionEvent ->
            {
                isallowedtoinsert=true;//disc is dropped, next player continues...
                if(gameEnded(currentrow,column))
                {
                    gameOver();
                }
                is_playerone_turn=! is_playerone_turn;
                playeronename.setText(is_playerone_turn?PLAYER_ONE:PLAYER_TWO);
            } );
            translateTransition.play();
    }
    //METHODS CALLED FROM CLICK ITEMS
    private void gameOver()
    {
        String plone="Player one"; String pltwo="Player two";
        String winner= is_playerone_turn? plone:pltwo;
        System.out.println("winner is "+winner);
        Alert alert= new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("congratulation we have a winner..");
        alert.setTitle("Winner declared...");
        alert.setContentText(winner);
        ButtonType clickreset= new ButtonType("Reset Game");
        alert.getButtonTypes().add(clickreset);
        Platform.runLater(()->
                {

            Optional<ButtonType> clickedreset=alert.showAndWait();
            if(clickedreset.isPresent() && clickedreset.get()==clickreset)
            {
                resetGame();
            }

                }
        );
    }
    public void resetGame() {
        insertedDiscsPane.getChildren().clear();
        for (int row=0; row<insertedDiscsarray.length;row++)
        {
            for (int col=0;col<insertedDiscsarray[row].length;col++)
            {
                insertedDiscsarray[row][col]=null;//clear off the discarray...
            }
        }
        is_playerone_turn=true;//initialize to original value...
        playeronename.setText(PLAYER_ONE);//initializes to original value
        createPlayground();//creates fresh playground...
    }
    //for when the game ends this method works.
    private boolean gameEnded(int row, int column)
    {
        //range of row values=0 to 5
        // use point 2d class

        //  VERTICAL POINTS ACCESS
        List<Point2D> verticalpoints = IntStream.rangeClosed(row - 3, row + 3)//returns rangeof values 0 to 5
                .mapToObj(r -> new Point2D(r, column))//0,3  1,3  2,3  --> Point 2d
                .collect(Collectors.toList());
        // HORIZONTAL POINTS ACCESS
        List<Point2D> horizontalpoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());
        // DIAGONAL POINTS ACCESS
        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());
        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());
        boolean isEnded = checkCombinations(verticalpoints)
                ||
                checkCombinations(horizontalpoints)
                ||
                checkCombinations(diagonal1Points)
                ||
                checkCombinations(diagonal2Points);
        return isEnded;
    }
    //CHECK COMBINATIONS
    private boolean checkCombinations(List<Point2D> points)
    {
        int chain = 0;
        for (Point2D point: points)
        {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();
            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if (disc != null && disc.isPlayerOneMove == is_playerone_turn) {  // if the last inserted Disc belongs to the current player
                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }
    //TO PREVENT ARRAY OUT OF BOUNDS EXCEPTION
    private Disc getDiscIfPresent(int row, int column) {    // To prevent ArrayIndexOutOfBoundException
        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)  // If row or column index is invalid
            return null;
        return insertedDiscsarray[row][column];
    }
    //inherited disc class from class circle
    private class Disc extends  Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove? Color.valueOf(Discone): Color.valueOf(Disctwo));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
    }
}