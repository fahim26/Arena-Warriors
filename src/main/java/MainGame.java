import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;


import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.almasb.fxgl.ui.FXGLUIConfig.getUIFactory;

public class MainGame extends GameApplication {

    //declaration
    private Entity background, player, coin, coin1;
    private boolean isForward = true;
    private boolean isCoinOntained = false;
    private boolean isCoin1Obtained = false;

    //for initializing the game window
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Coin Run");
        settings.setWidth(1200);
        settings.setHeight(700);
        settings.setCloseConfirmation(true);
    }

    //for initializing the game entities
    @Override
    protected void initGame() {
        background = FXGL.entityBuilder()
                .at(0,0)
                .view("background.png")
                .buildAndAttach();

        player = FXGL.entityBuilder()
                .at(0,580-140)
                .view("player2_still.png")
                .buildAndAttach();

        coin = entityBuilder()
                .at(715, 480)
                .viewWithBBox(new Circle(15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        coin1 = entityBuilder()
                .at(1280, 450)
                .viewWithBBox(new Circle(15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    //for initializing the user input
    @Override
    protected void initInput() {
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Attack") {
            @Override
            protected void onAction() {


            }
        }, KeyCode.SHIFT);

        input.addAction(new UserAction("Go right") {
            @Override
            protected void onAction() {
                isForward=true;
                player.setView(FXGL.getAssetLoader().loadTexture("player2.gif")); //changing the player image

                if(player.getX() < 1060){ //right bound for player
                    player.translateX(2);
                }

                if(background.getX() > -550) { //right bound for background
                    background.translateX(-2);
                    coin.translateX(-2);
                    coin1.translateX(-2);
                }
            }

            @Override
            protected void onActionEnd() {
                player.setView(FXGL.getAssetLoader().loadTexture("player2_still.png")); //changing the player image
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Go left") {
            @Override
            protected void onAction() {
                isForward=false;
                player.setView(FXGL.getAssetLoader().loadTexture("player2_flipped.gif")); //changing the player image
                if(player.getX() > 0){ //right bound for player
                    player.translateX(-2);
                }
                if(background.getX() < 0) { //right bound for background
                    background.translateX(2);
                    coin.translateX(2);
                    coin1.translateX(2);
                }
            }

            @Override
            protected void onActionEnd() {
                player.setView(FXGL.getAssetLoader().loadTexture("player2_still_flipped.png")); //changing the player image
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                if(player.getX()<1060 && isForward) {
                    for(int i = 0; i<=200; i+=50){
                        getMasterTimer().runOnceAfter(() -> {
                            player.translate(20, -20); //player is moving up
                        }, Duration.millis(i));
                    }
                    for(int i = 200; i<=400; i+=50){
                        getMasterTimer().runOnceAfter(() -> {
                            player.translate(20, 20); //player is moving down
                        }, Duration.millis(i));
                    }
                }else if(player.getX()>0 && !isForward){
                    for(int i = 0; i<=200; i+=50){
                        getMasterTimer().runOnceAfter(() -> {
                            player.translate(-20, -20); //player is moving up
                        }, Duration.millis(i));
                    }
                    for(int i = 200; i<=400; i+=50){
                        getMasterTimer().runOnceAfter(() -> {
                            player.translate(-20, 20); //player is moving down
                        }, Duration.millis(i));
                    }
                }

                if(!isCoinOntained
                        && coin.getCenter().getX()-player.getCenter().getX() <= 250
                        && coin.getCenter().getX()-player.getCenter().getX() >= -30){
                    FXGL.getGameState().increment("Coins", +3); //coin is obtained
                    isCoinOntained = true;
                    coin.removeFromWorld();
                }

                if(!isCoin1Obtained
                        && coin1.getCenter().getX()-player.getCenter().getX() <= 300
                        && coin1.getCenter().getX()-player.getCenter().getX() >= -50){
                    FXGL.getGameState().increment("Coins", +3); //coin1 is obtained
                    isCoin1Obtained = true;
                    coin1.removeFromWorld();
                }
            }
        }, KeyCode.SPACE);
    }


    //for keep tracking of how many coins have gained
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("Coins", 0);
    }

    //for displaying additional elements like score etc.
    @Override
    protected void initUI() {
        Text textPixels = new Text();
        textPixels.setTranslateX(50);
        textPixels.setTranslateY(100);
        textPixels.setStyle("-fx-font: 45 arial;");

        textPixels.textProperty().bind(FXGL.getGameState().intProperty("Coins").asString());

        FXGL.getGameScene().addUINode(textPixels);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
