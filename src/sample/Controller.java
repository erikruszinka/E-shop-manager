package sample;

import Connectivity.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    @FXML
    public Button carry_btn;
    @FXML
    public Button insert_btn;
    @FXML
    public TextField stuff_input;
    @FXML
    public TextField person_input;
    @FXML
    public TextField count_input;
    @FXML
    public TextField country_input;
    @FXML
    public Label result_label;
    @FXML
    public TableView<TableModel> table;
    @FXML
    public TableColumn<TableModel,String> name_column;
    @FXML
    public TableColumn<TableModel,String> person_column;
    @FXML
    public TableColumn<TableModel,Integer> count_column;
    @FXML
    public TableColumn<TableModel,String> country_column;

    private ObservableList<TableModel> list = FXCollections.observableArrayList();
    private int count_int;

    public void initialize(){
        try{
            DatabaseConnection dc = new DatabaseConnection();
            Connection connection = dc.getConnection();
            String query = "SELECT * FROM customers";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            list.clear();
            while(rs.next()){
                String name = rs.getString("Name");
                String person = rs.getString("Person");
                int count = rs.getInt("Count");
                String country = rs.getString("Country");
                list.add(new TableModel(name,person,count,country));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        name_column.setCellValueFactory(new PropertyValueFactory<>("Name"));
        person_column.setCellValueFactory(new PropertyValueFactory<>("Person"));
        count_column.setCellValueFactory(new PropertyValueFactory<>("Count"));
        country_column.setCellValueFactory(new PropertyValueFactory<>("Country"));
        table.setItems(list);
    }

    public void insertStuff() throws Exception {
        int err = 0;
        DatabaseConnection dc = new DatabaseConnection();
        Connection connection = dc.getConnection();
        Statement statement = connection.createStatement();
        String name = stuff_input.getText();
        String person = person_input.getText();
        String count = count_input.getText();
        String country = country_input.getText();

        String[] inputList = {name,person,count,country};
        for(String item : inputList){
            if(item.equals("")){
                err++;
            }
        }
        if(err>0){
            result_label.setAlignment(Pos.CENTER);
            result_label.setStyle("-fx-text-fill: #ff0004");
            result_label.setText("Fill all inputs!");
        } else {
            try{
                count_int = Integer.parseInt(count);
            } catch (Exception e){
                count_input.requestFocus();
                return;
            }
            String insert_query = "INSERT INTO customers (Name,Person,Count,Country) VALUES ('"+name+"','"+person+"','"+count_int+"','"+country+"')";
            String select_query = "SELECT ID,Count FROM customers WHERE name like '"+name+"' and person like '"+person+"' and country like '"+country+"'";
            ResultSet rs_sel = statement.executeQuery(select_query);
            if(rs_sel.next()){
                int newCount = rs_sel.getInt("Count")+count_int;
                statement.executeUpdate("UPDATE customers SET Count = '"+newCount+"'" +
                        " WHERE `customers`.`ID` = '"+rs_sel.getInt("ID")+"'");
                stuff_input.setText("");person_input.setText("");
                count_input.setText("");country_input.setText("");
                stuff_input.requestFocus();
                rs_sel.close();
            } else {
                statement.executeUpdate(insert_query);
                stuff_input.setText("");person_input.setText("");
                count_input.setText("");country_input.setText("");
                stuff_input.requestFocus();
            }
        }
        initialize();
    }

    public void carryStuff() throws SQLException {
        int err = 0;
        DatabaseConnection dc = new DatabaseConnection();
        Connection connection = dc.getConnection();
        Statement statement = connection.createStatement();
        String name = stuff_input.getText();
        String person = person_input.getText();
        String count_str = count_input.getText();
        String country = country_input.getText();

        String[] inputList = {name,person,count_str,country};
        for(String item : inputList){
            if(item.equals("")){
                err++;
            }
        } if(err>0){
            result_label.setAlignment(Pos.CENTER);
            result_label.setStyle("-fx-text-fill: #ff0004");
            result_label.setText("Fill all inputs!");
            stuff_input.requestFocus();
        } else {
            try{
                count_int = Integer.parseInt(count_str);
            } catch (Exception e){
                count_input.requestFocus();
                return;
            }
            String query = "SELECT Count,ID FROM customers WHERE name like '"+name+"'" +
                    " and person like '"+person+"'" + "and country like '"+country+"'";
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                int id = rs.getInt("ID");
                int count = rs.getInt("Count");
                if(count_int>count){
                    result_label.setAlignment(Pos.CENTER);
                    count_input.requestFocus();
                    return;
                } else if (count_int==count){
                    statement.executeUpdate("DELETE FROM customers WHERE ID like '"+id+"'");
                    result_label.setAlignment(Pos.CENTER);
                    stuff_input.setText("");person_input.setText("");
                    count_input.setText("");country_input.setText("");
                    stuff_input.requestFocus();
                } else {
                    rs.close();
                    int newCount = count-count_int;
                    statement.executeUpdate("UPDATE customers SET Count = '"+newCount+"' WHERE customers.ID = '"+id+"'");
                    result_label.setAlignment(Pos.CENTER);
                    stuff_input.setText("");person_input.setText("");
                    count_input.setText("");country_input.setText("");
                    stuff_input.requestFocus();
                }
            } else {
                result_label.setAlignment(Pos.CENTER);
            }
        }
        initialize();
    }
}