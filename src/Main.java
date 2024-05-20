
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        PolySportsDatabase myDatabase = PolySportsDatabase.getInstance();
        SportsDAO sportsDAO = new SportsDAO(myDatabase);
        try {
            for(Sport sport : sportsDAO.findByName()) {
                System.out.println(sport.getName());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}