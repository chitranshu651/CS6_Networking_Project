package Login_Signup;


import java.io.Serializable;

public class User implements Serializable {
    private String First;
    private String Last;
    private String Email;
    private String Username;
    private String Password;
    private String salt;


    public User(String first, String last, String email, String username, String password, String salt) {
        First = first;
        Last = last;
        Email = email;
        Username = username;
        Password = password;
        this.salt = salt;
    }

    public String getFirst() {
        return First;
    }

    public String getLast() {
        return Last;
    }

    public String getEmail() {
        return Email;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getSalt() {
        return salt;
    }



}
