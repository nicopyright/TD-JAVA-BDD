# COMPTE RENDU TD JAVA BDD
## OBJECTIFS
A travers cet exercice, nous allons :
- Utiliser Java pour vous connecter à une base de données,
- Créer une architecture combinant objets métiers et DAO

## PREMIERS PAS AVEC JDBC
### CONNEXION
Le code suivant montre comment établir une connexion à une base de données MySQL en utilisant JDBC.
```java
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection myConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Poly-sports",
                    "root",
                    "");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```
Ce code illustre l'utilisation de JDBC pour se connecter à une base de données MySQL. La méthode Class.forName charge le driver JDBC, nécessaire pour établir la connexion. Le DriverManager.getConnection est ensuite utilisé pour ouvrir la connexion à la base de données en fournissant l'URL, le nom d'utilisateur, et le mot de passe.
### PREMIERE REQUETE
Le code suivant montre comment exécuter une requête SQL et afficher les résultats.

```java
public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        Connection myConnection = null;
        try {
            myConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Poly-sports",
                    "root",
                    "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            Statement myStatement = myConnection.createStatement();
            ResultSet results = myStatement.executeQuery("SELECT * FROM `sport`");
            System.out.println("id" + " | " + "name" + " | " + "required_participants");
            System.out.println("-------------------------------");
            while (results.next()) {
                System.out.println(results.getString("id") + " | " + results.getString("name") + " | " + results.getString("required_participants"));
                System.out.println("-------------------------------");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```
Ce code montre comment exécuter une requête SQL et traiter les résultats. La méthode createStatement de la connexion est utilisée pour créer un objet Statement. La méthode executeQuery du Statement exécute la requête SQL, et les résultats sont traités via l'objet ResultSet.

résultat :
```
id | name | required_participants
-------------------------------
1 | Badminton (simple) | 2
-------------------------------
2 | Badminton (double) | 4
-------------------------------
3 | Basket | 10
-------------------------------
```
## STRUCTURONS TOUT CELA
### MYSQLDATABASE
```java
public class MYSQLDatabase {
    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private Connection connection;
    private static boolean driverLoaded;

    public MYSQLDatabase(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.connection = null;
        this.driverLoaded = false;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port+ "/"+ databaseName,
                    user,
                    password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void loadDriver() {
        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
                return;
            }
            driverLoaded = true;
        }
    }
}
```
La classe MYSQLDatabase encapsule les détails de connexion à une base de données MySQL. Elle contient des méthodes pour se connecter à la base de données (connect), créer des déclarations SQL (createStatement), et charger le driver JDBC (loadDriver). Cela permet de centraliser et de réutiliser la logique de connexion à la base de données.
Utilisation de MYSQLDatabase dans le main :
```java
public class Main {
    public static void main(String[] args) {
        MYSQLDatabase myDatabase = new MYSQLDatabase("localhost", 3306, "Poly-sports", "root", "");
        myDatabase.connect();
        Statement myStatement = myDatabase.createStatement();
        try {
            ResultSet results = myStatement.executeQuery("SELECT * FROM `sport`");
            System.out.println("id" + " | " + "name" + " | " + "required_participants");
            System.out.println("-------------------------------");
            while (results.next()) {
                System.out.println(results.getString("id") + " | " + results.getString("name") + " | " + results.getString("required_participants"));
                System.out.println("-------------------------------");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```
### POLY-SPORTS DATABASE
```java
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
```
PolySportsDatabase est une sous-classe de MYSQLDatabase qui utilise le pattern Singleton pour garantir qu'une seule instance de la classe existe. Ceci est utile pour gérer une connexion unique à la base de données dans toute l'application.

Dans le main on remplace :
```java
MYSQLDatabase myDatabase = new MYSQLDatabase("localhost", 3306, "Poly-sports", "root", "");
```
par:
```java
PolySportsDatabase myDatabase = PolySportsDatabase.getInstance();
```

### SPORT

```java
public class Sport {
    private int id;
    private String name;
    private int requiredParticipants;

    public Sport(int id, String name, int requiredParticipants){
        this.id = id;
        this.name = name;
        this.requiredParticipants = requiredParticipants;
    }

    public int getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public int getRequiredParticipants(){
        return requiredParticipants;
    }
}
```

```java
Sport foot = new Sport(4,"Football",10);
System.out.println(foot.getId() + " " + foot.getName() + " " + foot.getRequiredParticipants());
```
```
4 Football 10
```
La classe Sport représente un objet métier dans l'application. Elle encapsule les attributs et comportements associés à un sport

### SPORTSDAO
```java
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
}
```
La classe SportsDAO est responsable de l'accès aux données des sports dans la base de données. Elle contient des méthodes pour récupérer tous les sports (findAll), trouver un sport par son identifiant (findById), et trouver des sports par leur nom (findByName). Ces méthodes utilisent la connexion à la base de données fournie par l'objet MYSQLDatabase.

Utilisation de SportsDAO dans le main :
```java
public class Main {
    public static void main(String[] args) {
        PolySportsDatabase myDatabase = PolySportsDatabase.getInstance();
        SportsDAO sportsDAO = new SportsDAO(myDatabase);
        Sport[] sports = sportsDAO.findAll();
        for(Sport sport : sports) {
            System.out.println(sport.getName());
        }
    }
}
```
résultat :
```
Badminton (simple)
Badminton (double)
Basket
```

