package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;

public class Controller {

    @FXML
    private TextField textField;
    @FXML
    private Label maleTextLabel;
    @FXML
    private Label femaleTextLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label dateLabel2;
    @FXML
    private Label timeLabel;
    @FXML
    private Label placeLabel;

    Element femaleElement, maleElement, ageElement;
    Document doc;

    public void initialize() {

        Date date = new Date();
        dateLabel.setText(date.toString());
    }

    public void pressButton() throws IOException {

        try {
            doc = Jsoup.connect(textField.getText()).get();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Wrong or unreachable URL");
            alert.setContentText("URL address is wrong or can not be reached.");
            alert.showAndWait();
        }

        femaleElement = doc.select("div.post-content").select("p").get(0);
        maleElement = doc.select("div.post-content").select("p").get(1);

        // Part of code for (age, date, time, place) data acquiring
        Element codePart = doc.select("div.add_info").get(0);
        String codeToString = codePart.text().replaceAll("\\s", "");
        System.out.println(codeToString);

        String ageString = codeToString.substring(17, 22);
        String dateString = codeToString.substring(27, 37);
        String timeString = codeToString.substring(45, 50);
        String placeString = codeToString.substring(58);

        ageLabel.setText(ageString);
        dateLabel2.setText(dateString);
        timeLabel.setText(timeString);
        placeLabel.setText(placeString);

        System.out.println(ageString);
        System.out.println(dateString);
        System.out.println(timeString);
        System.out.println(placeString);


        String maleStringConversion = maleElement.toString();
        String femaleStringConversion = femaleElement.toString();
        String maleNumber = maleStringConversion.replaceAll("\\D+", "");
        String femaleNumber = femaleStringConversion.replaceAll("\\D+", "");
        maleTextLabel.setText(maleNumber);
        femaleTextLabel.setText(femaleNumber);
    }

    public void exitButton() {

        Platform.exit();
        System.exit(0);

    }


}
