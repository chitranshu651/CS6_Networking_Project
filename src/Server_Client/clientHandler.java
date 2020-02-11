package Server_Client;

import App.Message;
import Login_Signup.User;
import Misc.PasswordUtils;


import java.io.*;
import java.net.Socket;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;


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
                        ObjectOutput.writeObject(GetMessage());
                        break;
                    case "GetProfile":
                        // TODO GetProfile
                        ObjectOutput.writeObject(GetProfile());
                        break;
                    case "Search":
                        // TODO Search
                        ObjectOutput.writeObject(Search());
                        break;
                    case "SendMessage":
                        // TODO SendMessage
                        SendMessage();
                        break;
                    case "GetUsers":
                        ObjectOutput.writeObject(GetUsers());
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

    private ArrayList<User> GetUsers() {
        ArrayList<User> names = new ArrayList<User>();
        try{
            String sql = "SELECT `First`,`Last`,`Username`,`Email` FROM `user`;";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String first = rs.getString("First");
                String last = rs.getString("Last");
                String email = rs.getString("Email");
                String username = rs.getString("Username");
                User user1 = new User(first, last, email, username, "", "");
                names.add(user1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    private void SendMessage() throws IOException, ClassNotFoundException {
        Message message = (Message) ObjectInput.readObject();
        String sql = "INSERT INTO messages values(NULL,\"" + message.getSender() + "\",\"" + message.getReciever() + "\",\"" + message.getText() + "\",\"" + message.getTime() + "\");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<User> Search() {
        ArrayList<User> names = new ArrayList<User>();
        try {
            String user = dataInput.readUTF();
            String current = dataInput.readUTF();
            String sql = "SELECT `First`,`Last`,`Username`,`Email` FROM `user` WHERE `Username` LIKE \'" + user + "%\' and Username!= \"" + current + "\";";

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String first = rs.getString("First");
                String last = rs.getString("Last");
                String email = rs.getString("Email");
                String username = rs.getString("Username");
                User user1 = new User(first, last, email, username, "", "");
                names.add(user1);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Search done");
        return names;

    }


    private User GetProfile() {
        try {
            String user = dataInput.readUTF();
            String sql = "SELECT * from user where `Username`=\"" + user + "\";";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String first = rs.getString("First");
                String last = rs.getString("Last");
                String email = rs.getString("Email");
                String username = rs.getString("Username");
                User user1 = new User(first, last, email, username, "", "");
                System.out.println(System.getProperty("user.dir"));
                return user1;
            }
        } catch (IOException | SQLException e) {
            System.out.println(e);
        }

        return null;
    }


    private ArrayList<Message> GetMessage() {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            String user1 = dataInput.readUTF();
            String user2 = dataInput.readUTF();
            String sql = "select * from messages where reciever=\"" + user1 + "\" and  sender=\"" + user2 + "\" or reciever=\"" + user2 + "\" and sender=\"" + user1 + "\" order by time limit 100;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Message message = new Message();
                message.setText(rs.getString("message"));
                message.setReciever(rs.getString("reciever"));
                message.setSender(rs.getString("sender"));
                message.setTime(rs.getTimestamp("time"));
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
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
