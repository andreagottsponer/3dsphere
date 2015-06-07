package sample;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Main extends Application {

    private final Group root = new Group();
    private PerspectiveCamera camera;
    private final double sceneWidth = 800;
    private final double sceneHeight = 600;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);

    private int action;
    private double mouseScaleOldX;
    private double mouseColorOldX;

    private volatile boolean isPicking=false;
    private Point3D vecIni, vecPos;
    private double distance;
    private Sphere s;

    @Override
    public void start(Stage stage) {
        /*
        Box floor = new Box(1500, 10, 1500);
        floor.setMaterial(new PhongMaterial(Color.GRAY));
        floor.setTranslateY(150);
        root.getChildren().add(floor);
        */

        //Elements
        Sphere sphere = new Sphere(50);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        sphere.setTranslateX(0);
        sphere.setTranslateY(0);
        sphere.setTranslateZ(0);
        root.getChildren().add(sphere);

        Sphere sphere2 = new Sphere(50);
        sphere2.setMaterial(new PhongMaterial(Color.BLUE));
        sphere2.setTranslateX(-150);
        sphere2.setTranslateY(-150);
        sphere2.setTranslateZ(150);
        root.getChildren().add(sphere2);

        Sphere sphere3 = new Sphere(50);
        sphere3.setMaterial(new PhongMaterial(Color.YELLOW));
        sphere3.setTranslateX(150);
        sphere3.setTranslateY(150);
        sphere3.setTranslateZ(150);
        root.getChildren().add(sphere3);

        Sphere sphere4 = new Sphere(50);
        sphere4.setMaterial(new PhongMaterial(Color.GREEN));
        sphere4.setTranslateX(-300);
        sphere4.setTranslateY(-300);
        sphere4.setTranslateZ(300);
        root.getChildren().add(sphere4);

        Sphere sphere5 = new Sphere(50);
        sphere5.setMaterial(new PhongMaterial(Color.PINK));
        sphere5.setTranslateX(300);
        sphere5.setTranslateY(300);
        sphere5.setTranslateZ(300);
        root.getChildren().add(sphere5);

        Text text = new Text();
        text.setText("use 's' for scale the objects and 'c' to change the color");
        text.setStyle("-fx-font-size: 60;");
        text.setCache(true);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-border-color: black;-fx-background-color: #66CCFF;");
        borderPane.setTranslateY(300);
        borderPane.setTranslateX(-800);
        borderPane.setTop(text);

        this.root.getChildren().add(borderPane);

        camera = new PerspectiveCamera(true);
        camera.setVerticalFieldOfView(false);

        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.getTransforms().addAll (rotateX, rotateY, new Translate(0, 0, -3000));

        Scene scene = new Scene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("3d3d3d"));

        PointLight light = new PointLight(Color.GAINSBORO);
        root.getChildren().add(light);
        root.getChildren().add(new AmbientLight(Color.WHITE));
        scene.setCamera(camera);

        scene.setOnKeyPressed(event -> {
            KeyCode keycode = event.getCode();
            if(keycode == KeyCode.C) {
                action = 1;
            }
            else if(keycode == KeyCode.S) {
                action = 2;
            }
            else {
                action = 0;
            }
            System.out.println(action);
        });

        //events
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            PickResult pr = me.getPickResult();
            if (pr != null && pr.getIntersectedNode() != null && pr.getIntersectedNode() instanceof Sphere) {
                if(action == 1) {
                    mouseColorOldX = mousePosX;
                    s = (Sphere) pr.getIntersectedNode();
                }
                else if(action == 2) {
                    mouseScaleOldX = mousePosX;
                    s = (Sphere) pr.getIntersectedNode();
                }
                else {
                    distance = pr.getIntersectedDistance();
                    s = (Sphere) pr.getIntersectedNode();
                    isPicking = true;
                    vecIni = unProjectDirection(mousePosX, mousePosY, scene.getWidth(), scene.getHeight());
                }
            }
        });
        scene.setOnMouseDragged((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            PickResult pr = me.getPickResult();

            if(action == 1) {
                if(Math.abs(mousePosX - mouseColorOldX) > 0) {
                    double color= Math.abs(mousePosX- mouseColorOldX) % 6;
                    switch ((int) color) {
                        case 0:
                            s.setMaterial(new PhongMaterial(Color.RED));
                            break;
                        case 1:
                            s.setMaterial(new PhongMaterial(Color.BLUE));
                            break;
                        case 2:
                            s.setMaterial(new PhongMaterial(Color.YELLOW));
                            break;
                        case 3:
                            s.setMaterial(new PhongMaterial(Color.GREEN));
                            break;
                        case 4:
                            s.setMaterial(new PhongMaterial(Color.PINK));
                            break;
                        case 5:
                            s.setMaterial(new PhongMaterial(Color.VIOLET));
                            break;
                    }
                }
            }
            else if(action == 2) {
                if(Math.abs(mousePosX - mouseScaleOldX) > 0) {
                    try {
                        s.setScaleX(Math.abs(mousePosX - mouseScaleOldX));
                        s.setScaleY(Math.abs(mousePosX - mouseScaleOldX));
                        s.setScaleZ(Math.abs(mousePosX - mouseScaleOldX));
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            else {
                if (isPicking) {
                    vecPos = unProjectDirection(mousePosX, mousePosY, scene.getWidth(), scene.getHeight());
                    Point3D p = vecPos.subtract(vecIni).multiply(distance);
                    s.getTransforms().add(new Translate(p.getX(), p.getY(), p.getZ()));
                    vecIni = vecPos;
                    distance = pr.getIntersectedDistance();
                } else {
                    rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
                    rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
                    mouseOldX = mousePosX;
                    mouseOldY = mousePosY;
                }
            }
        });
        scene.setOnMouseReleased((MouseEvent me)->{
            if(action == 1) {
                action = 0;
            }
            else if(action == 2) {
                action = 0;
            }
            else {
                if (isPicking) {
                    isPicking = false;
                }
            }
        });

        stage.setTitle("3D Dragging");
        stage.setScene(scene);
        stage.show();
    }

    /*
     From fx83dfeatures.Camera3D
     http://hg.openjdk.java.net/openjfx/8u-dev/rt/file/5d371a34ddf1/apps/toys/FX8-3DFeatures/src/fx83dfeatures/Camera3D.java
    */
    public Point3D unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight) {
        double tanHFov = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f);
        Point3D vMouse = new Point3D(tanHFov*(2*sceneX/sWidth-1), tanHFov*(2*sceneY/sWidth-sHeight/sWidth), 1);

        Point3D result = localToSceneDirection(vMouse);
        return result.normalize();
    }

    public Point3D localToScene(Point3D pt) {
        Point3D res = camera.localToParentTransformProperty().get().transform(pt);
        if (camera.getParent() != null) {
            res = camera.getParent().localToSceneTransformProperty().get().transform(res);
        }
        return res;
    }

    public Point3D localToSceneDirection(Point3D dir) {
        Point3D res = localToScene(dir);
        return res.subtract(localToScene(new Point3D(0, 0, 0)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
