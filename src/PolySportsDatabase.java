public class PolySportsDatabase extends MYSQLDatabase{
    private static PolySportsDatabase instance = null;

    private PolySportsDatabase() {
        super("localhost", 3306, "Poly-sports", "root", "");
    }

    public static PolySportsDatabase getInstance() {
        if (instance == null) {
            instance = new PolySportsDatabase();
        }
        return instance;
    }
}
