package com.example.algorithim_project1;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main extends Application {
    static ArrayList<City> cities;
    static int shortestPath = 0;
    static int shortestPathIndex = 0;
    static String source = "";
    static String destanition = "";

    Group mainRoot;
    HBox ButtonsHBox;
    Scene mainScene;

    Button relation;
    Button bestButton;
    Button tableButton;
    Button chooseButton;
    Button alternativePathsButton;
    static int[][] mainMatrix;
    static int[][] path;
    Stage mainStage;
    static List<List<City>> stages;
    static ArrayList<ArrayList<Integer>> altPaths;
    int WIDTH_OF_MAP = 1000, HEIGHT_OF_MAP = 600;
    private static final double CIRCLE_RADIUS = 30.0;
    private static final double CIRCLE_MARGIN = 100;
    private static final double LINE_WIDTH = 2.0;
    private static final double FONT = 14.0;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.mainStage = stage;
        try {
            showMainStage();
            stages = new ArrayList<>();
            altPaths = new ArrayList<>();
        } catch (Exception e) {
            start(stage);
            return;
        }


    }


    private static void readTheDataFromTheFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        Integer.parseInt(scanner.nextLine().trim());
        cities = new ArrayList<>();
        String nextLine = scanner.nextLine().trim();
        source = nextLine.split(",")[0].trim();
        destanition = nextLine.split(",")[1].trim();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String cityname = line.substring(0, line.indexOf(",")).trim();
            System.out.println(cityname);
            City flag = getCityWithName(cityname);

            if (flag == null) {
                flag = new City(cityname);
                cities.add(flag);
            }
            String rest = line.substring(line.indexOf(",") + 1);


            String[] adjs = rest.split("],");
            for (int i = 0; i < adjs.length; i++) {
                String adj = adjs[i];
                adj = adj.replaceAll("\\[", "");
                adj = adj.replaceAll("]", "");


                String[] attr = adj.split(",");

                String cityDest = attr[0].trim();
                City found = getCityWithName(cityDest);
                if (found == null) {
                    found = new City(cityDest);
                    cities.add(found);
                }
                found.hotelCost = Integer.parseInt(attr[2].trim());
                flag.adjacentList.add(new Adjacent(found, Integer.parseInt(attr[1].trim()), Integer.parseInt(attr[2].trim())));

            }
        }
        fillMatrix();
        findTheBestPathCost();
    }

    private static ArrayList<Integer> getOptimalPath(int index) {
        int i = index;
        ArrayList<Integer> citiesPaths = new ArrayList<>();
        int indexDest = getCityIndexFromCities(getCityWithName(destanition));
        System.out.println("-----------" + i);
        System.out.println("------------" + indexDest);

        citiesPaths.add(indexDest);
        while (path[i][indexDest] != -1) {
            citiesPaths.add(i);
            int tempIndexTep = indexDest;
            indexDest = i;
            i = path[i][tempIndexTep];
        }
        citiesPaths.add(getCityIndexFromCities(getCityWithName(source)));
        Collections.reverse(citiesPaths);
        return citiesPaths;
    }


    private static void getAllPaths() {
        altPaths.clear();
        try {
            int index = getCityIndexFromCities(getCityWithName(destanition));
            for (int i = 0; i < path.length; i++) {
                if (path[i][index] == Integer.MAX_VALUE)
                    continue;
                ArrayList<Integer> path = new ArrayList<>();
                path.add(index);
                getAllPaths(path, index, i);

            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    private static void getAllPaths(ArrayList<Integer> path, int index1, int index2) {
        if (Main.path[index2][index1] == -1) {
            path.add(index2);
            Collections.reverse(path);
            altPaths.add(path);
            return;
        }
        path.add(index2);
        for (int i = 0; i < Main.path.length; i++) {
            if (Main.path[i][index2] == Integer.MAX_VALUE)
                continue;
            getAllPaths(new ArrayList<>(path), index2, i);
        }
    }

    public static void getStages(String start, String end) {
        Set<City> visited = new HashSet<>();
        Queue<City> queue = new LinkedList<>();
        City startCity = getCityWithName(start);

        if (startCity != null) {
            queue.add(startCity);
            visited.add(startCity);
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<City> stage = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                City current = queue.poll();
                stage.add(current);

                for (Adjacent adj : current.adjacentList) {
                    City neighbor = adj.city;
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            stages.add(stage);

            if (stage.contains(getCityWithName(end))) {
                break;
            }
        }
    }

    public static int[][] fillMatrix() {
        int[][] matrix2 = new int[cities.size()][cities.size()];
        path = new int[cities.size()][cities.size()];
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 0; j < matrix2.length; j++) {
                matrix2[i][j] = Integer.MAX_VALUE;
                path[i][j] = Integer.MAX_VALUE;

            }
        }

        for (int i = 0; i < matrix2.length; i++) {
            matrix2[i][i] = 0;
        }
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            int min = Integer.MAX_VALUE;
            int index = -1;
            for (int k = 0; k < cities.size(); k++) {
                if (k == i)
                    continue;
                if (matrix2[k][i] < min) {
                    min = matrix2[k][i];
                    index = k;
                }
            }
            if (min == Integer.MAX_VALUE)
                min = 0;
            for (int j = 0; j < city.adjacentList.size(); j++) {
                Adjacent adjc = city.adjacentList.get(j);
                int IndexOfCity = getCityIndexFromCities(adjc.city);
                matrix2[i][IndexOfCity] = min + adjc.hotel + adjc.petrol;
                path[i][IndexOfCity] = index;
            }
        }
        mainMatrix = matrix2;
        return matrix2;
    }

    public static City getCityWithName(String cityName) {
        for (City city : cities) {
            if (city.name.equals(cityName))
                return city;
        }
        return null;
    }

    public static void findTheBestPathCost() {
        shortestPath = Integer.MAX_VALUE;
        int index = getCityIndexFromCities(getCityWithName(destanition));
        for (int i = 0; i < mainMatrix.length; i++) {
            if (i == index)
                continue;
            if (shortestPath > mainMatrix[i][index]) {
                shortestPath = mainMatrix[i][index];
                shortestPathIndex = i;
            }
        }
    }

    private static int getCityIndexFromCities(City city) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i) == city)
                return i;
        }
        return -1;
    }

    public void showMainStage() throws FileNotFoundException {
        mainRoot = new Group();
        ButtonsHBox = new HBox();
        ButtonsHBox.setSpacing(10);
        ButtonsHBox.setTranslateX(30);
        ButtonsHBox.setTranslateY(40);
        mainScene = new Scene(mainRoot, 1000, 700, Color.BLACK);
        relation = new Button("relation");
        bestButton = new Button("optimal PATH");
        tableButton = new Button("TABLE");
        alternativePathsButton = new Button("ALTERNATIVE");
        chooseButton = new Button("CHOOSE FILE");
        chooseButton.setFont(new Font("Arial", 20));
        chooseButton.setPrefWidth(170);
        tableButton.setFont(new Font("Arial", 20));
        tableButton.setPrefWidth(170);
        relation.setFont(new Font("Arial", 20));
        relation.setPrefWidth(170);
        bestButton.setFont(new Font("Arial", 20));
        bestButton.setPrefWidth(170);
        alternativePathsButton.setFont(new Font("Arial", 20));
        alternativePathsButton.setPrefWidth(170);
        ButtonsHBox.getChildren().addAll(relation, bestButton, alternativePathsButton, tableButton, chooseButton);
        TextArea textArea = new TextArea();
        textArea.setLayoutX(100);
        textArea.setLayoutY(100);
        textArea.setPrefSize(800, 550);
        textArea.setStyle("-fx-control-inner-background: #000000;");
        mainRoot.getChildren().addAll(ButtonsHBox, textArea);
        mainStage.setScene(mainScene);
        mainStage.setTitle("Algorithim Project1");


        tableButton.setOnAction((ActionEvent e) -> {
            Group tableStageGroup = new Group();
            Scene tableStageScene = new Scene(tableStageGroup, 1100, 600, Color.BLACK);
            Stage tableStage = new Stage();
            for (int i = 0; i < cities.size(); i++) {
                Label xCity = new Label(String.valueOf(cities.get(i).getName()));
                xCity.setTextFill(Color.WHITE);
                xCity.setTranslateY(2);
                xCity.setTranslateX(60 + (i * 70));
                xCity.setFont(new Font("Arial", 20));

                Label yCity = new Label("" + cities.get(i).getName());
                yCity.setFont(new Font("Arial", 20));
                yCity.setTextFill(Color.WHITE);
                yCity.setTranslateY(30 + (i * 40));
                yCity.setTranslateX(2);
                tableStageGroup.getChildren().addAll(xCity, yCity);
            }
            for (int i = 0; i < mainMatrix.length; i++) {
                for (int j = 0; j < mainMatrix.length; j++) {
                    Label dpTableElement = new Label(path[j][i] == Integer.MAX_VALUE ? "0" : String.valueOf(path[j][i]));
                    dpTableElement.setTranslateX(65 + (i * 70));
                    dpTableElement.setTranslateY(30 + (j * 40));
                    dpTableElement.setFont(new Font("Arial", 18));
                    dpTableElement.setTextFill(Color.RED);
                    tableStageGroup.getChildren().add(dpTableElement);
                }
            }

            tableStage.setScene(tableStageScene);
            tableStage.show();
        });
        chooseButton.setOnAction((ActionEvent again) -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(mainStage);
            if (file != null) {
                try {
                    readTheDataFromTheFile(file);
                } catch (Exception e) {
                    showWarning("An error happened");
                }
            }

        });
        relation.setOnAction(e -> {
            showRelationStage();
        });
        bestButton.setOnAction(e -> {
            Stage primaryStage = new Stage();
            Pane root2 = new Pane();
            root2.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
            root2.setPrefSize(WIDTH_OF_MAP, HEIGHT_OF_MAP);
            Label cost = new Label("Cost : " + shortestPath);
            cost.setTextFill(Color.YELLOW);
            cost.setFont(new Font("Times-Roman", 40));
            cost.setTranslateX(470);
            cost.setTranslateY(50);
            root2.getChildren().add(cost);
            if (stages.isEmpty())
                getStages(source, destanition);
            drawPath(root2, getOptimalPath(shortestPathIndex), 0, true);
            Scene scene = new Scene(root2, WIDTH_OF_MAP, HEIGHT_OF_MAP);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
        alternativePathsButton.setOnAction(e -> {

                    String altPathsString = "";
                    getAllPaths();
                    for (int i = 0; i < altPaths.size(); i++) {
                        String path = (i+1)+" )  ";
                        int cost = 0 ;
                        for (int j = 0; j < altPaths.get(i).size() - 1; j++) {
                            path += cities.get(altPaths.get(i).get(j)).name + " -> ";
                            cost += cities.get(altPaths.get(i).get(j)).hotelCost;
                            for(Adjacent adjacent : cities.get(altPaths.get(i).get(j)).adjacentList){
                                if(adjacent.city.name.equals(cities.get(altPaths.get(i).get(j+1)).name))
                                    cost += adjacent.petrol;
                            }
                        }
                        if (altPaths.size() - 1 >= altPaths.get(i).size())
                            path += cities.get(altPaths.get(i).get(altPaths.get(i).size() - 1)).name;
                        altPathsString += path +"   //// Cost is "+cost+"\n";
                    }
                    textArea.setText(altPathsString);
                }
        );
        mainStage.show();
    }


    public void showRelationStage() {
        Stage primaryStage = new Stage();
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        root.setPrefSize(WIDTH_OF_MAP, HEIGHT_OF_MAP);
        TextArea textArea = new TextArea();
        textArea.setLayoutX(0);
        textArea.setLayoutY(0);
        textArea.setPrefSize(WIDTH_OF_MAP, HEIGHT_OF_MAP);
        textArea.setStyle("-fx-control-inner-background: #000000;");

        textArea.setText("mainMatrix[i][j]=infinty and mainMatrix[i][i]=0" +
               "\n"+"if i=j pass "+
                "\n"+ "min of mainMatrix[j][i] for all j "+"this will find the optimal solution of i and set the value of it to min" +
                "\n"+  "mainMatrix[i][adjacentIndex] = min +adjacentCity.hotel + adjacentCity.petrol" +
                "");
        textArea.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        root.getChildren().add(textArea);
        Scene scene = new Scene(root, WIDTH_OF_MAP, HEIGHT_OF_MAP);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void drawPath(Pane root, ArrayList<Integer> path, int selectedIndex, boolean isOptimal) {
        for (int i = 0; i < Main.path.length; i++) {
            for (int j = 0; j < Main.path[i].length; j++) {
                System.out.print("     " + Main.path[i][j]);
            }
            System.out.println();
        }
        root.getChildren().clear();
        double startX = CIRCLE_MARGIN;
        double startY = HEIGHT_OF_MAP / 2.0;
        double stageSpacing = (WIDTH_OF_MAP - 2.0 * CIRCLE_MARGIN) / stages.size();
        HashMap<String, CircleText> cityCircleMap = new HashMap<>();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        root.setPrefSize(WIDTH_OF_MAP, HEIGHT_OF_MAP);
        Label pathLabel = new Label("Path :");
        pathLabel.setTranslateX(430);
        pathLabel.setTranslateY(25);
        ComboBox<Integer> pathsCombobox = new ComboBox();
        ArrayList<Integer> pathsNumbers = new ArrayList<>();
        for (int i = 0; i < altPaths.size(); i++) {
            pathsNumbers.add(i + 1);
        }
        pathsCombobox.getItems().addAll(pathsNumbers);
        pathsCombobox.setTranslateX(470);
        pathsCombobox.setTranslateY(20);
        pathsCombobox.setPromptText(selectedIndex + "");

        if (!isOptimal)
            root.getChildren().addAll(pathsCombobox, pathLabel);

        for (int i = 0; i < stages.size(); i++) {
            List<City> stage = stages.get(i);
            double x = startX + i * stageSpacing;
            for (int j = 0; j < stage.size(); j++) {
                City city = stage.get(j);
                double y = startY + (j - (stage.size() - 1) / 2.0) * CIRCLE_MARGIN;
                CircleText circle = createCircle(x, y, city.getName(), city.hotelCost);
                root.getChildren().addAll(circle);
                cityCircleMap.put(city.getName(), circle);
            }
        }
        //Draw the Lines
        int cost = 0;
        for (int i = 0; i < stages.size() - 1; i++) {
            List<City> stage = stages.get(i);
            double x = startX + i * stageSpacing;
            City city1 = cities.get(path.get(i));
            City city2 = cities.get(path.get(i + 1));
            int j = 0;
            for (City city : stage) {
                if (stage.get(j).equals(city1))
                    break;
                j++;
            }
            int petrolCost = 0;
            for (Adjacent adjacent : city1.adjacentList) {
                if (adjacent.city.name.equals(city2.name))
                    petrolCost = adjacent.petrol;
            }
            cost += city1.hotelCost;
            cost += petrolCost;

            double y = startY + (j - (stage.size() - 1) / 2.0) * CIRCLE_MARGIN;
            CircleText adjacentCircle = cityCircleMap.get(city2.name);

            Line line = makeLine(x, y, adjacentCircle.getCircle().getCenterX(), adjacentCircle.getCircle().getCenterY());

            double adjacentCircleX = adjacentCircle.getCircle().getCenterX();
            double adjacentCircleY = adjacentCircle.getCircle().getCenterY();
            double quarterX = (3 * x + adjacentCircleX) / 4.0;
            double quarterY = (3 * y + adjacentCircleY) / 4.0;

            Text costText = makeAText(Integer.toString(petrolCost));
            costText.setFill(Color.BLACK);
            costText.setFont(Font.font("Arial", FontWeight.BOLD, FONT));
            costText.setTranslateX(quarterX);
            costText.setTranslateY(quarterY + costText.getBoundsInLocal().getHeight() / 2);

            root.getChildren().addAll(line, costText);

        }
        Label costLabel = new Label("Cost : " + cost);

        costLabel.setTranslateX(480);
        costLabel.setTranslateY(50);
        costLabel.setTextFill(Color.YELLOW);
        costLabel.setFont(new Font("Times-Roman", 40));
        root.getChildren().add(costLabel);
        //Draw the Labels inside the circles
        for (City city : cities) {
            CircleText circle = cityCircleMap.get(city.name);
            if (!root.getChildren().contains(circle.getText()))
                root.getChildren().addAll(circle.getText());
        }
        pathsCombobox.setOnAction(event -> {
            Integer selectedItem = pathsCombobox.getSelectionModel().getSelectedItem();
            drawPath(root, altPaths.get(selectedItem - 1), selectedItem, false);
        });
    }

    private CircleText createCircle(double x, double y, String label, int hotelCost) {
        Circle circle = new Circle(x, y, (CIRCLE_RADIUS + 10));
        circle.setStroke(Color.GRAY);
        circle.setStrokeWidth(LINE_WIDTH);
        circle.setFill(Color.GRAY);

        Text labelText = makeAText(label);
        labelText.setFill(Color.CYAN);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, FONT));

        Text costText = makeAText(Integer.toString(hotelCost));
        costText.setFill(Color.BLUE);
        costText.setFont(Font.font("Arial", FontWeight.BOLD, FONT));
        VBox labelContainer;
        if (hotelCost == 0)
            labelContainer = new VBox(labelText);
        else
            labelContainer = new VBox(costText, labelText);

        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setTranslateX(x - CIRCLE_RADIUS / 2);
        labelContainer.setTranslateY(y - CIRCLE_RADIUS / 2);

        CircleText circleWithText = new CircleText(x, y, CIRCLE_RADIUS, labelContainer);
        circleWithText.setCenterX(x);
        circleWithText.setCenterY(y);
        return circleWithText;
    }

    private Line makeLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(LINE_WIDTH);
        return line;
    }

    private Text makeAText(String label) {
        Text text = new Text(label);
        text.setFont(Font.font("Arial", FontWeight.BOLD, FONT));
        return text;
    }

    public static void showWarning(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setContentText(warningMessage);
        alert.show();
    }
}
