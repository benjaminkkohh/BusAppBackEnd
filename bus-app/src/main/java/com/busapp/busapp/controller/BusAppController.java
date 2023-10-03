package com.busapp.busapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.busapp.busapp.service.BusDataService;

@CrossOrigin
@Controller
public class BusAppController {
    @Autowired
    private BusDataService busDataService;
    

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty file");
        }

        try {
            // Read and parse the CSV file
            // Assuming you have a method in BusDataService to handle this
            busDataService.insertRawData(file);
            

            return ResponseEntity.ok("Successful Import");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getAllVehicleReferences")
    @ResponseBody
    public ResponseEntity<List<String>> getAllVehicleReferences() throws IOException, SQLException{
        try {
          
            busDataService.getVehicleReferences();
            

            return ResponseEntity.ok(busDataService.getVehicleReferences());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(busDataService.getVehicleReferences());
        }
    }

    
    @GetMapping("/getAllPublishedLineNames")
    @ResponseBody
    public ResponseEntity<List<String>> getPublishedLineNames() throws IOException, SQLException{
        try {
          
            busDataService.getPublishedLineNames();
            

            return ResponseEntity.ok(busDataService.getPublishedLineNames());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(busDataService.getPublishedLineNames());
        }
    }
}
