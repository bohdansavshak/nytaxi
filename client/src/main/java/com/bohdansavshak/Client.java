package com.bohdansavshak;

import com.bohdansavshak.model.TaxiTrip;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadDirectoryRequest;

@SpringBootApplication
@Slf4j
public class Client implements CommandLineRunner {

  private final WebClient webClient;
  private final S3TransferManager s3TransferManager;

  @Value("${sample.data.path}")
  private String sampleDataPath;

  @Value("${first.half.of.the.year}")
  private String firstHalfOfTheYearFile;

  @Value("${second.half.of.the.year}")
  private String secondHalfOfTheYearFile;

  @Value("${s3.bucket.name}")
  private String s3BucketName;

  public Client(WebClient webClient, S3TransferManager s3TransferManager) {
    this.webClient = webClient;
    this.s3TransferManager = s3TransferManager;
  }

  public static void main(String[] args) {
    SpringApplication.run(Client.class, args);
  }

  @Override
  public void run(String... args) {
    downloadOnlyNewFilesToSampleDataFolderFromS3();

    List<TaxiTrip> firstHalfOfTheYearTaxiTrips =
        readSampleFile(Paths.get(sampleDataPath, firstHalfOfTheYearFile));
    List<TaxiTrip> secondHalfOfTheYearTaxiTripts =
        readSampleFile(Paths.get(sampleDataPath, secondHalfOfTheYearFile));

    var start = System.currentTimeMillis();
    var firstExecutionTime = sendWriteRequestsToFrontend(firstHalfOfTheYearTaxiTrips);
    var secondExecutionTime = sendWriteRequestsToFrontend(secondHalfOfTheYearTaxiTripts);

    List<Long> executionTimes =
        Stream.concat(firstExecutionTime.stream(), secondExecutionTime.stream()).toList();

    logPercentiles(executionTimes, System.currentTimeMillis() - start);

    log.info("In total send: {} requests", executionTimes.size());
    log.info("Finished");
    System.exit(0);
  }

  private static void logPercentiles(List<Long> executionTime, long timeItTook) {
    List<Long> sortedExecutionTime = executionTime.stream().sorted().toList();
    log.info("99 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.99)));
    log.info("95 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.95)));
    log.info("90 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.9)));
    log.info("50 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.5)));
    log.info("Took: {}", timeItTook);
  }

  private List<Long> sendWriteRequestsToFrontend(List<TaxiTrip> taxiTrips) {
    log.info("Start sending taxi trips to frontend, taxiTrips.size: {}", taxiTrips.size());

    List<Long> executionTime =
        Flux.fromIterable(taxiTrips)
            .buffer(20)
            .delayElements(Duration.ofSeconds(1))
            .flatMapIterable(e -> e)
            .flatMap(this::sendRequest)
            .collectList()
            .block();
    return executionTime;
  }

  private void downloadOnlyNewFilesToSampleDataFolderFromS3() {
    Path sampleDataPath = Paths.get(this.sampleDataPath);
    Set<String> existingFiles = getExistingFiles(sampleDataPath);

    s3BucketName = "467576817753-bohdansavshak-nytaxi";
    DirectoryDownload directoryDownload =
        s3TransferManager.downloadDirectory(
            DownloadDirectoryRequest.builder()
                .destination(sampleDataPath)
                .filter(key -> !existingFiles.contains(key.key()))
                .bucket(s3BucketName)
                .build());
    CompletedDirectoryDownload completedDirectoryDownload =
        directoryDownload.completionFuture().join();

    completedDirectoryDownload
        .failedTransfers()
        .forEach(fail -> log.warn("Object [{}] failed to transfer", fail.toString()));
    var failedTransferNumber = completedDirectoryDownload.failedTransfers().size();
    log.info("Number of failed transfer number: {}", failedTransferNumber);
  }

  private static Set<String> getExistingFiles(Path directoryPath) {
    Set<String> existingFiles = new HashSet<>();
    try (Stream<Path> paths = Files.list(directoryPath)) {
      paths
          .filter(Files::isRegularFile)
          .map(path -> path.getFileName().toString())
          .forEach(existingFiles::add);
    } catch (Exception e) {
      System.err.println("Error listing files in directory: " + e.getMessage());
    }
    return existingFiles;
  }

  private Mono<Long> sendRequest(TaxiTrip taxiTrip) {
    var s = System.currentTimeMillis();
    return webClient
        .post()
        .uri("/api/v1/message")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(taxiTrip))
        .retrieve()
        .bodyToMono(TaxiTrip.class)
        .map(e -> (System.currentTimeMillis() - s));
  }

  public List<TaxiTrip> readSampleFile(Path filePath) {
    try {
      return readCsvFile(filePath);
    } catch (IOException e) {
      System.err.println("An error occurred while reading the CSV file: " + e.getMessage());
    }
    return null;
  }

  private List<TaxiTrip> readCsvFile(Path filePath) throws IOException {
    List<TaxiTrip> taxiTrips = new ArrayList<>();

    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
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
