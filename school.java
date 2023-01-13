import java.sql.*;
import java.util.Scanner;

public class school {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner sca = new Scanner(System.in);
        while (true) {
            switch (function()) {
                case 1:
                    System.out.println("1 Select all students\n2 Select all classes");
                    select(sca.nextInt());
                    break;
                case 2:
                    insert();
                    break;
                case 3:
                    System.out.println("Please input the SID of the student you want to change:");
                    update(sca.nextInt());
                    break;
                case 4:
                    System.out.println("Please input the SID of the student you want to delete:");
                    delete(sca.nextInt());
                    break;
                case 5:
                    System.out.println("Please input the CID of the class you want to change:");
                    updateClassID(sca.nextInt());
                    break;
                default:
                    System.err.println("Please select from 1-5");
            }
        }
    }

    //All functions of the system
    public static int function() {
        Scanner sca = new Scanner(System.in);
        System.out.println("Welcome to use the Academic Registration System");
        System.out.println("All functions are as follows:");
        System.out.println("1 Search for information on all students");
        System.out.println("2 Insert a new student");
        System.out.println("3 Update a student");
        System.out.println("4 Delete a new student");
        System.out.println("5 Update a class");

        System.out.println("Please choose number of the function you want:");
        return sca.nextInt();
    }

    //Connect java to database
    public static Connection connectDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/school?useUnicode=true&characterEncoding=utf8&uesSSL=true";
        String user = "root";
        String password = "12345";
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    //Select all the data from the table
    public static void select(int function2) throws ClassNotFoundException, SQLException {
        Connection con = connectDatabase();
        Statement statement = con.createStatement();
        if (function2 == 1) {
            String sql = "SELECT * FROM student";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("==================");
                System.out.println("sid=" + resultSet.getObject("SID"));
                System.out.println("name=" + resultSet.getObject("name"));
                System.out.println("gender=" + resultSet.getObject("gender"));
                System.out.println("CID=" + resultSet.getObject("CID"));
                System.out.println("==================");
            }
        } else if (function2 == 2) {
            String sql = "SELECT * FROM class";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("==================");
                System.out.println("cid=" + resultSet.getObject("CID"));
                System.out.println("SID=" + resultSet.getObject("SID"));
                System.out.println("comeTime=" + resultSet.getObject("comeTime"));
                System.out.println("==================");
            }
        } else {
            System.err.println("Please select from 1-2");
        }
    }

    //Insert the new data into the database
    public static void insert() throws ClassNotFoundException, SQLException {
        Connection connection = connectDatabase();
        String sql1 = "insert into `student` values(?,?,?,?)";
        String sql2 = "insert into `class` values(?,?,?)";
        PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
        Scanner sca = new Scanner(System.in);

        System.out.println("Please input the SID of new student:");
        int SID = sca.nextInt();
        System.out.println("Please input the name of new student:");
        String name = sca.next();
        System.out.println("Please input the gender of new student:");
        String gender = sca.next();
        System.out.println("Please input the CID of new student:");
        int CID = sca.nextInt();

        Time time = new Time(System.currentTimeMillis());

        preparedStatement1.setInt(1, SID);
        preparedStatement1.setString(2, name);
        preparedStatement1.setString(3, gender);
        preparedStatement1.setInt(4, CID);

        preparedStatement2.setInt(1, CID);
        preparedStatement2.setInt(2, SID);
        preparedStatement2.setTime(3, time);

        if (preparedStatement1.executeUpdate() == 1 && preparedStatement2.executeUpdate() == 1) {
            System.out.println("Successful inserted!");
        } else {
            System.out.println("Fail to insert");
        }
    }

    public static void update(int sid) throws ClassNotFoundException, SQLException {
        Connection connection = connectDatabase();
        String sql = "update student set SID=?,name=?,gender=?,CID=? where SID=" + sid;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        Scanner sca = new Scanner(System.in);

        System.out.println("Please input the new SID of the student:");
        int SID = sca.nextInt();
        System.out.println("Please input the new name of the student:");
        String name = sca.next();
        System.out.println("Please input the new gender of the student:");
        String gender = sca.next();
        System.out.println("Please input the new CID of the student:");
        int CID = sca.nextInt();

        preparedStatement.setInt(1, SID);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, gender);
        preparedStatement.setInt(4, CID);

        if (preparedStatement.executeUpdate() > 0) {
            System.out.println("Successful updated!");
        } else {
            System.out.println("Fail to update");
        }
    }


    //Delete the chosen data
    public static void delete(int sid) throws ClassNotFoundException, SQLException {
        Connection connection = connectDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("delete from Student where SID=" + sid);
        if (preparedStatement.executeUpdate() == 0) {
            System.out.println("The student is not in the school");
        } else {
            System.out.println("Successful deleted!");
        }
    }

    //Update the id of class
    public static void updateClassID(int cid) throws ClassNotFoundException, SQLException {
        Connection connection = connectDatabase();
        PreparedStatement preparedStatement = connection.prepareStatement("update student set CID=? where CID=" + cid);
        Scanner sca = new Scanner(System.in);

        System.out.println("Please input the CID of the class:");
        int CID = sca.nextInt();

        preparedStatement.setInt(1, CID);
        preparedStatement.executeUpdate();
    }
}
