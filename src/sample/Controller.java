package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Controller {

    private static final String HOVERED_BUTTON_STYLE = "-fx-background-radius: 30; -fx-background-color: linear-gradient(#e03e21, #ad2108); -fx-background-insets: 0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-insets: 1";
    private final String IDLE_BUTTON_STYLE = "-fx-background-radius: 30; -fx-background-color: linear-gradient(#ff5400, #be1d00); -fx-background-insets: 0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-insets: 1";

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
    @FXML
    private CheckBox loopCheckBox;
    @FXML
    private TextField timeInterval;
    @FXML
    private CheckBox emailCheckbox;
    @FXML
    private Button checkBtn;
    @FXML
    private Button exitBtn;


    private Element femaleElement, maleElement;
    private Document doc;
    private ArrayList<String> arrayList;

    public void initialize() {

        Date date = new Date();
        dateLabel.setText(date.toString());

        timeInterval.visibleProperty().bind(loopCheckBox.selectedProperty());
        checkBtn.setOnMouseEntered(event -> checkBtn.setStyle(HOVERED_BUTTON_STYLE));
        checkBtn.setOnMouseExited(event -> checkBtn.setStyle(IDLE_BUTTON_STYLE));
        exitBtn.setOnMouseEntered(event -> exitBtn.setStyle(HOVERED_BUTTON_STYLE));
        exitBtn.setOnMouseExited(event -> exitBtn.setStyle(IDLE_BUTTON_STYLE));


    }

    public void pressButton() throws InterruptedException {

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

        String maleStringConversion = maleElement.toString();
        String femaleStringConversion = femaleElement.toString();
        String maleTicketsNumber = maleStringConversion.replaceAll("\\D+", "");
        String femaleTicketsNumber = femaleStringConversion.replaceAll("\\D+", "");
        maleTextLabel.setText(maleTicketsNumber);
        femaleTextLabel.setText(femaleTicketsNumber);

        // Part of code for (age, date, time, place) data acquiring
        Element codePart = doc.select("div.add_info").get(0);
        String codeToString = codePart.text().replaceAll("\\s", "");
//        System.out.println(codeToString);

        String ageString = codeToString.substring(17, 22);
        String dateString = codeToString.substring(27, 37);
        String timeString = codeToString.substring(45, 50);
        String placeString = codeToString.substring(58);

        ageLabel.setText(ageString);
        dateLabel2.setText(dateString);
        timeLabel.setText(timeString);
        placeLabel.setText(placeString);

        String subject = "Speed Dates Free Tickets: " + maleTextLabel.getText();
        String body = "Free tickets " + maleTextLabel.getText() +  " available: http://speed-dates.pl/randka/speed-dating-grupa-wiekowa-22-29/";

        if (emailCheckbox.isSelected() && loopCheckBox.isSelected() && !maleTextLabel.getText().equals("0")) {
//            sendMail(readDataFromFile().get(0), readDataFromFile().get(1), readDataFromFile().get(2), subject, body);
            System.out.println("Notification mail has been sent. Looping....");
            TimeUnit.SECONDS.sleep(Long.parseLong(timeInterval.getText()));
        } else if (emailCheckbox.isSelected() && !maleTextLabel.getText().equals("0")) {
            sendMail(readDataFromFile().get(0), readDataFromFile().get(1), readDataFromFile().get(2), subject, body);
            System.out.println("Notification mail has been sent.");
        } else if (loopCheckBox.isSelected() && (!maleTextLabel.getText().equals("0"))) {
            System.out.println("Looping");
            TimeUnit.SECONDS.sleep(Long.parseLong(timeInterval.getText()));
            System.out.println("Looping");
        }
//        for (int i=0; i<2; i++) {
//            System.out.println(i);
//            TimeUnit.SECONDS.sleep(Long.parseLong(timeInterval.getText()));
////                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
////                    break;
////                }
//        }
    }

    public void sendMail(String from, String password, String receiver, String subject, String body) {

        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress toAddress = new InternetAddress(receiver);

            toAddress = new InternetAddress(receiver);


            message.addRecipient(Message.RecipientType.TO, toAddress);


            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();


        } catch (MessagingException me) {
            me.printStackTrace();
        }

    }

    public ArrayList<String> readDataFromFile() {

        try {
            Scanner textFile = new Scanner(new File("data.txt"));
            arrayList = new ArrayList<>();

            while (textFile.hasNext()) {
                arrayList.add(textFile.nextLine());
            }
            return arrayList;
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("File not found");
            alert.setContentText("Data file has not been found.");
            alert.showAndWait();
            System.out.println("Error reading file");
        }
        return arrayList;
    }


    public void exitButton() {


        Platform.exit();
        System.exit(0);

    }


}
