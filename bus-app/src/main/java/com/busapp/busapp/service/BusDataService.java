package com.busapp.busapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.busapp.busapp.GeoJSON.Feature;
import com.busapp.busapp.GeoJSON.FeatureCollection;
import com.busapp.busapp.GeoJSON.Geometry;
import com.busapp.busapp.GeoJSON.Properties;
import com.busapp.busapp.entity.CleanBusData;
import com.busapp.busapp.entity.RawBusData;
import com.busapp.busapp.repository.CleanBusDataRepository;
import com.busapp.busapp.repository.RawBusDataRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BusDataService {

    @Autowired
    private RawBusDataRepository rawBusDataRepository;

    @Autowired
    private CleanBusDataRepository cleanBusDataRepository;

    // Inject the database connection properties from application.properties
    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void insertRawData(MultipartFile file) throws IOException, SQLException, CsvException {
        int rowNumber = 0;
        
        // Database connection and table creation
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {
            
            // SQL statement to truncate the tables if they exist
            String truncateRawTable = "TRUNCATE TABLE raw_bus_data";
            String truncateCleanTable = "TRUNCATE TABLE clean_bus_data";
            
            // Execute the SQL statements
            statement.executeUpdate(truncateRawTable);
            statement.executeUpdate(truncateCleanTable);
            
            // Read and parse the CSV file
            try (Reader reader = new InputStreamReader(file.getInputStream());
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                List<String[]> rows = csvReader.readAll();
                List<RawBusData> csvDataList = new ArrayList<>();

                for (String[] row : rows) {
                    RawBusData csvData = new RawBusData();
                    
                    rowNumber++;
                    csvData.setRecordedAtTime(row[0]);
                    csvData.setDirectionRef(row[1]);
                    csvData.setPublishedLineName(row[2]);
                    csvData.setOriginName(row[3]);
                    csvData.setOriginLat(row[4]);
                    csvData.setOriginLong(row[5]);
                    csvData.setDestinationName(row[6]);
                    csvData.setDestinationLat(row[7]);
                    csvData.setDestinationLong(row[8]);
                    csvData.setVehicleRef(row[9]);
                    csvData.setVehicleLocationLatitude(row[10]);
                    csvData.setVehicleLocationLongitude(row[11]);
                    csvData.setNextStopPointName(row[12]);
                    if (row.length == 18) {
                        csvData.setUnknown(row[13]);
                        csvData.setArrivalProximityText(row[14]);
                        csvData.setDistanceFromStop(row[15]);
                        csvData.setExpectedArrivalTime(row[16]);
                        csvData.setScheduledArrivalTime(row[17]);
                    } else if (row.length < 17) {
                        System.out.println("Row " + rowNumber + " has LESS than 17 columns " + "(" + row.length + ")" + ".");
                    } else if (row.length > 17) {
                        System.out.println("Row " + rowNumber + " has MORE than 17 columns " + "(" + row.length + ")" + ".");
                    } else {
                        csvData.setArrivalProximityText(row[13]);
                        csvData.setDistanceFromStop(row[14]);
                        csvData.setExpectedArrivalTime(row[15]);
                        csvData.setScheduledArrivalTime(row[16]);
                    }
    
                    csvDataList.add(csvData);
                }

                // Save data to the database
                saveAllRawBusData(csvDataList);

                List<RawBusData> rawBusDataList = rawBusDataRepository.findAll();
                List<CleanBusData> cleanBusDataList = new ArrayList<>();
                
                rowNumber = 0;

                for (RawBusData rawBusData : rawBusDataList)
                {
                    rowNumber++;
                    CleanBusData cleanBusData = new CleanBusData();
                    
                    //RecordedAtTime
                    try {
                        // Convert the String date to Date object without milliseconds
                        String rawDateStr = rawBusData.getRecordedAtTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Adjust the format as needed
                        Date recordedAtTime = dateFormat.parse(rawDateStr);
                        cleanBusData.setRecordedAtTime(recordedAtTime);                        
                    } 
                    catch (ParseException e) {
                        // Handle the parsing exception, e.g., log an error
                        System.err.println("Error parsing date at row " + rowNumber + ": " + e.getMessage());
                    }
                    //DirectionRef
                    try {
                        // Assuming rawBusData.getDirectionRef() returns a valid integer represented as a String
                        String directionRefStr = rawBusData.getDirectionRef();
                        int directionRef = Integer.parseInt(directionRefStr);
                        if(directionRef!=0 && directionRef!=1){
                            System.out.println("DirectionRef is not 0 OR 1 at Row: "+rowNumber);
                        }
                        cleanBusData.setDirectionRef(directionRef);
                    } 
                    catch (NumberFormatException e) {
                        
                        System.err.println("Error parsing directionRef: " + e.getMessage());
                        
                    }
                    //PublishedLineName
                    String publishedLineName = rawBusData.getPublishedLineName();
                    if (publishedLineName == null || publishedLineName.trim().isEmpty() || publishedLineName.equals("NA")) {
                        System.err.println("Error at row " + rowNumber + ": Invalid published line name");
                    } 
                    else {
                    cleanBusData.setPublishedLineName(publishedLineName);
                    }
                    //OriginName
                    String originName = rawBusData.getOriginName();
                    if (originName == null || originName.trim().isEmpty() || originName.equals("NA")) {
                        System.err.println("Error at row " + rowNumber + ": Invalid origin name");
                    } 
                    else {
                    cleanBusData.setOriginName(originName);
                    }     
                    //OriginLat  
                    String originLatStr = rawBusData.getOriginLat();

                    try {
                        double originLat = Double.parseDouble(originLatStr);

                        // Check if originLat is within the range [-90, 90]
                        if (originLat < -90 || originLat > 90) {
                            System.err.println("Error at row " + rowNumber + ": Origin latitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setOriginLat(originLat);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Invalid origin latitude format");
                    }
                    //OriginLong
                    String originLongStr = rawBusData.getOriginLong();

                    try {
                        double originLong = Double.parseDouble(originLongStr);

                        // Check if originLat is within the range [-90, 90]
                        if (originLong < -180 || originLong > 180) {
                            System.err.println("Error at row " + rowNumber + ": Origin longitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setOriginLong(originLong);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Invalid origin longitude format");
                    }
                    //DestinationName
                    String destinationName = rawBusData.getDestinationName();
                    if (destinationName == null || destinationName.trim().isEmpty() || destinationName.equals("NA")) {
                        System.err.println("Error at row " + rowNumber + ": Invalid destination name");
                    } 
                    else {
                    cleanBusData.setDestinationName(destinationName);
                    }    
                    //DestinationLat  
                    String destinationLatStr = rawBusData.getDestinationLat();

                    try {
                        double destinationLat = Double.parseDouble(destinationLatStr);

                        // Check if destinationLat is within the range [-90, 90]
                        if (destinationLat < -90 || destinationLat > 90) {
                            System.err.println("Error at row " + rowNumber + ": Destination latitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setDestinationLat(destinationLat);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Invalid destination latitude format");
                    }
                    //DestinationLong
                    String destinationLongStr = rawBusData.getDestinationLong();

                    try {
                        double destinationLong = Double.parseDouble(destinationLongStr);

                        // Check if destinationLong is within the range [-90, 90]
                        if (destinationLong < -180 || destinationLong > 180) {
                            System.err.println("Error at row " + rowNumber + ": Destination longitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setDestinationLong(destinationLong);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Invalid destination longitude format");
                    } 
                    cleanBusDataList.add(cleanBusData);
                     
                    //VehicleRef
                    String vehicleRef = rawBusData.getVehicleRef();
                    if (vehicleRef == null || vehicleRef.trim().isEmpty() || vehicleRef.equals("NA")) {
                        System.err.println("Error at row " + rowNumber + ": Invalid vehicle reference");
                    } 
                    else {
                    cleanBusData.setVehicleRef(vehicleRef);
                    }
                    //VehicleLocationLatitude  
                    String vehicleLocationLatitudeStr = rawBusData.getVehicleLocationLatitude();

                    try {
                        double vehicleLocationLatitude = Double.parseDouble(vehicleLocationLatitudeStr);

                        // Check if VehicleLocationLatitude is within the range [-90, 90]
                        if (vehicleLocationLatitude < -90 || vehicleLocationLatitude > 90) {
                            System.err.println("Error at row " + rowNumber + ": Vehicle location latitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setVehicleLocationLatitude(vehicleLocationLatitude);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Invalid vehicle location latitude format");
                    }
                    //VehicleLocationLongitude
                    String vehicleLocationLongitudeStr = rawBusData.getVehicleLocationLongitude();

                    try {
                        double vehicleLocationLongitude = Double.parseDouble(vehicleLocationLongitudeStr);

                        // Check if VehicleLocationLongitude is within the range [-90, 90]
                        if (vehicleLocationLongitude < -180 || vehicleLocationLongitude > 180) {
                            System.err.println("Error at row " + rowNumber + ": Vehicle location longitude out of range");
                        }
                        //Decided not to set all to fixed amt of DPs
                        cleanBusData.setVehicleLocationLongitude(vehicleLocationLongitude);
                    } catch (NumberFormatException e) {
                        System.err.println("Error at row " + rowNumber + ": Vehicle location longitude format");
                    }
                    //NextStopPointName
                    String nextStopPointName = rawBusData.getNextStopPointName();

                    if (nextStopPointName == null || nextStopPointName.trim().isEmpty() || nextStopPointName.equals("NA") ||nextStopPointName.equals("NULL")) {
                        cleanBusData.setNextStopPointName(null);
                    } else {
                        cleanBusData.setNextStopPointName(nextStopPointName);
                    }

                    if (rawBusData.getUnknown() != null) {
                        String unknownStr = rawBusData.getUnknown();
                        String newNextStopPointName = nextStopPointName + " " + unknownStr;
                        cleanBusData.setNextStopPointName(newNextStopPointName);
                    }
                    //ArrivalProximityText
                    String arrivalProximityText = rawBusData.getArrivalProximityText();
                    if (arrivalProximityText == null || arrivalProximityText.trim().isEmpty() || arrivalProximityText.equals("NA")) {
                        cleanBusData.setArrivalProximityText(null);
                    } 
                    else {
                    cleanBusData.setArrivalProximityText(arrivalProximityText);
                    }
                    // DistanceFromStop
                    try {
                        String distanceFromStopStr = rawBusData.getDistanceFromStop();
                        int distanceFromStop;

                        if ("NA".equals(distanceFromStopStr)) {
                            distanceFromStop = 0;
                        } else {
                            distanceFromStop = Integer.parseInt(distanceFromStopStr);

                            if (distanceFromStop < 0) {
                                System.err.println("Distance from stop is less than 0 at Row: " + rowNumber);
                                
                            }
                        }

                        cleanBusData.setDistanceFromStop(distanceFromStop);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing DistanceFromStop: " + e.getMessage());
                        
                    }
                    //ExpectedArrivalTime
                    try {
                        
                        String expectedArrivalTimeStr = rawBusData.getExpectedArrivalTime();
                        if ("NA".equals(expectedArrivalTimeStr)) {
                            cleanBusData.setExpectedArrivalTime(null);                        
                        }   
                        else{
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Adjust the format as needed
                            Date expectedArrivalTime = dateFormat.parse(expectedArrivalTimeStr);
                            cleanBusData.setExpectedArrivalTime(expectedArrivalTime);                        
                        } 
                        
                    } 
                    catch (ParseException e) {
                        // Handle the parsing exception, e.g., log an error
                        System.err.println("Error parsing date at row " + rowNumber + ": " + e.getMessage());
                    }
                    // ScheduledArrivalTime
                    try {
                        String scheduledArrivalTimeStr = rawBusData.getScheduledArrivalTime();

                        if ("NA".equals(scheduledArrivalTimeStr)) {
                            cleanBusData.setScheduledArrivalTime(null); // Use null to represent "NA"
                        } else {
                            // Split the time string into hours, minutes, and seconds
                            String[] timeParts = scheduledArrivalTimeStr.split(":");
                            
                            int hours = Integer.parseInt(timeParts[0]);
                            int minutes = Integer.parseInt(timeParts[1]);
                            int seconds = Integer.parseInt(timeParts[2]);

                            // Check if the hour is greater than or equal to 24
                            if (hours >= 24) {
                                // Subtract 24 from the hour to bring it within the valid range
                                hours -= 24;
                            }

                            // Create a LocalTime object with the corrected values
                            LocalTime scheduledArrivalTime = LocalTime.of(hours, minutes, seconds);

                            cleanBusData.setScheduledArrivalTime(scheduledArrivalTime);
                        }
                    } catch (Exception e) {
                        // Handle the parsing exception, e.g., log an error
                        System.err.println("Error parsing time at row " + rowNumber + ": " + e.getMessage());
                    }
                    cleanBusDataList.add(cleanBusData);    
                }
                 // Save data to the clean_bus_data table
                saveAllCleanBusData(cleanBusDataList);
            }
        }
    }
    public List<String> getVehicleReferences()throws IOException, SQLException{
       try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()){

        }
        List<String> distinctVehicleReferences = cleanBusDataRepository.findDistinctVehicleRef();
        
        
        return(distinctVehicleReferences);
        
    }

    public List<String> getPublishedLineNames()throws IOException, SQLException{
       try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()){

        }
        List<String> distinctPublishedLineNames = cleanBusDataRepository.findDistinctPublishedLineName();
        
        
        return(distinctPublishedLineNames);
        
    }

    public String vehRef(String vehicleReference) throws IOException, SQLException{
    try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
         Statement statement = connection.createStatement()) {

        //Fetch Data from Repo Query
        List<CleanBusData> allRows = cleanBusDataRepository.findByVehicleRef(vehicleReference);

        // Create a new empty list for sorted data
        List<CleanBusData> sortedByOriginNameList = allRows.stream()
            .sorted(Comparator.comparing(CleanBusData::getOriginName))
            .collect(Collectors.toList());

        // Create a list to store the groups of rows
        List<List<CleanBusData>> groupAsFeatureList = new ArrayList<>();

        // Initialize variables to keep track of the current group
        List<CleanBusData> currentGroup = new ArrayList<>();
        CleanBusData previousRow = null;

        for (CleanBusData row : sortedByOriginNameList) {
            if (previousRow == null) {
                // For the first row, initialize the previousRow and add the row to the current group
                previousRow = row;
                currentGroup.add(row);
            } else if (row.getOriginName().equals(previousRow.getOriginName())
                    && row.getDestinationName().equals(previousRow.getDestinationName())
                    && isWithin8To12Minutes(previousRow.getRecordedAtTime(), row.getRecordedAtTime())) {
                // Add the current row to the current group
                currentGroup.add(row);
                previousRow = row; // Update previousRow
            } else {
                // Start a new group
                groupAsFeatureList.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(row);
                previousRow = row; // Update previousRow
            }
        }

        // Add the last group to the list
        groupAsFeatureList.add(currentGroup);

        List<Feature> featureList = new ArrayList<>();

        for (List<CleanBusData> group : groupAsFeatureList) {
            Feature feature = new Feature();
            feature.setType("Feature"); // Set the type of the Feature object
        
            // Create and set the Geometry object
            Geometry geometry = new Geometry();
            if (isPointGeometry(group)) {
                geometry.setType("Point"); // Set the type to "Point" if it's a point geometry
                List<Double> coordinates = new ArrayList<>();
        
                for (CleanBusData cleanBusData : group) {
                    
                    coordinates.add(cleanBusData.getVehicleLocationLongitude()); // Replace with the actual method to get longitude
                    coordinates.add(cleanBusData.getVehicleLocationLatitude()); // Replace with the actual method to get latitude
                    geometry.setCoordinates(coordinates);
                }
        
                
                feature.setGeometry(geometry);
        
            // Create and set the Properties object
            Properties properties = new Properties();
            // Set properties as needed
            properties.setVehicleRef(group.get(0).getVehicleRef());
            properties.setPublishedLineName(group.get(0).getPublishedLineName());
            properties.setOriginName(group.get(0).getOriginName());
            properties.setDestinationName(group.get(0).getDestinationName());
            properties.setDirectionRef(Integer.toString(group.get(0).getDirectionRef()));
            properties.setStartTime(group.get(0).getRecordedAtTime().toString());
            if(group.get(group.size() - 1).getExpectedArrivalTime()==null){
                properties.setEndTime("NA");
            }
            else{
                properties.setEndTime(group.get(group.size() - 1).getExpectedArrivalTime().toString());
            }
            
            // Calculate the number of points
            int numOfPoints = coordinates.size();
            properties.setNumOfPoints(Integer.toString(numOfPoints));

            // Create a map to store point properties
            Map<String, Object> pointProperties = new LinkedHashMap<>();

                
            String pointKey = "Point " + (1);
                    
            pointProperties.put(pointKey + " geom", "MyLatLong(longitude=" + coordinates.get(0) + ", latitude=" + coordinates.get(1) + ")");
            pointProperties.put(pointKey + " arrival", group.get(0).getArrivalProximityText());
            pointProperties.put(pointKey + " dist from stop", Integer.toString(group.get(0).getDistanceFromStop()));

            if (group.get(0).getExpectedArrivalTime() != null) {
                String expectedArrivalTimeStr = group.get(0).getExpectedArrivalTime().toString();
                pointProperties.put(pointKey + " time", expectedArrivalTimeStr);
            } else {
                pointProperties.put(pointKey + " time", "NA");
            }
                

                // Add the map of point properties to the Properties class
                properties.setPoint(pointProperties);
                        
                // Add other properties as needed
                feature.setProperties(properties);

                featureList.add(feature);    
            } else {
                geometry.setType("LineString"); // Set the type to "LineString" if it's not a point geometry
                List<List<Double> > coordinates = new ArrayList<>();
        
                for (CleanBusData cleanBusData : group) {
                    List<Double> coordinatePair = new ArrayList<>();
                    coordinatePair.add(cleanBusData.getVehicleLocationLongitude()); // Replace with the actual method to get longitude
                    coordinatePair.add(cleanBusData.getVehicleLocationLatitude()); // Replace with the actual method to get latitude
                    coordinates.add(coordinatePair);
                    
                }
        
                geometry.setCoordinates(coordinates);
        
            feature.setGeometry(geometry);
        
            // Create and set the Properties object
            Properties properties = new Properties();
            // Set properties as needed
            properties.setVehicleRef(group.get(0).getVehicleRef());
            properties.setPublishedLineName(group.get(0).getPublishedLineName());
            properties.setOriginName(group.get(0).getOriginName());
            properties.setDestinationName(group.get(0).getDestinationName());
            properties.setDirectionRef(Integer.toString(group.get(0).getDirectionRef()));
            properties.setStartTime(group.get(0).getRecordedAtTime().toString());
            if(group.get(group.size() - 1).getExpectedArrivalTime()==null){
                properties.setEndTime("NA");
            }
            else{
                properties.setEndTime(group.get(group.size() - 1).getExpectedArrivalTime().toString());
            }
            
            // Calculate the number of points
            int numOfPoints = coordinates.size();
            properties.setNumOfPoints(Integer.toString(numOfPoints));

            // Create a map to store point properties
            Map<String, Object> pointProperties = new LinkedHashMap<>();

                for (int i = 0; i < numOfPoints; i++) {
                    String pointKey = "Point " + (i + 1);
                    
                    pointProperties.put(pointKey + " geom", "MyLatLong(longitude=" + coordinates.get(i).get(0) + ", latitude=" + coordinates.get(i).get(1) + ")");
                    pointProperties.put(pointKey + " arrival", group.get(i).getArrivalProximityText());
                    pointProperties.put(pointKey + " dist from stop", Integer.toString(group.get(i).getDistanceFromStop()));

                    if (group.get(i).getExpectedArrivalTime() != null) {
                        String expectedArrivalTimeStr = group.get(i).getExpectedArrivalTime().toString();
                        pointProperties.put(pointKey + " time", expectedArrivalTimeStr);
                    } else {
                        pointProperties.put(pointKey + " time", "NA");
                    }
                }

                // Add the map of point properties to the Properties class
                properties.setPoint(pointProperties);
                        
                // Add other properties as needed
                feature.setProperties(properties);

                featureList.add(feature);    
                                 
            }
        }

        //Create Feature Collection
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setType("FeatureCollection");
        featureCollection.setFeatures(featureList);

        //Serialize the GeoJSONFeatureCollection to JSON using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String geoJSONString = objectMapper.writeValueAsString(featureCollection);

        //Display GeoJSON
        return geoJSONString;
    }
}
    
    public String pubLine(String publishedLine) throws IOException, SQLException{
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
         Statement statement = connection.createStatement()) {
        
        // Fetch Data from Repo Query
        List<CleanBusData> allRows = cleanBusDataRepository.findByPublishedLine(publishedLine);

        // Sort the list by vehicle reference, origin name, and recorded time
        List<CleanBusData> sortedByVehRefList = allRows.stream()
            .sorted(Comparator.comparing(CleanBusData::getVehicleRef)
                .thenComparing((a, b) -> a.getOriginName().compareTo(b.getOriginName()))
                .thenComparing(CleanBusData::getRecordedAtTime))
            .collect(Collectors.toList());

        // Create a list to store the groups of rows
        List<List<CleanBusData>> groupAsFeatureList = new ArrayList<>();

        // Initialize variables to keep track of the current group
        List<CleanBusData> currentGroup = new ArrayList<>();
        CleanBusData previousRow = null;
        
        for (CleanBusData row : sortedByVehRefList) {
            if (previousRow == null) {
                // For the first row, initialize the previousRow and add the row to the current group
                previousRow = row;
                currentGroup.add(row);
            } else if (row.getOriginName().equals(previousRow.getOriginName())
                    && row.getDestinationName().equals(previousRow.getDestinationName())
                    && isWithin8To12Minutes(previousRow.getRecordedAtTime(), row.getRecordedAtTime())) {
                // Add the current row to the current group
                currentGroup.add(row);
                previousRow = row; // Update previousRow
            } else {
                // Start a new group
                groupAsFeatureList.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(row);
                previousRow = row; // Update previousRow
            }
        }
        // Add the last group to the list
        groupAsFeatureList.add(currentGroup);

        List<Feature> featureList = new ArrayList<>();
        
        for (List<CleanBusData> group : groupAsFeatureList) {
            Feature feature = new Feature();
            feature.setType("Feature"); // Set the type of the Feature object
        
            // Create and set the Geometry object
            Geometry geometry = new Geometry();
            if (isPointGeometry(group)) {
                geometry.setType("Point"); // Set the type to "Point" if it's a point geometry
                List<Double> coordinates = new ArrayList<>();
        
                coordinates.add(group.get(0).getVehicleLocationLongitude()); // Replace with the actual method to get longitude
                coordinates.add(group.get(0).getVehicleLocationLatitude()); // Replace with the actual method to get latitude             
        
                geometry.setCoordinates(coordinates);
                feature.setGeometry(geometry);
        
            // Create and set the Properties object
            Properties properties = new Properties();
            // Set properties as needed
            properties.setVehicleRef(group.get(0).getVehicleRef());
            properties.setPublishedLineName(group.get(0).getPublishedLineName());
            properties.setOriginName(group.get(0).getOriginName());
            properties.setDestinationName(group.get(0).getDestinationName());
            properties.setDirectionRef(Integer.toString(group.get(0).getDirectionRef()));
            properties.setStartTime(group.get(0).getRecordedAtTime().toString());
            if(group.get(group.size() - 1).getExpectedArrivalTime()==null){
                properties.setEndTime("NA");
            }
            else{
                properties.setEndTime(group.get(group.size() - 1).getExpectedArrivalTime().toString());
            }
            
            // Calculate the number of points
            int numOfPoints = coordinates.size();
            properties.setNumOfPoints(Integer.toString(numOfPoints));

            // Create a map to store point properties
            Map<String, Object> pointProperties = new LinkedHashMap<>();

                
                    String pointKey = "Point " + (1);
                    
                    pointProperties.put(pointKey + " geom", "MyLatLong(longitude=" + coordinates.get(0) + ", latitude=" + coordinates.get(1) + ")");
                    pointProperties.put(pointKey + " arrival", group.get(0).getArrivalProximityText());
                    pointProperties.put(pointKey + " dist from stop", Integer.toString(group.get(0).getDistanceFromStop()));

                    if (group.get(0).getExpectedArrivalTime() != null) {
                        String expectedArrivalTimeStr = group.get(0).getExpectedArrivalTime().toString();
                        pointProperties.put(pointKey + " time", expectedArrivalTimeStr);
                    } else {
                        pointProperties.put(pointKey + " time", "NA");
                    }
                

                // Add the map of point properties to the Properties class
                properties.setPoint(pointProperties);
                        
                // Add other properties as needed
                feature.setProperties(properties);

                featureList.add(feature);    
            } else {
                geometry.setType("LineString"); // Set the type to "LineString" if it's not a point geometry
                List<List<Double> > coordinates = new ArrayList<>();
        
                for (CleanBusData cleanBusData : group) {
                    List<Double> coordinatePair = new ArrayList<>();
                    coordinatePair.add(cleanBusData.getVehicleLocationLongitude()); // Replace with the actual method to get longitude
                    coordinatePair.add(cleanBusData.getVehicleLocationLatitude()); // Replace with the actual method to get latitude
                    coordinates.add(coordinatePair);
                    geometry.setCoordinates(coordinates);
                    
                }
        
                
        
            feature.setGeometry(geometry);
        
                
        
            feature.setGeometry(geometry);
        
            // Create and set the Properties object
            Properties properties = new Properties();
            // Set properties as needed
            properties.setVehicleRef(group.get(0).getVehicleRef());
            properties.setPublishedLineName(group.get(0).getPublishedLineName());
            properties.setOriginName(group.get(0).getOriginName());
            properties.setDestinationName(group.get(0).getDestinationName());
            properties.setDirectionRef(Integer.toString(group.get(0).getDirectionRef()));
            properties.setStartTime(group.get(0).getRecordedAtTime().toString());
            if(group.get(group.size() - 1).getExpectedArrivalTime()==null){
                properties.setEndTime("NA");
            }
            else{
                properties.setEndTime(group.get(group.size() - 1).getExpectedArrivalTime().toString());
            }
            
            // Calculate the number of points
            int numOfPoints = coordinates.size();
            properties.setNumOfPoints(Integer.toString(numOfPoints));

            // Create a map to store point properties
            Map<String, Object> pointProperties = new LinkedHashMap<>();

                for (int i = 0; i < numOfPoints; i++) {
                    String pointKey = "Point " + (i + 1);
                    
                    pointProperties.put(pointKey + " geom", "MyLatLong(longitude=" + coordinates.get(i).get(0) + ", latitude=" + coordinates.get(i).get(1) + ")");
                    pointProperties.put(pointKey + " arrival", group.get(i).getArrivalProximityText());
                    pointProperties.put(pointKey + " dist from stop", Integer.toString(group.get(i).getDistanceFromStop()));

                    if (group.get(i).getExpectedArrivalTime() != null) {
                        String expectedArrivalTimeStr = group.get(i).getExpectedArrivalTime().toString();
                        pointProperties.put(pointKey + " time", expectedArrivalTimeStr);
                    } else {
                        pointProperties.put(pointKey + " time", "NA");
                    }
                }

                // Add the map of point properties to the Properties class
                properties.setPoint(pointProperties);
                        
                // Add other properties as needed
                feature.setProperties(properties);

                featureList.add(feature);    
                                 
            }
        }

        //Create Feature Collection
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setType("FeatureCollection");
        featureCollection.setFeatures(featureList);

        //Serialize the GeoJSONFeatureCollection to JSON using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String geoJSONString = objectMapper.writeValueAsString(featureCollection);

        //Display GeoJSON
        return geoJSONString;
    }
    }

    boolean isWithin8To12Minutes(Date time1, Date time2) {
        long timeDifference = Math.abs(time1.getTime() - time2.getTime());
        long eightMinutesInMillis = 8 * 60 * 1000; // 8 minutes in milliseconds
        long twelveMinutesInMillis = 12 * 60 * 1000; // 12 minutes in milliseconds

        return timeDifference >= eightMinutesInMillis && timeDifference <= twelveMinutesInMillis;
    }

    private boolean isPointGeometry(List<CleanBusData> cleanBusData) {
    return cleanBusData.size() == 1;
}

    public void saveAllRawBusData(List<RawBusData> busdata) {
        rawBusDataRepository.saveAll(busdata);
    }
    
    public void saveAllCleanBusData(List<CleanBusData> busdata) {
        cleanBusDataRepository.saveAll(busdata);
    }
}
