package com.example.cloudapplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private String homeDir;

    private byte[] buf;
    @FXML
    public ListView<String> serverView;
    @FXML
    public ListView<String> clientView;
    private Network network;


    private void readLoop() {
        try {
            while (true) {
                String command = network.readString();
                if(command.equals("#List#")){
                    Platform.runLater(() -> serverView.getItems().clear());
                    int len = network.readInt();
                    for (int i = 0; i < len; i++) {
                        String file = network.readString();
                        Platform.runLater(() -> serverView.getItems().add(file));
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("Connection lost");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            buf = new byte[256];
            homeDir = System.getProperty("user.home");
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(homeDir));
            network = new Network(8189);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private List<String> getFiles(String dir){
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

   public void upload(ActionEvent actionEvent) throws IOException {
        network.getOutputStream().writeUTF("#file#");
        String file = clientView.getSelectionModel().getSelectedItem();

        network.getOutputStream().writeUTF(file);
        File toSend = Path.of(homeDir).resolve(file).toFile();
        network.getOutputStream().writeLong(toSend.length());
        try(FileInputStream fis = new FileInputStream(toSend)){
            while (fis.available() > 0){
                int read = fis.read(buf);
                network.getOutputStream().write(buf,0,read);

            }
        }
        network.getOutputStream().flush();
    }

    public void download(ActionEvent actionEvent) {
    }


}