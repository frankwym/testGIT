package com.dbconn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.util.JUtil;

public class MyJdbc {
    private String url;
    private String user;
    private String password;
    private String driver;

    public MyJdbc() {
        String path = JUtil.GetWebInfPath() + "prop//jdbc.properties";
        load(path);
    }

    public MyJdbc(String path) {
        load(path);
    }

    private void load(String path) {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        url = prop.getProperty("url");
        user = prop.getProperty("user");
        password = prop.getProperty("password");
        driver = prop.getProperty("driver");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }
}
