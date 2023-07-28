package com.bohdansavshak;

import com.bohdansavshak.model.TaxiTrip;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CsvSplitter {

    private static final int CHUNK_SIZE = 10 * 1024 * 1024; // 100 MB

    public static void main(String[] args) throws InterruptedException {
        WebClient webClient2 = WebClient.builder()
                .baseUrl("http://localhost:8080/api/v1/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();


//
        List<TaxiTrip> taxiTrips = readTaxiTripFromCsv();
//        TaxiTrip block = webClient2.post()
//                .uri("/r2dbc/message")
//                .body(Mono.just(taxiTrips.get(10)), TaxiTrip.class)
//                .retrieve()
//                .bodyToMono(TaxiTrip.class)
//                .block();
//        System.out.println(block);



        // Process the response
        Flux.fromIterable(taxiTrips)
                .flatMap(taxiTrip -> {
                    System.out.println(taxiTrip);
                    MultiValueMap<String, String> formData = toMap(taxiTrip);

                        return webClient2.post()
                                .uri("/message")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(BodyInserters.fromValue(taxiTrip))
                                .retrieve()
                                .bodyToMono(TaxiTrip.class);})
                .subscribe(System.out::println);


        Thread.sleep(50000);
    }

    public static MultiValueMap<String, String> toMap(TaxiTrip taxiTrip) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            formData.add("id", String.valueOf(taxiTrip.getId()));
            formData.add("tpepPickupDatetime", taxiTrip.getTpepPickupDatetime());
            formData.add("tpepDropoffDatetime", taxiTrip.getTpepDropoffDatetime());
            formData.add("dropOffDay", taxiTrip.getDropOffDay());
            formData.add("dropOffMonth", taxiTrip.getDropOffMonth());
            formData.add("passengerCount", String.valueOf(taxiTrip.getPassengerCount()));
            formData.add("tripDistance", taxiTrip.getTripDistance().toString());
            formData.add("puLocationId", String.valueOf(taxiTrip.getPuLocationId()));
            formData.add("doLocationId", String.valueOf(taxiTrip.getDoLocationId()));
            formData.add("storeAndFwdFlag", String.valueOf(taxiTrip.getStoreAndFwdFlag()));
            formData.add("fareAmount", taxiTrip.getFareAmount().toString());
            formData.add("extra", taxiTrip.getExtra().toString());
            formData.add("mtaTax", taxiTrip.getMtaTax().toString());
            formData.add("improvementSurcharge", taxiTrip.getImprovementSurcharge().toString());
            formData.add("tipAmount", taxiTrip.getTipAmount().toString());
            formData.add("tollsAmount", taxiTrip.getTollsAmount().toString());
            formData.add("totalAmount", taxiTrip.getTotalAmount().toString());
            formData.add("vendorId", String.valueOf(taxiTrip.getVendorId()));
            formData.add("rateCodeId", String.valueOf(taxiTrip.getRateCodeId()));
            formData.add("paymentTypeId", String.valueOf(taxiTrip.getPaymentTypeId()));

        return formData;
    }
    record Message(String message) {
    }
    private static void splitLargeCsv() {
        String inputFilePath = "C:\\Users\\bohda\\IdeaProjects\\yellow_taxi_trip_data\\2018_Yellow_Taxi_Trip_Data.csv";
        String outputFolderPath = "C:\\Users\\bohda\\IdeaProjects\\yellow_taxi_trip\\split";

        try {
            splitCsvFile(inputFilePath, outputFolderPath);
            System.out.println("CSV file split successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while splitting the CSV file: " + e.getMessage());
        }
    }

    private static void splitCsvFile(String inputFilePath, String outputFolderPath) throws IOException {
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

                TaxiTrip trip = TaxiTrip.builder()
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
