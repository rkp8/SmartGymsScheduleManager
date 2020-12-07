package com.example.slabiak.appointmentscheduler.controller;

import com.example.slabiak.appointmentscheduler.security.CustomUserDetails;
import com.example.slabiak.appointmentscheduler.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showHome(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {

        int inclass = 30;
        String[] starts = {};
        String[] ends = {};
        String arr = "";
        String arr1 = "";
        String arr2 = "";
        String arr3 = "";
        int count = 0;


//TODO: Add pooling

        String configFile = "src/main/resources/db.properties";
        HikariConfig cfg = new HikariConfig(configFile);
        HikariDataSource ds = new HikariDataSource(cfg);


        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;


        String url1 = "jdbc:mysql://bc3abcb07a2286:8591c741@us-cdbr-east-02.cleardb.com/heroku_ccf46755d26bc4f?serverTimezone=UTC&reconnect=true";
        String use = "bc3abcb07a2286";
        String password2 = "8591c741";

        //Load data from MySQL database:
        //-------------------------------------------------------------------------------------------------------------

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            // con = ds.getConnection();
            con = ds.getConnection();

            String sql = "SELECT start FROM appointments";
            pst = con.prepareStatement(sql);
            //pst.setString(1, Album);

            rs = pst.executeQuery();
            //System.out.println(rs);

            String all;
            while (rs.next()) {
                all = rs.getString("start");
                if (all != null)
                    arr += "," + all;
                //System.out.println(arr);
            }

            arr = arr.replace("null", "");
            starts = arr.split(",");

            for(String item : starts) {
                System.out.println("starts");
                System.out.println(item);


            }


            sql = "SELECT end FROM appointments";
            pst = con.prepareStatement(sql);
            //pst.setString(1, Album);

            rs = pst.executeQuery();
            //System.out.println(rs);

            String all1;
            while (rs.next()) {
                all1 = rs.getString("end");
                if (all1 != null)
                    arr1 += "," + all1;
                //System.out.println(arr);
            }

            arr1 = arr1.replace("null", "");
            ends = arr1.split(",");

            for(String item : ends)
                System.out.println(item);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {

            try {

                if(rs != null){
                    rs.close();
                }

                if (pst != null) {
                    pst.close();
                }

                if (con != null) {
                    con.close();
                }

                ds.close();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


            String [] starts1 = {};
            String [] ends1 = {};

            starts1 = arr.split(",");
            ends1 = arr1.split(",");

            for(int i = 1; i<starts1.length; i++){
               /* System.out.println("Arrays");

                System.out.println(arr);
                System.out.println(arr1);

                System.out.println("start");
                System.out.println(starts1[i]);
                System.out.println("end");
                System.out.println(ends1[i]);*/


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime start = LocalDateTime.parse(starts1[i], formatter);
                //start = start.minus(5, ChronoUnit.HOURS);
                LocalDateTime end = LocalDateTime.parse(ends1[i], formatter);
                //end = end.minus(5, ChronoUnit.HOURS);

                if(LocalDateTime.now().isAfter(start) && LocalDateTime.now().isBefore(end))
                    inclass-=1;

            }

            System.out.println(inclass);



        }
        model.addAttribute("user", userService.getUserById(currentUser.getId()));
        model.addAttribute("inclass", inclass);

        return "home";
    }

    @GetMapping("/login")
    public String login(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser != null) {
            return "redirect:/";
        }
        return "users/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "access-denied";
    }


}
