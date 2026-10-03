package com.client;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class UtilsViews { //Esta clase, es, por asi decirlo, un gestor de ventanas 

    public static StackPane parentContainer = new StackPane(); //Este es el contenedor donde estan nuestras vistas

    public static ArrayList<Object> controllers = new ArrayList<>();

    // Esta funcion agrega los fxml al contenedor para mostrarlos
    public static void addView(Class<?> cls, String name, String path) throws Exception {

        boolean defaultView = false;

        FXMLLoader loader = new FXMLLoader(cls.getResource(path));

        Pane view = loader.load();

        ObservableList<Node> children = parentContainer.getChildren();

        if (children.isEmpty()) {
            defaultView = true;
        }

        view.setId(name);
        view.setVisible(defaultView);
        view.setManaged(defaultView);

        children.add(view);
        controllers.add(loader.getController());
    }

    // sirve para recuperar el controlador de una vista
    public static Object getController(String viewId) {

        int index = 0;

        for (Node n : parentContainer.getChildren()) {

            if (n.getId().equals(viewId)) {
                return controllers.get(index);
            }

            index++;
        }

        return null;
    }

    public static String getActiveView() {

        for (Node n : parentContainer.getChildren()) {

            if (n.isVisible()) {
                return n.getId();
            }
        }

        return null;
    }

    // Sirve para poder cambiar de pantalla
    public static void setView(String viewId) {

        ArrayList<Node> list = new ArrayList<>();
        list.addAll(parentContainer.getChildrenUnmodifiable());

        // Show selected view, hide the others
        for (Node n : list) {

            if (n.getId().equals(viewId)) {

                n.setVisible(true);
                n.setManaged(true);

            } else {

                n.setVisible(false);
                n.setManaged(false);
            }
        }

        parentContainer.requestFocus();
    }

}