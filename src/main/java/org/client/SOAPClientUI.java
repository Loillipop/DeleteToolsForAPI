package org.client;




import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SOAPClientUI extends Application {

    // Инициализация логгера
    private static final Logger logger = Logger.getLogger(SOAPClientUI.class);

    // Поля для ввода адреса SOAP-сервиса и пути к файлу с ID
    private TextField soapAddressField;
    private TextField filePathField;

    // Поле для вывода ответа
    private TextArea responseArea;

    public static void main(String[] args) {
        // Запуск JavaFX приложения
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Конфигурация логгера
        PropertyConfigurator.configure(SOAPClientUI.class.getResource("/log4j.properties"));

        // Настройка пользовательского интерфейса
        primaryStage.setTitle("SOAP Client");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Label и TextField для адреса SOAP-сервиса
        Label soapAddressLabel = new Label("SOAP Service Address:");
        GridPane.setConstraints(soapAddressLabel, 0, 0);
        soapAddressField = new TextField();
        GridPane.setConstraints(soapAddressField, 1, 0);

        // Label и TextField для пути к файлу с ID
        Label filePathLabel = new Label("File Path:");
        GridPane.setConstraints(filePathLabel, 0, 1);
        filePathField = new TextField();
        GridPane.setConstraints(filePathField, 1, 1);

        // Button для отправки запроса
        Button sendRequestButton = new Button("Send Request");
        GridPane.setConstraints(sendRequestButton, 0, 2);
        sendRequestButton.setOnAction(e -> sendRequest());

        // TextArea для вывода ответа
        responseArea = new TextArea();
        responseArea.setEditable(false);
        GridPane.setConstraints(responseArea, 0, 3, 2, 1);

        // Добавляем элементы на GridPane
        grid.getChildren().addAll(soapAddressLabel, soapAddressField, filePathLabel, filePathField,
                sendRequestButton, responseArea);

        // Создаем сцену и добавляем GridPane
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendRequest() {
        try {
            // Получаем адрес SOAP-сервиса и путь к файлу с ID из полей ввода
            String soapAddress = soapAddressField.getText();
            String filePath = filePathField.getText();

            // Загружаем содержимое файла с ID в строку
            String ids = new String(Files.readAllBytes(Paths.get(filePath)));

            // Создаем фабрику клиентов
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient(soapAddress);

            // Вызываем метод DeleteDocuments с передачей ID
            Object[] result = client.invoke("DeleteDocuments", ids);

            // Очищаем TextArea и выводим ответ
            responseArea.clear();
            responseArea.setText("Response: " + result[0]);

            // Записываем ответ в лог
            logger.info("Response: " + result[0]);

        } catch (Exception e) {
            // В случае ошибки очищаем TextArea и записываем ошибку в лог
            responseArea.clear();
            responseArea.setText("Error occurred: " + e.getMessage());
            logger.error("Error occurred: " + e.getMessage(), e);
        }
    }
}
