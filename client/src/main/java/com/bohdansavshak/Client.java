package com.bohdansavshak;

import com.bohdansavshak.model.TaxiTrip;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class Client {

  private static final int CHUNK_SIZE = 10 * 1024 * 1024; // 100 MB

  public static void main(String[] args) throws InterruptedException {
    WebClient webClient =
        WebClient.builder()
            .baseUrl("http://localhost:8080/api/v1/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    List<TaxiTrip> taxiTrips = readTaxiTripFromCsv();

    // Process the response
    Flux.fromIterable(taxiTrips)
        .flatMap(
            taxiTrip ->
                webClient
                    .post()
                    .uri("/message")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(taxiTrip))
                    .retrieve()
                    .bodyToMono(TaxiTrip.class))
        .subscribe(System.out::println);
    Thread.sleep(50000);
  }

  private static void splitLargeCsv() {
    String inputFilePath =
        "C:\\Users\\bohda\\IdeaProjects\\yellow_taxi_trip_data\\2018_Yellow_Taxi_Trip_Data.csv";
    String outputFolderPath = "C:\\Users\\bohda\\IdeaProjects\\yellow_taxi_trip\\split";

    try {
      splitCsvFile(inputFilePath, outputFolderPath);
      System.out.println("CSV file split successfully!");
    } catch (IOException e) {
      System.err.println("An error occurred while splitting the CSV file: " + e.getMessage());
    }
  }

  private static void splitCsvFile(String inputFilePath, String outputFolderPath)
      throws IOException {
    File inputFile = new File(inputFilePath);
    File outputFolder = new File(outputFolderPath);
    outputFolder.mkdirs();

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      String header = reader.readLine();
      String line;
      int chunkNumber = 1;
      long fileSize = inputFile.length();
      long remainingSize = fileSize;
      int bufferSize = 8 * 1024; // 8 KB
      char[] buffer = new char[bufferSize];

      while ((line = reader.readLine()) != null) {
        File outputFile = new File(outputFolder, "chunk_" + chunkNumber + ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
          writer.write(header);
          writer.newLine();

          long chunkSize = 0;
          while (line != null && (chunkSize + line.length() + 1) <= CHUNK_SIZE) {
            writer.write(line);
            writer.newLine();
            chunkSize += line.length() + 1; // Add 1 for the new line character
            line = reader.readLine();
          }
        }

        remainingSize -= outputFile.length();
        chunkNumber++;
      }
    }
  }

  public static List<TaxiTrip> readTaxiTripFromCsv() {
    String filePath = "C:\\Users\\bohda\\IdeaProjects\\yellow_taxi_trip\\split\\chunk_2.csv";

    try {
      return readCsvFile(filePath);
    } catch (IOException e) {
      System.err.println("An error occurred while reading the CSV file: " + e.getMessage());
    }
    return null;
  }

  private static List<TaxiTrip> readCsvFile(String filePath) throws IOException {
    List<TaxiTrip> taxiTrips = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String header = reader.readLine(); // Assuming the first line is the header
      String line;

      while ((line = reader.readLine()) != null) {
        String[] fields = line.split(",");

        long vendorId = Long.parseLong(fields[0]);
        String pickupTime = fields[1];
        String dropoffTime = fields[2];
        int passengerCount = Integer.parseInt(fields[3]);
        BigDecimal tripDistance = new BigDecimal(fields[4]);
        long rateCodeId = Long.parseLong(fields[5]);
        Boolean storeAndFwdFlag = fields[6].equals("Y");
        int puLocationId = Integer.parseInt(fields[7]);
        int doLocationId = Integer.parseInt(fields[8]);
        long paymentType = Long.parseLong(fields[9]);
        BigDecimal fareAmount = new BigDecimal(fields[10]);
        BigDecimal extra = new BigDecimal(fields[11]);
        BigDecimal mtaTax = new BigDecimal(fields[12]);
        BigDecimal tipAmount = new BigDecimal(fields[13]);
        BigDecimal tollsAmount = new BigDecimal(fields[14]);
        BigDecimal improvementSurcharge = new BigDecimal(fields[15]);
        BigDecimal totalAmount = new BigDecimal(fields[16]);

        TaxiTrip trip =
            TaxiTrip.builder()
                .vendorId(vendorId)
                .tpepPickupDatetime(pickupTime)
                .tpepDropoffDatetime(dropoffTime)
                .passengerCount(passengerCount)
                .tripDistance(tripDistance)
                .rateCodeId(rateCodeId)
                .storeAndFwdFlag(storeAndFwdFlag)
                .puLocationId(puLocationId)
                .doLocationId(doLocationId)
                .paymentTypeId(paymentType)
                .fareAmount(fareAmount)
                .extra(extra)
                .mtaTax(mtaTax)
                .tipAmount(tipAmount)
                .tollsAmount(tollsAmount)
                .improvementSurcharge(improvementSurcharge)
                .totalAmount(totalAmount)
                .build();

        taxiTrips.add(trip);
      }
    }

    return taxiTrips;
  }
}
