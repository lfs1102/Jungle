import animation.ScaleSelectedAnimal;
import exception.GameWinException;
import entity.Board;
import entity.animal.Animal;
import entity.tile.Tile;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by lifengshuang on 28/11/2016.
 */
public class PJ2 extends Application {

    private static final double LEFT_MARGIN = 0.14;
    private static final double RIGHT_MARGIN = 0.14;
    private static final double TOP_MARGIN = 0.02;
    private static final double BOTTOM_MARGIN = 0.02;
    private static final int ROWS = 7;
    private static final int COLS = 9;

    private ImageView background;

    private ScaleSelectedAnimal scaleAnimation = new ScaleSelectedAnimal();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 900, 600);
        loadScene(pane);
        primaryStage.setTitle("斗兽棋");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadScene(Pane pane) {
        loadBackground(pane);
        loadAnimals(pane);
        loadFunctions(pane);
    }

    private void loadBackground(Pane pane) {
        background = new ImageView("file:pic" + File.separator + "Map2.png");
        background.xProperty().bind(new SimpleDoubleProperty(0));
        background.yProperty().bind(new SimpleDoubleProperty(0));
        background.fitHeightProperty().bind(pane.heightProperty());
        background.fitWidthProperty().bind(pane.widthProperty());
        background.setOnMouseClicked(event -> {
            double xPercent = event.getX() / background.getFitWidth();
            double yPercent = event.getY() / background.getFitHeight();
            int col = (xPercent >= LEFT_MARGIN && xPercent <= (1 - RIGHT_MARGIN))
                    ? (int) ((xPercent - LEFT_MARGIN) / (1 - LEFT_MARGIN - RIGHT_MARGIN) * COLS)
                    : -1;
            int row = (yPercent >= TOP_MARGIN && yPercent <= (1 - BOTTOM_MARGIN))
                    ? (int) ((yPercent - TOP_MARGIN) / (1 - TOP_MARGIN - BOTTOM_MARGIN) * ROWS)
                    : -1;
            System.out.println("Tile clicked, row = " + row + " and col = " + col);
            try {
                Board.getInstance().tileClicked(row, col);
            } catch (GameWinException e) {
                String side = e.getSide() == Board.Side.LEFT ? "左方" : "右方";
                Pane shadowPane = new Pane();
                Label label = new Label(side + "胜利!");
                label.setFont(new Font(50));
                label.setLayoutX(pane.getWidth()/2 - 216);
                label.setLayoutY(100);
                shadowPane.setLayoutX(pane.getWidth() * LEFT_MARGIN);
                shadowPane.setPrefWidth(pane.getWidth() * (1 - LEFT_MARGIN - RIGHT_MARGIN));
                shadowPane.setMinHeight(pane.getHeight());
                shadowPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.6);");
                shadowPane.getChildren().add(label);
                pane.getChildren().add(shadowPane);
            }
            updateAnimals(Board.getInstance().getAnimals());
        });
        pane.getChildren().add(background);
    }


    private void updateAnimals(Animal[] animals) {
        for (Animal animal : animals) {
            updateAnimal(animal);
        }
        scaleAnimation.play(Board.getInstance().getSelectedAnimal());
    }

    private void updateAnimal(Animal animal) {
        ImageView imageView = animal.getView();
        Tile tile = Board.getInstance().getTileByAnimal(animal);
        if (tile == null) {
            imageView.xProperty().bind(background.fitWidthProperty());
            imageView.yProperty().bind(background.fitHeightProperty());
            return;
        }
        DoubleProperty backgroundWidth = background.fitWidthProperty();
        DoubleProperty backgroundHeight = background.fitHeightProperty();
        imageView.xProperty().bind(backgroundWidth.multiply((1 - LEFT_MARGIN - RIGHT_MARGIN) * tile.getCol() / COLS).add(backgroundWidth.multiply(LEFT_MARGIN)));
        imageView.yProperty().bind(backgroundHeight.multiply((1 - TOP_MARGIN - BOTTOM_MARGIN) * tile.getRow() / ROWS).add(backgroundHeight.multiply(TOP_MARGIN)));
        imageView.fitWidthProperty().bind(backgroundWidth.multiply((1 - LEFT_MARGIN - RIGHT_MARGIN) / COLS));
        imageView.fitHeightProperty().bind(backgroundHeight.divide(ROWS));
    }

    private void loadAnimals(Pane pane) {
        for (Board.Side side : Board.Side.values()) {
            String basePath = "file:pic" + File.separator + "animals" + File.separator + side.name().toLowerCase() + File.separator;
            for (int i = 1; i < 9; i++) {
                Animal animal = Board.getInstance().getAnimal(side, i);
                animal.setColoredImage(new Image(basePath + i + ".png"));
                animal.setGreyImage(new Image(basePath + "grey" + File.separator + i + ".png"));
                ImageView imageView = new ImageView();
                animal.setView(imageView);
                imageView.setOnMouseClicked(event -> {
                    Tile tile = Board.getInstance().getTileByAnimal(animal);
                    System.out.println("This is " + animal.toString() + " at " + tile.getRow() + ", " + tile.getCol());
                    try {
                        Board.getInstance().tileClicked(tile.getRow(), tile.getCol());
                    } catch (GameWinException e) {
                        e.printStackTrace();
                    }
                    updateAnimals(Board.getInstance().getAnimals());
                });
                pane.getChildren().add(imageView);
            }
        }
        updateAnimals(Board.getInstance().getAnimals());
        Board.getInstance().refreshAnimalImages();
    }

    private void loadFunctions(Pane pane) {
        Button restart = new Button("重新开始");
        restart.translateXProperty().bind(background.fitWidthProperty().multiply(0.01));
        restart.setTranslateY(20);
        restart.setOnMouseClicked(event -> {
            Board.getInstance().restart();
            pane.getChildren().remove(0, pane.getChildren().size());
            loadBackground(pane);
            loadAnimals(pane);
            loadFunctions(pane);
            updateAnimals(Board.getInstance().getAnimals());
        });
        pane.getChildren().add(restart);

        Button undo = new Button("悔棋");
        undo.translateXProperty().bind(background.fitWidthProperty().multiply(0.01));
        undo.translateYProperty().bind(background.fitHeightProperty().subtract(40));
        undo.setOnMouseClicked(event -> {
            Board.getInstance().undo();
            updateAnimals(Board.getInstance().getAnimals());
        });
        pane.getChildren().add(undo);

        Button help = new Button("帮助");
        help.translateXProperty().bind(background.fitWidthProperty().multiply(0.01));
        help.setTranslateY(60);
        help.setOnMouseClicked(event -> {
            Text text = new Text(
                    "斗兽棋的棋盘\n" +
                    "斗兽棋的棋盘横七列，纵九行，棋子放在格子中。双方底在线各有三个陷阱（作品字排）和一个兽穴(于品字中间)。 棋牌中部有两片水域，称之为小河。\n" +
                    "\n" +
                    "斗兽棋的棋子\n" +
                    "斗兽棋棋子共十六个，分为红蓝双方，双方各有八只一样的棋子（下称为：兽 或 动物），按照战斗力强弱排列为：象>狮>虎>豹>狗>狼>猫>鼠。\n" +
                    "\n" +
                    "斗兽棋的走法\n" +
                    "游戏开始时，红方先走，然后轮流走棋。每次可走动一只兽，每只兽每次走一方格，除己方兽穴和小河以外，前后左右均可。但是，狮、虎、鼠还有不同走法：\n" +
                    "狮虎跳河法：狮虎在小河边时，可以纵横对直跳过小河，且能把小河对岸的敌方较小的兽类吃掉，但是如果对方老鼠在河里，把跳的路线阻隔就不能跳，若对岸是对方比自己战斗力前的兽，也不可以跳过小河；\n" +
                    "鼠游过河法：鼠是唯一可以走入小河的兽，走法同陆地上一样，每次走一格，上下左右均可，而且，陆地上的其他兽不可以吃小河中的鼠，小河中的鼠也不能吃陆地上的象，鼠类互吃不受小河影响。\n" +
                    "\n" +
                    "斗兽棋的吃法\n" +
                    "斗兽棋吃法分普通吃法和特殊此法，普通吃法是按照兽的战斗力强弱，强者可以吃弱者。\n" +
                    "特殊吃法如下：\n" +
                    "1、鼠吃象法：八兽的吃法除按照战斗力强弱次序外，惟鼠能吃象，象不能吃鼠。\n" +
                    "2、互吃法：凡同类相遇，可互相吃。\n" +
                    "3、陷阱：棋盘设陷阱，专为限制敌兽的战斗力（自己的兽，不受限制），敌兽走入陷阱，即失去战斗力，本方的任意兽类都可以吃去陷阱里的兽类。\n" +
                    "综合普通吃法和特殊吃法，将斗兽棋此法总结如下：\n" +
                    "鼠可以吃象、鼠\n" +
                    "猫可以吃猫、鼠；\n" +
                    "狼可以吃狼、猫、鼠；\n" +
                    "狗可以吃狗、狼、猫、鼠；\n" +
                    "豹可以吃豹、狗、狼、猫、鼠；\n" +
                    "虎可以吃虎、豹、狗、狼、猫、鼠；\n" +
                    "狮可以吃狮、虎、豹、狗、狼、猫、鼠；\n" +
                    "象可以吃象、狮、虎、豹、狗、狼、猫；\n" +
                    "\n" +
                    "斗兽棋胜负判定:\n" +
                    "1、任何一方的兽走入敌方的兽穴就算胜利（自己的兽类不可以走入自己的兽穴）；\n" +
                    "2、任何一方的兽被吃光就算失败，对方获胜；\n" +
                    "3、任何一方所有活着的兽被对方困住，均不可移动时，就算失败，对方获胜；\n"
            );
            text.wrappingWidthProperty().bind(pane.widthProperty().subtract(60));
            text.setLayoutY(30);
            text.setLayoutX(30);
            Pane helpPane = new Pane();
            helpPane.getChildren().add(text);
            helpPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
            helpPane.setMinHeight(pane.getHeight());
            helpPane.setMinWidth(pane.getWidth());
            helpPane.setOnMouseClicked(event1 -> pane.getChildren().remove(helpPane));
            pane.getChildren().add(helpPane);
        });
        pane.getChildren().add(help);
    }
}
