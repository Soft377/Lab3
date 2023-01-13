import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public class weatherSystem {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        String[] cities = {"Beijing", "Shanghai", "Fuzhou"};
        inputStream(cities[0]);
        inputStream(cities[1]);
        inputStream(cities[2]);

        while (true) {
            switch (function()) {
                case 1:
                    System.out.println("Please choose the city you want to search:");
                    System.out.println("1 Beijing\n2 Shanghai\n3 Fuzhou");
                    Scanner sca = new Scanner(System.in);
                    int input = sca.nextInt();
                    selectInformation(input);
                    break;
                case 2:
                    System.out.println("Please choose the city you want to search:");
                    System.out.println("1 Beijing\n2 Shanghai\n3 Fuzhou");
                    Scanner sca2 = new Scanner(System.in);
                    int input2 = sca2.nextInt();
                    selectWeather(input2);
                    break;
            }
        }
    }

    //The functions of the system
    public static int function() {
        Scanner sca = new Scanner(System.in);
        System.out.println("Welcome to use the Weather System");
        System.out.println("All functions are as follows:");
        System.out.println("1 Search the basic information of one city");
        System.out.println("2 Search the weather information of one city");;
        return sca.nextInt();
    }

    //get the data by using api
    public static void inputStream(String ss) throws IOException, SQLException, ClassNotFoundException {
        String str = URLEncoder.encode(ss, "UTF-8");
        //basic information
        String url1 = ("https://geoapi.qweather.com/v2/city/lookup?key=7fe6bfbf7e554a2b91055f27ba02ebb8&location=" + str);
        String url2 = "https://devapi.qweather.com/v7/weather/3d?key=7fe6bfbf7e554a2b91055f27ba02ebb8&location=";
        String[] url = {url1, url2};
        for (int i = 0; i < url.length; i++) {
//            if(i == 1){
//                Connection con = connectDatabase();
//                Statement statement = con.createStatement();
//                String sql = "SELECT * FROM city where cityName =" + "'" + ss + "'";
//                ResultSet resultSet = statement.executeQuery(sql);
//
//                int id = 0;
//                while(resultSet.next()) {
//                    id = (Integer) resultSet.getObject("ID");
//                    System.out.println(id);
//
//            }
//                //Bug1:Fail to add id to url2
//                url2 += id;
//            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url[i]).openConnection();
            GZIPInputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());

            StringBuilder res = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            if (i == 0) {
               insert2city(res, ss);
            } else {
                //Bug2:Because of Bug1,the method can't run
                //insert2weather(res,ss);
            }

        }
    }

    public static void insert2city(StringBuilder res, String str) throws SQLException, ClassNotFoundException {
        JSONObject object = JSONObject.fromObject(res.toString());

        String baseInformation = object.getJSONArray("location").toString();
        JSONArray jsonArr = JSONArray.fromObject(baseInformation);

        JSONObject jsonObject = jsonArr.getJSONObject(0);
        int id = jsonObject.getInt("id");
        double lat = jsonObject.getDouble("lat");
        double lon = jsonObject.getDouble("lon");
        Connection connection = connectDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("insert into `city` values(?,?,?,?)");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, str);
        preparedStatement.setDouble(3, lat);
        preparedStatement.setDouble(4, lon);

        preparedStatement.executeUpdate();
    }

    public static void insert2weather(StringBuilder res, String str) throws SQLException, ClassNotFoundException {
        JSONObject object = JSONObject.fromObject(res.toString());
        String baseInformation = object.getJSONArray("daily").toString();
        JSONArray jsonArr = JSONArray.fromObject(baseInformation);

        JSONObject jsonObject = jsonArr.getJSONObject(0);
        String fxDate = jsonObject.getString("fxDate");
        double tempMax = jsonObject.getDouble("tempMax");
        double tempMin = jsonObject.getDouble("tempMin");
        String textDay = jsonObject.getString("textDay");
        Connection connection = connectDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("insert into `weather` values(?,?,?,?,?)");
        preparedStatement.setDate(1, Date.valueOf(fxDate));
        preparedStatement.setString(2, str);
        preparedStatement.setDouble(3, tempMax);
        preparedStatement.setDouble(4, tempMin);
        preparedStatement.setString(5, textDay);

        preparedStatement.executeUpdate();
    }

    //Connect to database
    public static Connection connectDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/weathersystem?useUnicode=true&characterEncoding=utf8&uesSSL=true";
        String user = "root";
        String password = "12345";
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }


    public static void selectInformation(int input) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
        Connection con = connectDatabase();
        Statement statement = con.createStatement();
        String city = "";
        if (input == 1) {
            city = "Beijing";
        } else if (input == 2) {
            city = "Shanghai";
        } else if (input == 3) {
            city = "Fuzhou";
        } else {
            System.err.println("Please select from 1-3");
        }

        String sql = "SELECT * FROM city where cityName =" + "'" + city + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println("==================");
            System.out.println("ID=" + resultSet.getObject("ID"));
            System.out.println("lat=" + resultSet.getObject("lat"));
            System.out.println("lon=" + resultSet.getObject("lon"));
            System.out.println("==================");
        }
    }

    //select the weather of cities
    public static void selectWeather(int input) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
        Connection con = connectDatabase();
        Statement statement = con.createStatement();
        String city = "";
        if (input == 1) {
            city = "Beijing";
        } else if (input == 2) {
            city = "Shanghai";
        } else if (input == 3) {
            city = "Fuzhou";
        } else {
            System.err.println("Please select from 1-3");
        }

        String sql = "SELECT * FROM weather where cityName = " + "'" + city + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println("==================");
            System.out.println("fxDate=" + resultSet.getObject("fxDate"));
            System.out.println("tempMax=" + resultSet.getObject("tempMax"));
            System.out.println("tempMin=" + resultSet.getObject("tempMin"));
            System.out.println("textDay=" + resultSet.getObject("textDay"));
            System.out.println("==================");
        }
    }

}



