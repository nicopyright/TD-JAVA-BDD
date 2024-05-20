import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SportsDAO {
    private MYSQLDatabase database;

    public SportsDAO(MYSQLDatabase database){
        this.database = database;
    }
    public Sport[] findAll(){
        List<Sport> sport = new ArrayList<Sport>();
        database.connect();
        Statement myStatement = database.createStatement();
        try {
            ResultSet results = myStatement.executeQuery("SELECT * FROM `sport`");
            while (results.next()) {
                sport.add(new Sport(results.getInt("id"),results.getString("name"),results.getInt("required_participants")));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //conversion de la List en Array
        Sport[] array_sport = new Sport[ sport.size() ];
        sport.toArray(array_sport);
        return array_sport;
    }

    public Sport findById(int id){
        database.connect();
        try {
            PreparedStatement myStatement = database.prepareStatement("SELECT * FROM `sport` WHERE `id` = ?");
            myStatement.setInt(1, id);

            ResultSet results = myStatement.executeQuery(myStatement.toString());
            while (results.next()) {
                return new Sport(results.getInt("id"),results.getString("name"),results.getInt("required_participants"));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Sport[] findByName(){
        List<Sport> sport = new ArrayList<Sport>();
        database.connect();
        System.out.println("Keyword to search :");
        Scanner myScanner = new Scanner(System.in);
        String name = myScanner.nextLine();
        try {
            String query = "SELECT * FROM `sport` WHERE `name` LIKE ?";
            PreparedStatement myStatement = database.prepareStatement(query);
            myStatement.setString(1, "%"+name+"%");
            ResultSet results = myStatement.executeQuery();
            while (results.next()) {
                sport.add(new Sport(results.getInt("id"),results.getString("name"),results.getInt("required_participants")));
            }
        }
        catch (Exception e) {
            System.out.println("ici");
            System.out.println(e.getMessage());
        }
        //conversion de la List en Array
        Sport[] array_sport = new Sport[ sport.size() ];
        sport.toArray(array_sport);
        return array_sport;
    }
}
