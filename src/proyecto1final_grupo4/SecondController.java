package proyecto1final_grupo4;

import Clases.Directory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.imageio.ImageIO;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author GRUPO4
 */
public class SecondController implements Initializable {

    @FXML
    private AnchorPane barra;
    @FXML
    private TextField textfield;
    @FXML
    private Button visualize;
    @FXML
    private AnchorPane center;
    @FXML
    private Button save;
    
    LinkedList<Directory> treeMap;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void quitButton(ActionEvent event) {
        Stage stage = (Stage) save.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void getVector(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void moveWindow(MouseEvent event) {
        Stage stage = (Stage) barra.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    public void directoryButtonAction(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("src"));

        File selectedDir = dc.showDialog(null);

        if (selectedDir == null) {
            System.out.println("Not directory selected");
        } else {
            double size = 0;
            Directory dir = new Directory(selectedDir.getName());
            dir.setSize(redondeo(recorrerDirectorio(selectedDir.listFiles(), size, dir), 2));
            textfield.setText(selectedDir.getAbsolutePath());
            visualize.setDisable(false);
            treeMap = new LinkedList<>();
            treeMap.add(dir);
            System.out.println("---------- TreeMap ---------");
            iterar(treeMap, 0);
        }
        center.getChildren().clear();
        save.setDisable(true);
    }

    @FXML
    private void saveButtonAction(ActionEvent event) {
        boolean result = false;
        try {
            String nombre = "captura";
            String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            WritableImage snapshot = center.snapshot(null, null);

            BufferedImage bufferedImage;
            bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            File file1 = new File(nombre + fecha + ".png");
            result = ImageIO.write(bufferedImage, "png", file1);
        } catch (IOException ex) {
            Logger.getLogger(SecondController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (result) {
            Alert dialog = new Alert(AlertType.INFORMATION);
            dialog.setTitle("Confirmación");
            dialog.setHeaderText(null);
            dialog.setContentText("Captura guardada con éxito!");
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.showAndWait();

        } else {

        }
    }
    
    public double redondeo(double tam, int decimales) {
        return new BigDecimal(tam)
                .setScale(decimales, RoundingMode.HALF_EVEN).doubleValue();
    }

    public String identar(int num) {
        char[] carac = new char[num];
        String iden = "";
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                carac[i] = '-';
            }
            iden = new String(carac);
        } else {
            return iden;
        }

        return iden;
    }

    public void iterar(LinkedList<Directory> treeMap, int num) {
        Iterator it = treeMap.iterator();
        while (it.hasNext()) {
            Directory next = (Directory) it.next();

            if (next.getDirectorios().size() > 0) {
                System.out.println(identar(num) + "Carpeta: " + next.getName() + "| size: " + next.getSize());
                iterar(next.getDirectorios(), num + 2);
            } else {
                System.out.println(identar(num) + "Archivo: " + next.getName() + "| size: " + next.getSize());
            }
        }
    }
    
    public boolean isFile(File file) {
        return file.isFile();
    }

    public double recorrerDirectorio(File[] content, double total, Directory dirt) {
        for (File file : content) {
            if (isFile(file)) {
                total += redondeo(file.length() / 1024.0, 2);
                Directory direct = new Directory(file.getName(), redondeo(file.length() / 1024.0, 2));
                dirt.getDirectorios().add(direct);
            } else {
                double tam = 0.0;
                Directory dir = new Directory(file.getName());
                double size = redondeo(recorrerDirectorio(file.listFiles(), tam, dir), 2);
                dir.setSize(size);
                dirt.getDirectorios().add(dir);
                total += size;
            }
        }
        return total;
    }

    public void setLabelSize(Label lb, double amount) {
        lb.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        DecimalFormat two = new DecimalFormat("0.00");
        if (amount < 1024) {
            lb.setText("(" + amount + " KB" + ")");
        } else if (amount > 1024 && amount < 1024 * 1024) {
            lb.setText("(" + two.format(amount / 1024) + " MB" + ")");
        } else if (amount > 1024 * 1024 && amount < 1024 * 1024) {
            lb.setText("(" + two.format(amount / 1024 * 1024) + " GB" + ")");
        } else {
            lb.setText("(" + two.format(amount / 1024 * 1024 * 1024) + " TB" + ")");
        }
    }

    public Color getRandomColor() {
        Random rd = new Random();
        float r = rd.nextFloat();
        float g = rd.nextFloat();
        float b = rd.nextFloat();
        Color randomColor = new Color(r, g, b, 1);
        return randomColor;
    }

    public double getSize(File dir) {
        double size = 0.0;
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                size += f.length();
            } else {
                File[] fileB = f.listFiles();
                for (File fl : fileB) {
                    size += fl.length();
                }
            }
        }
        return size;
    }

    @FXML
    public void visualizeButtonAction(ActionEvent event) throws IOException {
        VBox container = new VBox();
        Pane SizeTotal = new Pane();

        HBox graphics = new HBox();
        graphics.setMaxWidth(960);
        graphics.setMaxHeight(650);

        Rectangle graphicSizeTotal = new Rectangle();
        graphicSizeTotal.setWidth(960);
        graphicSizeTotal.setHeight(25);
        graphicSizeTotal.setFill(Color.CORAL);
        graphicSizeTotal.setStroke(Color.WHITE);

        Label extensionSize = new Label();
        setLabelSize(extensionSize, treeMap.getFirst().getSize());

        SizeTotal.getChildren().addAll(graphicSizeTotal, extensionSize);
        container.getChildren().addAll(SizeTotal, graphics);
        Painting(treeMap.getFirst(), graphics, 960.0, 650.0, "h");
        center.getChildren().addAll(container);
        save.setDisable(false);
    }

    public void Painting(Directory directory, Pane pane, double width, double height, String type) {
        LinkedList<Directory> selected = directory.getDirectorios();
        double size = directory.getSize();
        selected.forEach((f) -> {
            if (!f.isDirectory() && type.equals("h")) {
                double fact1 = width * (f.getSize() / size);
                double fact2 = height;
                Rectangle shape = new Rectangle(fact1, fact2);
                shape.setFill(getRandomColor());
                shape.setStrokeType(StrokeType.INSIDE);
                shape.setStroke(Color.WHITE);
                VBox temp = new VBox();
                temp.getChildren().addAll(shape);
                pane.getChildren().add(temp);
            } else if (!f.isDirectory() && type.equals("v")) {
                double fact1 = width;
                double fact2 = height * (f.getSize() / size);
                Rectangle shape = new Rectangle(fact1, fact2);
                shape.setFill(getRandomColor());
                shape.setStrokeType(StrokeType.INSIDE);
                shape.setStroke(Color.WHITE);
                HBox temp = new HBox();
                temp.getChildren().addAll(shape);
                pane.getChildren().add(temp);
            } else if (f.isDirectory() && type.equals("h")) {
                double size2 = f.getSize();
                VBox box = new VBox();
                box.setMaxWidth(width * (size2 / size));
                box.setMaxHeight(height);
                Painting(f, box, box.getMaxWidth(), box.getMaxHeight(), "v");
                pane.getChildren().add(box);
            } else if (f.isDirectory() && type.equals("v")) {
                double size2 = f.getSize();
                HBox box = new HBox();
                box.setMaxWidth(width);
                box.setMaxHeight(height * (size2 / size));
                Painting(f, box, box.getMaxWidth(), box.getMaxHeight(), "h");
                pane.getChildren().add(box);
            }
        });
    }
}
