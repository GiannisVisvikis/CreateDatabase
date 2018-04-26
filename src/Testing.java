import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Testing
{

    private static ArrayList<Season> allSeasons = new ArrayList<>();

    public static void main(String[] args)
    {
        //get the data from the api

        String allSeasonsUrl = "http://ergast.com/api/f1/seasons.json?limit=100&offset=0";
        JSONObject allSeasonsData = downLoadInfoString(allSeasonsUrl);

        Object[] allDriversInfo = totalDriversInfo(allSeasonsData);
        Object[] allConstructorsInfo = totalConstructorsInfo(allSeasonsData);
        Object[] allCircuitsInfo = totalCircuitsInfo(allSeasonsData);


/*

        System.out.println("Total drivers : " + ( (ArrayList) allDriversInfo[0]).size() );
        System.out.println("Total driver eras : " + ((HashMap) allDriversInfo[1]).size() );
        System.out.println("==========//==========");
        System.out.println("Total constructors : " + ( (ArrayList) allConstructorsInfo[0]).size() );
        System.out.println("Total constructor eras : " + ( (HashMap) allConstructorsInfo[1]).size() );
        System.out.println("==========//==========");
        System.out.println("Total circuits : " + ( (ArrayList) allCircuitsInfo[0]).size() );
        System.out.println("Total circuit eras : " + ( (HashMap) allCircuitsInfo[1]).size() );

*/

        //Create the fucking database already man

        Connection databaseConnection = null;

        try
        {
            Class.forName("org.sqlite.JDBC");
            String databasePath = "/home/giannis/Documents/AppDatabases/F1_STORY.db";
            databaseConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            //CREATE THE SEASONS TABLE
            System.out.println("CREATING SEASONS TABLE");
            //Statement seasonStatement = databaseConnection.createStatement();

            String dropAllSeasonsQuery = "DROP TABLE IF EXISTS ALL_SEASONS;";
            //seasonStatement.addBatch(dropAllSeasonsQuery);
            System.out.println(dropAllSeasonsQuery);

            String createAllSeasonsQuery = "CREATE TABLE IF NOT EXISTS ALL_SEASONS (SEASON_NAME VARCHAR NOT NULL, URL VARCHAR NOT NULL);";
            //seasonStatement.addBatch(createAllSeasonsQuery);
            System.out.println(createAllSeasonsQuery);

            StringBuilder addSeasonsQueryBuilder = new StringBuilder("INSERT INTO ALL_SEASONS VALUES ");

            for(Season season : allSeasons)
            {
                addSeasonsQueryBuilder.append("('" + season.getSeasonName() + "', " + "'" + season.getSeasonUrl() + "'),");
            }

            String addSeasonsQuery = addSeasonsQueryBuilder.substring(0, addSeasonsQueryBuilder.length() - 1) +";";

            //seasonStatement.addBatch(addSeasonsQuery);
            System.out.println(addSeasonsQuery);



            //CREATE THE DRIVERS TABLES
            //CREATE THE ALL_DRIVERS TABLE

            //Statement driversStatement = databaseConnection.createStatement();

            String dropAllDriversQuery = "DROP TABLE IF EXISTS ALL_DRIVERS;";
            //driversStatement.addBatch(dropAllDriversQuery);

            String createAllDriversQuery = "CREATE TABLE IF NOT EXISTS ALL_DRIVERS (DRIVER_ID VARCHAR PRIMARY KEY, DRIVER_NAME VARCHAR NOT NULL, DRIVER_SURNAME VARCHAR NOT NULL, URL VARCHAR NOT NULL);";
            System.out.println(createAllDriversQuery);
            //driversStatement.addBatch(createAllDriversQuery);

            ArrayList<Driver> allDrivers = (ArrayList<Driver>) allDriversInfo[0];

            StringBuilder insertToAllDriversBuilder = new StringBuilder("INSERT INTO ALL_DRIVERS VALUES ");

            for(Driver driver : allDrivers)
            {
                String name = driver.getDriverName();
                String surname = driver.getDriverSurname();
                name = name.replace("'", "''");
                surname = surname.replace("'", "''");
                insertToAllDriversBuilder.append("('" + driver.getDriverId() + "', '" + name + "', '" + surname + "', '" + driver.getDriverUrl() + "'),");
            }

            String insertToAllDriversQuery = insertToAllDriversBuilder.substring(0, insertToAllDriversBuilder.length() - 1) +";";
            System.out.println(insertToAllDriversQuery);

            //driversStatement.addBatch(insertToAllDriversQuery);

            //create the eras tables
            HashMap<String, HashMap<String, Driver>> driversEras = (HashMap<String, HashMap<String, Driver>>) allDriversInfo[1];

            for(String era : driversEras.keySet())
            {
                System.out.println("Creating " + era + "_DRIVERS table ...");
                String dropEraTableQuery = "DROP TABLE IF EXISTS '" + era + "_DRIVERS';";
                System.out.println(dropEraTableQuery);
                //driversStatement.addBatch(dropEraTableQuery);

                String createEraTableQuery = "CREATE TABLE IF NOT EXISTS '" + era + "_DRIVERS' (DRIVER_ID VARCHAR PRIMARY KEY, DRIVER_NAME VARCHAR NOT NULL, DRIVER_SURNAME VARCHAR NOT NULL);";
                System.out.println(createEraTableQuery);
                //driversStatement.addBatch(createEraTableQuery);

                StringBuilder insertIntoDriversEraBuilder = new StringBuilder("INSERT INTO '" + era + "_DRIVERS' VALUES ");

                HashMap<String, Driver> eraDriversMap = driversEras.get(era);

                for(String key : eraDriversMap.keySet())
                {
                    Driver eraDriver = eraDriversMap.get(key);
                    String eraName = eraDriver.getDriverName();
                    String eraSurname = eraDriver.getDriverSurname();
                    eraName = eraName.replace("'", "''");
                    eraSurname = eraSurname.replace("'", "''");
                    insertIntoDriversEraBuilder.append("('" + eraDriver.getDriverId() + "', '" + eraName +  "', '" + eraSurname + "'),");
                }

                String insertToEraTableQuery = insertIntoDriversEraBuilder.substring(0, insertIntoDriversEraBuilder.length() -1) + ";";

                System.out.println(insertToEraTableQuery);
                //driversStatement.addBatch(insertToEraTableQuery);
            }





            //CREATE THE CONSTRUCTORS TABLES
            //CREATE THE ALL_CONSTRUCTORS TABLE

            //Statement constructorsStatement = databaseConnection.createStatement();

            String dropAllConstructorsQuery = "DROP TABLE IF EXISTS ALL_CONSTRUCTORS;";
            //System.out.println(dropAllConstructorsQuery);
            //constructorsStatement.addBatch(dropAllConstructorsQuery);

            String createAllConstructorsQuery = "CREATE TABLE IF NOT EXISTS ALL_CONSTRUCTORS (CONSTRUCTOR_ID VARCHAR PRIMARY KEY, CONSTRUCTOR_SURNAME VARCHAR NOT NULL, URL VARCHAR NOT NULL);";
            System.out.println(createAllConstructorsQuery);
            //constructorsStatement.addBatch(createAllConstructorsQuery);

            ArrayList<Constructor> allConstructors = (ArrayList<Constructor>) allConstructorsInfo[0];

            StringBuilder insertToAllConstructorsBuilder = new StringBuilder("INSERT INTO ALL_CONSTRUCTORS VALUES ");

            for(Constructor constructor : allConstructors)
            {
                String fullConstructorName = constructor.getConstructorName();
                fullConstructorName = fullConstructorName.replace("'", "''");
                insertToAllConstructorsBuilder.append("('" + constructor.getConstructorId() + "', '" + fullConstructorName + "', '" + constructor.getConstructorUrl() + "'),");
            }

            String insertToAllConstructorsQuery = insertToAllConstructorsBuilder.substring(0, insertToAllConstructorsBuilder.length() - 1) +";";
            System.out.println(insertToAllConstructorsQuery);

            //constructorsStatement.addBatch(insertToAllConstructorsQuery);

            //create the eras tables
            HashMap<String, HashMap<String, Constructor>> constructorsEras = (HashMap<String, HashMap<String, Constructor>>) allConstructorsInfo[1];

            for(String era : constructorsEras.keySet())
            {
                System.out.println("Creating " + era + "_CONSTRUCTORS table ...");
                String dropConstructorEraTableQuery = "DROP TABLE IF EXISTS '" + era + "_CONSTRUCTORS';";
                //constructorsStatement.addBatch(dropConstructorEraTableQuery);

                String createConstructorEraTableQuery = "CREATE TABLE IF NOT EXISTS '" + era + "_CONSTRUCTORS' (CONSTRUCTOR_ID VARCHAR PRIMARY KEY, CONSTRUCTOR_SURNAME VARCHAR NOT NULL);";
                System.out.println(createConstructorEraTableQuery);
                //constructorsStatement.addBatch(createConstructorEraTableQuery);

                StringBuilder insertIntoConstructorsEraBuilder = new StringBuilder("INSERT INTO '" + era + "_CONSTRUCTORS' VALUES ");

                HashMap<String, Constructor> eraConstructorsMap = constructorsEras.get(era);

                for(String key : eraConstructorsMap.keySet())
                {
                    Constructor eraConstructor = eraConstructorsMap.get(key);
                    String eraConstructorFullName = eraConstructor.getConstructorName();
                    eraConstructorFullName = eraConstructorFullName.replace("'", "''");
                    insertIntoConstructorsEraBuilder.append("('" + eraConstructor.getConstructorId() + "', '" + eraConstructorFullName + "'),");
                }

                String insertToConstructorsEraTableQuery = insertIntoConstructorsEraBuilder.substring(0, insertIntoConstructorsEraBuilder.length() -1) + ";";
                System.out.println(insertToConstructorsEraTableQuery);
                //constructorsStatement.addBatch(insertToConstructorsEraTableQuery);
            }




            //CREATE THE CIRCUITS TABLES
            //CREATE THE ALL_CIRCUITS TABLE

            //Statement circuitsStatement = databaseConnection.createStatement();

            String dropAllCircuitsQuery = "DROP TABLE IF EXISTS ALL_CIRCUITS;";
            //System.out.println(dropAllCircuitsQuery);
            //circuitsStatement.addBatch(dropAllCircuitsQuery);

            String createAllCircuitsQuery = "CREATE TABLE IF NOT EXISTS ALL_CIRCUITS (CIRCUIT_ID VARCHAR PRIMARY KEY, CIRCUIT_SURNAME VARCHAR NOT NULL, URL VARCHAR NOT NULL);";
            System.out.println(createAllCircuitsQuery);
            //circuitsStatement.addBatch(createAllCircuitsQuery);

            ArrayList<Circuit> allCircuits = (ArrayList<Circuit>) allCircuitsInfo[0];

            StringBuilder insertToAllCircuitsBuilder = new StringBuilder("INSERT INTO ALL_CIRCUITS VALUES ");

            for(Circuit circuit : allCircuits)
            {
                String fullCircuitName = circuit.getCircuitName();
                fullCircuitName = fullCircuitName.replace("'", "''");
                insertToAllCircuitsBuilder.append("('" + circuit.getCircuitId() + "', '" + fullCircuitName + "', '" + circuit.getCircuitUrl() + "'),");
            }

            String insertToAllCircuitsQuery = insertToAllCircuitsBuilder.substring(0, insertToAllCircuitsBuilder.length() - 1) +";";
            System.out.println(insertToAllCircuitsQuery);

            //circuitsStatement.addBatch(insertToAllCircuitsQuery);

            //create the eras tables
            HashMap<String, HashMap<String, Circuit>> circuitEras = (HashMap<String, HashMap<String, Circuit>>) allCircuitsInfo[1];

            for(String era : circuitEras.keySet())
            {
                System.out.println("Creating " + era + "_CIRCUITS table ...");
                String dropCircuitEraTableQuery = "DROP TABLE IF EXISTS '" + era + "_CIRCUITS';";
                //circuitsStatement.addBatch(dropCircuitEraTableQuery);

                String createCircuitEraTableQuery = "CREATE TABLE IF NOT EXISTS '" + era + "_CIRCUITS' (CIRCUIT_ID VARCHAR PRIMARY KEY, CIRCUIT_SURNAME VARCHAR NOT NULL);";
                System.out.println(createCircuitEraTableQuery);
                //circuitsStatement.addBatch(createCircuitEraTableQuery);

                StringBuilder insertIntoCircuitsEraBuilder = new StringBuilder("INSERT INTO '" + era + "_CIRCUITS' VALUES ");

                HashMap<String, Circuit> eraCircuitsMap = circuitEras.get(era);

                for(String key : eraCircuitsMap.keySet())
                {
                    Circuit eraCircuit = eraCircuitsMap.get(key);
                    String eraCircuitFullName = eraCircuit.getCircuitName();
                    eraCircuitFullName = eraCircuitFullName.replace("'", "''");
                    insertIntoCircuitsEraBuilder.append("('" + eraCircuit.getCircuitId() + "', '" + eraCircuitFullName + "'),");
                }

                String insertToCircuitsEraTableQuery = insertIntoCircuitsEraBuilder.substring(0, insertIntoCircuitsEraBuilder.length() -1) + ";";
                System.out.println(insertToCircuitsEraTableQuery);
                //circuitsStatement.addBatch(insertToCircuitsEraTableQuery);
            }



            //EXECUTE the batches

            //seasonStatement.executeBatch();
            //System.out.println("Executed seasons batch");

            //driversStatement.executeBatch();
            //System.out.println("Executed drivers batch");

            //constructorsStatement.executeBatch();
            //System.out.println("Executed constructors batch");

            //circuitsStatement.executeBatch();
            //System.out.println("Executed circuits batch");

            System.out.println("ALL DONE!!!");



        }
        catch (ClassNotFoundException cnf)
        {
            System.out.println(cnf.getMessage());
        }
        catch (SQLException sqe)
        {
            System.out.println(sqe.getMessage());
        }
        finally
        {
            try
            {
                databaseConnection.close();
            }
            catch (SQLException sq)
            {
                System.out.println(sq.getMessage());
            }
        }


    }//main






    /**
     * Classifies the data to eras and total entries
     * @param  allSeasonsData the seasons JSON object to get info from
     * @return an Object[] containing an ArrayList<Data> and a HashMap<eraString, HashMap<nameString,Boolean>>
     */
    private static Object[] totalDriversInfo(JSONObject allSeasonsData)
    {
        Object[] result = null;

        System.out.println("Processing drivers");

        try
        {
            JSONObject mrData = allSeasonsData.getJSONObject("MRData");
            JSONObject seasonsTable = mrData.getJSONObject("SeasonTable");

            JSONArray allSeasonsArray = seasonsTable.getJSONArray("Seasons");

            // {alonso:Driver object for Alonso, hamilton:Driver object for Hamilton ... etc for all drivers}
            HashMap<String, Driver> allDriversMap = new HashMap<>();

            //{1980s:{senna:Driver(senna, Ayrton, Senna, url), mansell:true ...}, 1970s:{stewart:true, cevert:true ...} ...}
            HashMap<String, HashMap<String, Driver>> erasMap = new HashMap<>();


            //all driverInstances will fit here
            ArrayList<Driver> allData = new ArrayList<>();


            for(int index=0; index < allSeasonsArray.length(); index ++)
            {
                JSONObject seasonObject = allSeasonsArray.getJSONObject(index);

                String season = seasonObject.getString("season");
                String seasonUrl = seasonObject.getString("url");

                allSeasons.add(new Season(season, seasonUrl));

                //System.out.println("Processing " + season + " season...");

                String era = getEra(season, 0);

                //store the era
                if(erasMap.get(era) == null)
                {
                    //System.out.println("Created " + era + " era");
                    erasMap.put(era, new HashMap<String, Driver>());
                }

                String seasonDrivers = "http://ergast.com/api/f1/" + season + "/drivers.json?limit=1000&offset=0";

                JSONObject allSeasonDriversJSON = downLoadInfoString(seasonDrivers);

                JSONObject driverMRData = allSeasonDriversJSON.getJSONObject("MRData");
                JSONObject driversTable = driverMRData.getJSONObject("DriverTable");

                JSONArray allDriversArray = driversTable.getJSONArray("Drivers");

                for(int driverIndex=0; driverIndex<allDriversArray.length(); driverIndex++)
                {

                    JSONObject driverObject = allDriversArray.getJSONObject(driverIndex);

                    String id = driverObject.getString("driverId");
                    String name = driverObject.getString("givenName");
                    String surname = driverObject.getString("familyName");
                    String url = driverObject.getString("url");

                    if(allDriversMap.get(id) == null) //driver never seen b4
                    {
                        Driver driver = new Driver(id, name, surname, url);

                        allDriversMap.put(id, driver);
                        allData.add(driver);
                    }

                    if(erasMap.get(era).get(id) == null) //driver not inside this era yet
                    {
                        Driver eraDriver = allDriversMap.get(id);
                        erasMap.get(era).put(id, eraDriver);
                        //System.out.println("Added " + id + " in " + era + " era");
                    }


                }

            }

            result = new Object[]{allData, erasMap};
        }
        catch (JSONException je)
        {
            System.out.println(je.getMessage());
        }

        //System.out.println( ((ArrayList) result[0]).size());
        //System.out.println( ((HashMap) result[1]).size() );

        return result;
    }



    /**
     * Classifies the data to eras and total entries
     * @param  allSeasonsData the seasons JSON object to get info from
     * @return an Object[] containing an ArrayList<Data> and a HashMap<eraString, HashMap<nameString,Boolean>>
     */
    private static Object[] totalConstructorsInfo(JSONObject allSeasonsData)
    {
        Object[] result = null;

        System.out.println("Processing constructors");

        try
        {
            JSONObject mrData = allSeasonsData.getJSONObject("MRData");
            JSONObject seasonsTable = mrData.getJSONObject("SeasonTable");

            JSONArray allSeasonsArray = seasonsTable.getJSONArray("Seasons");

            // {alonso:Driver object for Alonso, hamilton:Driver object for Hamilton ... etc for all drivers}
            HashMap<String, Constructor> allConstructorsMap = new HashMap<>();

            //{1980s:{senna:true, mansell:true ...}, 1970s:{stewart:true, cevert:true ...} ...}
            HashMap<String, HashMap<String, Constructor>> erasMap = new HashMap<>();

            //all driverInstances will fit here
            ArrayList<Constructor> allData = new ArrayList<>();
            Constructor eagleConstructor = new Constructor("Eagle", "eagle", "http://en.wikipedia.org/wiki/Anglo_American_Racers");
            allData.add(eagleConstructor);


            for(int index=0; index < allSeasonsArray.length(); index ++)
            {
                JSONObject seasonObject = allSeasonsArray.getJSONObject(index);

                String season = seasonObject.getString("season");

                //System.out.println("Processing " + season + " season...");

                String era = getEra(season, 0);

                //store the era
                if(erasMap.get(era) == null)
                {
                    //System.out.println("Created " + era + " era");
                    erasMap.put(era, new HashMap<String, Constructor>());
                }

                String seasonConstructors = "http://ergast.com/api/f1/" + season + "/constructors.json?limit=1000&offset=0";

                JSONObject allSeasonDriversJSON = downLoadInfoString(seasonConstructors);

                JSONObject constructorMRData = allSeasonDriversJSON.getJSONObject("MRData");
                JSONObject constructorsTable = constructorMRData.getJSONObject("ConstructorTable");

                JSONArray allDriversArray = constructorsTable.getJSONArray("Constructors");

                for(int constructorIndex=0; constructorIndex<allDriversArray.length(); constructorIndex++)
                {

                    JSONObject constructorObject = allDriversArray.getJSONObject(constructorIndex);

                    String id = constructorObject.getString("constructorId");
                    String name = constructorObject.getString("name");
                    String url = constructorObject.getString("url");

                    if(allConstructorsMap.get(id) == null) //driver never seen b4
                    {
                        Constructor constructor = new Constructor(name, id, url);

                        allConstructorsMap.put(id, constructor);
                        allData.add(constructor);
                    }

                    if(erasMap.get(era).get(id) == null) //driver not inside this era yet
                    {
                        Constructor eraConstructor = allConstructorsMap.get(id);
                        erasMap.get(era).put(id, eraConstructor);
                        //System.out.println("Added " + id + " in " + era + " era");
                    }


                }

            }

            result = new Object[]{allData, erasMap, allConstructorsMap};
        }
        catch (JSONException je)
        {
            System.out.println(je.getMessage());
        }

        //System.out.println( ((ArrayList) result[0]).size());
        //System.out.println( ((HashMap) result[1]).size() );
        
        return result;
    }




    /**
     * Classifies the data to eras and total entries
     * @param  allSeasonsData the seasons JSON object to get info from
     * @return an Object[] containing an ArrayList<Data> and a HashMap<eraString, HashMap<nameString,Boolean>>
     */
    private static Object[] totalCircuitsInfo(JSONObject allSeasonsData)
    {
        Object[] result = null;

        System.out.println("Processing circuits");

        try
        {
            JSONObject mrData = allSeasonsData.getJSONObject("MRData");
            JSONObject seasonsTable = mrData.getJSONObject("SeasonTable");

            JSONArray allSeasonsArray = seasonsTable.getJSONArray("Seasons");

            // {alonso:Driver object for Alonso, hamilton:Driver object for Hamilton ... etc for all drivers}
            HashMap<String, Circuit> allCircuitsMap = new HashMap<>();

            //{1980s:{senna:true, mansell:true ...}, 1970s:{stewart:true, cevert:true ...} ...}
            HashMap<String, HashMap<String, Circuit>> erasMap = new HashMap<>();

            //all driverInstances will fit here
            ArrayList<Circuit> allData = new ArrayList<>();
            Circuit imperial = new Circuit("Port Imperial Street Circuit", "port_imperial", "http://en.wikipedia.org/wiki/Port_Imperial_Street_Circuit");
            allData.add(imperial);

            for(int index=0; index < allSeasonsArray.length(); index ++)
            {
                JSONObject seasonObject = allSeasonsArray.getJSONObject(index);

                String season = seasonObject.getString("season");

                //System.out.println("Processing " + season + " season...");

                String era = getEra(season, 0);

                //store the era
                if(erasMap.get(era) == null)
                {
                    //System.out.println("Created " + era + " era");
                    erasMap.put(era, new HashMap<String, Circuit>());
                }

                String seasonCircuits = "http://ergast.com/api/f1/" + season + "/circuits.json?limit=1000&offset=0";

                JSONObject allSeasonDriversJSON = downLoadInfoString(seasonCircuits);

                JSONObject constructorMRData = allSeasonDriversJSON.getJSONObject("MRData");
                JSONObject constructorsTable = constructorMRData.getJSONObject("CircuitTable");

                JSONArray allDriversArray = constructorsTable.getJSONArray("Circuits");

                for(int circuitIndex=0; circuitIndex<allDriversArray.length(); circuitIndex++)
                {

                    JSONObject circuitObject = allDriversArray.getJSONObject(circuitIndex);

                    String id = circuitObject.getString("circuitId");
                    String name = circuitObject.getString("circuitName");
                    String url = circuitObject.getString("url");

                    if(allCircuitsMap.get(id) == null) //driver never seen b4
                    {
                        Circuit circuit = new Circuit(name, id, url);

                        allCircuitsMap.put(id, circuit);
                        allData.add(circuit);
                    }

                    if(erasMap.get(era).get(id) == null) //driver not inside this era yet
                    {
                        Circuit eraCircuit = allCircuitsMap.get(id);
                        erasMap.get(era).put(id, eraCircuit);
                        //System.out.println("Added " + id + " in " + era + " era");
                    }


                }

            }

            result = new Object[]{allData, erasMap, allCircuitsMap};
        }
        catch (JSONException je)
        {
            System.out.println(je.getMessage());
        }

        //System.out.println( ((ArrayList) result[0]).size());
        //System.out.println( ((HashMap) result[1]).size() );

        return result;
    }






    private static String getEra(String season, int index)
    {
        if(index == 3)
            return "0s";
        else
            return season.charAt(index) + getEra(season, ++ index);
    }






    private static JSONObject downLoadInfoString(String infoUrl)
    {

        JSONObject result = null;

        BufferedReader reader = null;
        HttpURLConnection con = null;

        try
        {
            StringBuilder builder = new StringBuilder();

            URL url = new URL(infoUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while ( (line = reader.readLine()) != null )
            {
                builder.append(line);
            }

            result = new JSONObject(builder.toString());

        }
        catch (MalformedURLException mue)
        {
            System.out.println(mue.getMessage());
        }
        catch (IOException io)
        {
            System.out.println(io.getMessage());
        }
        catch (JSONException je)
        {
            System.out.println(je.getMessage());
        }
        finally
        {
            try
            {
                reader.close();
                con.disconnect();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }

        //System.out.println(result.toString());

        return result;
    }



}
