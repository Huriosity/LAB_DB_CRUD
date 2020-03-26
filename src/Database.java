import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static String url = System.getenv().get("db_url");
    private static String username = System.getenv().get("db_username");
    private static String password = System.getenv().get("db_password");

    public static void getAllInfoFromDatabaseAndWriteInFile(String filePath){
        ResultSet resultSet = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Connection succesfull!");
            try (Connection connection = DriverManager.getConnection(url, username,password)){
                System.out.println("Connection to rus_ruller DB succesfull!");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery("select * from ruller,ruller_town_relation,town WHERE (ruller.ruller_ID = ruller_town_relation.foreight_ruller_ID AND ruller_town_relation.foreight_town_ID = town.town_ID);");
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
    public static void createNewRecordInTheDatabase(ArrayList<ArrayList<String>> keyValuePair){
        String ruller_firstname = null;
        String ruller_patronomic = null;
        String ruller_title = null;
        String year_of_birth = null;
        String year_of_death = null;
        String town_name = null;
        int start_year = -1;
        int end_year = -1;

        String sqlRequest = null;

        for (int i = 0; i < keyValuePair.get(0).size(); i++ ){

            String currentKey = keyValuePair.get(0).get(i);
            String currentValue = keyValuePair.get(1).get(i);

            if (currentKey.equals("ruller_firstname")){
                ruller_firstname = currentValue;

            } else if(currentKey.equals("ruller_patronomic")){
                ruller_patronomic = currentValue;

            } else if(currentKey.equals("ruller_title")){
                ruller_title = currentValue;

            } else if(currentKey.equals("year_of_birth")){
                year_of_birth = getYearsOfLifiFromKeyValuePair(currentValue);

            } else if(currentKey.equals("year_of_death")){
                year_of_death = getYearsOfLifiFromKeyValuePair(currentValue);

            } else if(currentKey.equals("town_name")){
                town_name = currentValue;

            } else if(currentKey.equals("start_year")){
                start_year = getIntFromKeyValuePair(currentValue);

            } else if(currentKey.equals("end_year")){
                end_year = getIntFromKeyValuePair(currentValue);
            }
        }
        int ruller_id = getRullerId(ruller_firstname, ruller_patronomic, ruller_title);

        if (!checkFieldExistence( town_name,"town" )){
            sqlRequest = "INSERT town(town_name) VALUES ('" + town_name + "');";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);
        }
        if (ruller_id == -1){
            // first request exist
            sqlRequest = "INSERT ruller (ruller_firstname,ruller_patronomic,ruller_title) VALUES ('" +
            ruller_firstname + "','" + ruller_patronomic + "','" + ruller_title + "');";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);

            // 2nd request exist
            // ruller_years_of_life
            int last_ruller_id = getRullerId(ruller_firstname, ruller_patronomic, ruller_title);
            int town_ID = getTownIdByTownName(town_name);
            sqlRequest = "INSERT ruller_years_of_life(foreight_ruller_ID,year_of_birth,year_of_death) VALUES (" +
                    last_ruller_id +",'" +  year_of_birth + "','" + year_of_death + "');";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);

            // 3rd request exist
            // ruller_town_relation
            sqlRequest = "INSERT ruller_town_relation(foreight_ruller_ID,foreight_town_ID,start_year,end_year) VALUES (" +
            last_ruller_id + "," + town_ID + "," + start_year + "," + end_year + ");";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);

        } else{
            // first request in this case dosn't exist
            // 2nd request dosn't exist

            // 3rd request exist
            // ruller_town_relation
            int town_ID = getTownIdByTownName(town_name);
            sqlRequest = "INSERT ruller_town_relation(foreight_ruller_ID,foreight_town_ID,start_year,end_year) VALUES (" +
                    ruller_id + "," + town_ID + "," + start_year + "," + end_year + ");";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);

        }
    }

    public static void updateRecordInTheDatabase(ArrayList<ArrayList<String>> keyValuePair){

        String ruller_firstname = null;
        String ruller_patronomic = null;
        String ruller_title = null;
        String year_of_birth = null;
        String year_of_death = null;
        String town_name = null;
        int start_year = -1;
        int end_year = -1;
        int ruller_id = -1;
        int old_town_id = -1;

        String sqlRequest = null;

        for (int i = 0; i < keyValuePair.get(0).size(); i++ ){

            String currentKey = keyValuePair.get(0).get(i);
            String currentValue = keyValuePair.get(1).get(i);

            if (currentKey.equals("ruller_firstname")){
                ruller_firstname = currentValue;

            } else if(currentKey.equals("ruller_patronomic")){
                ruller_patronomic = currentValue;

            } else if(currentKey.equals("ruller_title")){
                ruller_title = currentValue;

            } else if(currentKey.equals("year_of_birth")){
                year_of_birth = getYearsOfLifiFromKeyValuePair(currentValue);

            } else if(currentKey.equals("year_of_death")){
                year_of_death = getYearsOfLifiFromKeyValuePair(currentValue);

            } else if(currentKey.equals("town_name")){
                town_name = currentValue;

            } else if(currentKey.equals("start_year")){
                start_year = getIntFromKeyValuePair(currentValue);

            } else if(currentKey.equals("end_year")){
                end_year = getIntFromKeyValuePair(currentValue);

            } else if (currentKey.equals("ruller_ID")){
                ruller_id = getIntFromKeyValuePair(currentValue);

            } else if (currentKey.equals("foreight_town_ID")){
                old_town_id = getIntFromKeyValuePair(currentValue);

            }
        }

        int foreight_town_ID = getTownIdByTownName(town_name);

        sqlRequest = "UPDATE ruller_years_of_life SET year_of_birth = '" + year_of_birth + "',year_of_death = '"
                + year_of_death + "' WHERE foreight_ruller_ID = " + ruller_id + ";";
        System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
        executeTheGivenСommandForTheDatabase(sqlRequest);

        if (foreight_town_ID == -1){
            sqlRequest = "INSERT town(town_name) VALUES ('" + town_name + "');";
            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);

            foreight_town_ID = getTownIdByTownName(town_name);
            sqlRequest = "UPDATE ruller_town_relation SET start_year = "
                    + start_year + ", end_year = " + end_year + ", foreight_town_ID = " + foreight_town_ID +
                    " WHERE foreight_ruller_ID = " + ruller_id +
                    " AND foreight_town_ID = " + old_town_id + ";";

            executeTheGivenСommandForTheDatabase(sqlRequest);
        } else {

            sqlRequest = "UPDATE ruller_town_relation SET start_year = "
                    + start_year + ", end_year = " + end_year + ", foreight_town_ID = " + foreight_town_ID +
                    " WHERE foreight_ruller_ID = " + ruller_id +
                    " AND foreight_town_ID = " + old_town_id + ";";

            System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
            executeTheGivenСommandForTheDatabase(sqlRequest);
        }

        sqlRequest = "UPDATE ruller SET ruller_firstname = '" + ruller_firstname + "', ruller_patronomic = '" +
                ruller_patronomic + "', ruller_title = '" + ruller_title + "' WHERE ruller_ID = " + ruller_id + ";" ;

        System.out.println("CONFIGURATE THIS sqlRequest: " + sqlRequest);
        executeTheGivenСommandForTheDatabase(sqlRequest);

    }

    private static String getYearsOfLifiFromKeyValuePair(String str) {
        if(str.isEmpty()){
            return "UNKNOWN";
        }
        return str;
    }

    private static int getIntFromKeyValuePair(String str){
        if(str.isEmpty()){
            return -1;
        }
        return Integer.parseInt(str);
    }

    private static int getRullerId(String ruller_firstname, String ruller_patronomic, String ruller_title){
        String sqlRequest = null;
        sqlRequest = "select (ruller_ID) from ruller WHERE ruller_firstname = '" + ruller_firstname +
                "' AND ruller_patronomic = '" + ruller_patronomic + "' AND ruller_title = '" + ruller_title + "';";

        System.out.println("SQL REQUEST = " + sqlRequest);
        return getSomeIdFromSqlRequest(sqlRequest);
    }

    private static int getTownIdByTownName(String town_name){
        String sqlRequest = null;
        sqlRequest = "select (town_ID) from town WHERE town_name = '" + town_name + "';";
        System.out.println("SQL REQUEST = " + sqlRequest);
        return getSomeIdFromSqlRequest(sqlRequest);
    }

    // return -1 if the database does not have record
    // return id if record exist
    private static int getSomeIdFromSqlRequest(String sqlRequest){
        int id = -1;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Connection succesfull!");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connection to rus_ruller DB succesfull!");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(sqlRequest);
                while (resultSet.next()) {// тУТ ДВИЖЕНИЕ ПО ROW
                    id = resultSet.getInt(1);
                }
                System.out.println("GET ID = " + id);
            }
        } catch (Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }

        return id;
    }

    private static Boolean checkFieldExistence(String willCheck, String whereCkeck){
        String sqlRequest = "Select * FROM " + whereCkeck;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Connection succesfull!");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connection to rus_ruller DB succesfull!");
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(sqlRequest);
                String tmp = null;
                while (resultSet.next()) {// тУТ ДВИЖЕНИЕ ПО ROW
                    tmp = resultSet.getString(2);
                    System.out.println("TMP == " + tmp);
                    System.out.println("willcheck == " + willCheck);
                    if (tmp.equals( willCheck )){
                        return true;
                    }
                }
            }
        } catch (Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
        return false;
    }

    private static void executeTheGivenСommandForTheDatabase(String sqlRequest){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            System.out.println("Connection succesfull!");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connection to rus_ruller DB succesfull!");
                Statement statement = connection.createStatement();
                int rows = statement.executeUpdate(sqlRequest);
                System.out.printf("Added %d rows", rows);
            }
        } catch (Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }
}
