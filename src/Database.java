import java.nio.file.Path;
import java.sql.*;

public class Database {

    private static String url = System.getenv().get("db_url");
    private static String username = System.getenv().get("db_username");
    private static String password = System.getenv().get("db_password");
    //private String sqlRequest;

    public static void getAllInfoFromDatabaseAndWriteInFile(String filePath){
        ResultSet resultSet = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Connection succesfull!");
            try (Connection connection = DriverManager.getConnection(url, username,password)){
                System.out.println("Connection to rus_ruller DB succesfull!");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery("select * from ruller,ruller_town_relation,town WHERE (ruller.ruller_ID = ruller_town_relation.foreight_ruller_ID AND ruller_town_relation.town_name = town.town_name);");
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int colNum = rsmd.getColumnCount();
                int rowNum = 0;
                try {
                    resultSet.last();
                    rowNum = resultSet.getRow();
                    resultSet.beforeFirst();
                } catch(Exception ex) {
                    System.err.println(ex);
                }
                String[] arr_col_names = new String[colNum];
                String[][] fullDataFromSet = new String[colNum][rowNum];
                for(int i = 0; i < colNum; i++){
                    arr_col_names[i] = rsmd.getColumnName(i+1);
                }
                int iter = 1;

                while (resultSet.next()) {// тУТ ДВИЖЕНИЕ ПО ROW
                    for(int i = 0; i < colNum; i++){
                        if(rsmd.getColumnClassName(i+1) == "java.lang.Integer"){
                            try {
                                fullDataFromSet[i][iter-1] = Integer.toString(resultSet.getInt(i+1));
                            }catch (Exception err){
                                System.err.println(err);
                            }
                        }else if(rsmd.getColumnClassName(i+1) == "java.lang.String"){
                            try {
                                fullDataFromSet[i][iter-1] = (String) resultSet.getString(i+1);
                            } catch (Exception err){
                                System.err.println(err);
                            }
                        }
                    }
                    iter++;
                }
                xmlParser.writeXML(arr_col_names,fullDataFromSet,filePath);
            }
        } catch (Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }
}
