package animation;

import entity.animal.Animal;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Created by lifengshuang on 02/01/2017.
 */
public class ScaleSelectedAnimal {

    private Timeline timeline;
    private Animal animal;
    private double initialScaleX;
    private double initialScaleY;
    private boolean expand;

    public ScaleSelectedAnimal() {
        this.expand = true;
        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (animal == null) {
                return;
            }
            if (expand) {
                animal.getView().setScaleX(animal.getView().getScaleX() * 1.03);
                animal.getView().setScaleY(animal.getView().getScaleY() * 1.03);
                if (animal.getView().getScaleX() / initialScaleX > 1.3) {
                    expand = false;
                }
            } else {
                animal.getView().setScaleX(animal.getView().getScaleX() * 0.97);
                animal.getView().setScaleY(animal.getView().getScaleY() * 0.97);
                if (initialScaleX / animal.getView().getScaleX() > 1.1) {
                    expand = true;
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void play(Animal animal) {
        timeline.pause();
        if (this.animal != null) {
            this.animal.getView().setScaleX(this.initialScaleX);
            this.animal.getView().setScaleY(this.initialScaleY);
        }
        if (animal != null) {
            this.initialScaleX = animal.getView().getScaleX();
            this.initialScaleY = animal.getView().getScaleY();
        }
        timeline.play();
        this.animal = animal;
    }
}
