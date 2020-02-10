package Server_Client;

import Login_Signup.User;
import Misc.PasswordUtils;


import java.io.*;
import java.net.Socket;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class clientHandler implements Runnable {

    private Socket client;

    private ObjectInputStream ObjectInput;
    private ObjectOutputStream ObjectOutput;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private String name;
    private ConnectionClass databaseConnect = new ConnectionClass();
    private Connection connection = databaseConnect.getconnection();
    private PasswordUtils check = new PasswordUtils();



    public clientHandler(Socket client, String name, ObjectOutputStream ObjectOutput, ObjectInputStream ObjectInput, DataOutputStream dataOutput, DataInputStream dataInput) {
        this.name = name;
        this.client = client;
        this.ObjectOutput = ObjectOutput;
        this.ObjectInput = ObjectInput;
        this.dataInput = dataInput;
        this.dataOutput = dataOutput;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String recieved = dataInput.readUTF();
                if (recieved.equalsIgnoreCase("Exit")) {
                    System.out.println("Client Connection Terminating");
                    this.client.close();
                    break;
                }
                switch (recieved) {
                    case "Login":
                        dataOutput.writeBoolean(login());
                        break;
                    case "Signup":
                        dataOutput.writeBoolean(Signup());
                        break;
                    case "GetMessages":
                        // TODO GetMessages
                        break;
                    case "GetProfile":
                        // TODO GetProfile
                        break;
                    case "Search":
                        // TODO Search
                        break;
                    case "SendMessage":
                        // TODO SendMessage
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
                break;
            }

        }
        try {
            ObjectOutput.close();
            ObjectInput.close();
            dataOutput.close();
            dataInput.close();
            client.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private boolean Signup() {
        try
        {
            User register = (User) ObjectInput.readObject();
            String sql = "INSERT INTO user values(\"" + register.getFirst() + "\", \"" + register.getLast() + "\", \"" + register.getEmail() + "\", \"" + register.getUsername() + "\", \"" + register.getPassword() + "\", \"" + register.getSalt() + "\");";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            System.out.println("Signup Done");
            return true;
        } catch (IOException | SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return false;
    }


    private boolean login() {
        try {
            String username = dataInput.readUTF();
            String password = dataInput.readUTF();
            String sql = "SELECT `Password`, `Salt` FROM `user` WHERE `Username`=\"" + username + "\";";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            String Spassword = "";
            String Salt = "A2B2";

            while (rs.next()) {
                Spassword = rs.getString("Password");
                Salt = rs.getString("Salt");
            }
            System.out.println("Login Done");
            if (check.verifyUserPassword(password, Spassword, Salt)) {
                return true;
            }
        } catch (IOException | SQLException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }


}