### FINDBYID
```java
public Sport findById(int id){
    database.connect();
    Statement myStatement = database.createStatement();
    try {
        ResultSet results = myStatement.executeQuery("SELECT * FROM `sport` WHERE `id`="+id);
        while (results.next()) {
            return new Sport(results.getInt("id"),results.getString("name"),results.getInt("required_participants"));
        }
    }
    catch (Exception e) {
        System.out.println(e.getMessage());
    }
    return null;
}
```
La méthode findById permet de récupérer un sport en utilisant son identifiant unique. Si l'identifiant n'est pas trouvé, la méthode retourne null.

Utilisation de findById dans le main :
```java
public class Main {
    public static void main(String[] args) {
        PolySportsDatabase myDatabase = PolySportsDatabase.getInstance();
        SportsDAO sportsDAO = new SportsDAO(myDatabase);
        try {
            System.out.println(sportsDAO.findById(3).getName());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        try {
            System.out.println(sportsDAO.findById(4).getName());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
```
résultat :
```
Basket
Cannot invoke "Sport.getName()" because the return value of "SportsDAO.findById(int)" is null
```
on obtient une erreur car l'ID 4 n'existe pas dans la base de données.

### FINDBYNAME

```java
public Sport[] findByName(String name){
        List<Sport> sport = new ArrayList<Sport>();
        database.connect();
        Statement myStatement = database.createStatement();
        try {
            ResultSet results = myStatement.executeQuery("SELECT * FROM `sport` WHERE `name` LIKE '%"+name+"%'");
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
```
La méthode findByName permet de rechercher des sports par nom, en utilisant une clause LIKE SQL pour permettre des recherches partielles.

Utilisation de findByName dans le main :
```java
public class Main {
    public static void main(String[] args) {
        PolySportsDatabase myDatabase = PolySportsDatabase.getInstance();
        SportsDAO sportsDAO = new SportsDAO(myDatabase);
        try {
            for(Sport sport : sportsDAO.findByName("Bad")) {
                System.out.println(sport.getName());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
```
résultat :
```
Badminton (simple)
Badminton (double)
```
On enlève le paramètre 'name' de la méthode findByName, on le remplace par un Scanner dans la méthode:
```java
System.out.println("Keyword to search :");
        Scanner myScanner = new Scanner(System.in);
        String name = myScanner.nextLine();
```
résultat :
```
Keyword to search :
Bad
Badminton (simple)
Badminton (double)
```

## INJECTION SQL
### ALLOWMULTIQUERIES
```java
public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port+ "/" + databaseName + "?allowMultiQuerie=true",
                    user,
                    password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
```
Test de l'injection SQL :

```
search :
'; DELETE FROM sport WHERE 1; -- 
Badminton (simple)
Badminton (double)
Basket

Process finished with exit code 0
```
Résultat de la tentative d'injection :
```
MariaDB [poly-sports]> SELECT * FROM sport;
Empty set (0.000 sec)
```
L'injection SQL a réussi à supprimer toutes les entrées de la table sport.

La configuration allowMultiQueries permet l'exécution de multiples requêtes dans une seule instruction SQL, ce qui peut être exploité pour les attaques par injection SQL. Il est important de désactiver cette option ou d'utiliser des PreparedStatement pour se protéger contre de telles vulnérabilités.
### PREPAREDSTATEMENT   
ajout de la méthode prepareStatement dans MYSQLDatabase :
```java
public PreparedStatement prepareStatement(String data) {
    try {
        return connection.prepareStatement(data);
    } catch (SQLException e) {

        throw new RuntimeException(e);
    }
}
```
Modification de la méthode findByName et findByIs dans SportsDAO :
```java
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
```
ajout de sports dans la base de données :

```
+----+---------------+-----------------------+
| id | name          | required_participants |
+----+---------------+-----------------------+
|  1 | Badminton (s) |                     2 |
|  2 | Badminton (d) |                     4 |
+----+---------------+-----------------------+
```
recherche par nom avec le mot clé "bad":
```
Keyword to search :
Bad
Badminton (s)
Badminton (d)
```
tentative d'injection SQL :
```
Keyword to search :
'; DELETE FROM sport WHERE 1; -- 
```
echeque de l'injection SQL, la table sport n'a pas été vidée.:
```
select * from sport;
+----+---------------+-----------------------+
| id | name          | required_participants |
+----+---------------+-----------------------+
|  1 | Badminton (s) |                     2 |
|  2 | Badminton (d) |                     4 |
+----+---------------+-----------------------+
2 rows in set (0.000 sec)
```
## CONCLUSION

A travers cet exercice, nous avons utilisé Java pour nous connecter à une base de données MySQL en utilisant JDBC. Nous avons créé une architecture combinant objets métiers et DAO pour gérer l'accès aux données des sports. Nous avons également exploré les vulnérabilités d'injection SQL et comment les prévenir en utilisant des PreparedStatement. Ce TD nous a permis de mieux comprendre les concepts de base de la programmation Java avec une base de données et de renforcer nos compétences en matière de sécurité des applications.
