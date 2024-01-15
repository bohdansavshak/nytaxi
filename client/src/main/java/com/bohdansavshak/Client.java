package com.bohdansavshak;

import com.bohdansavshak.model.TaxiTrip;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

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

    var start1 = System.currentTimeMillis();
    var firstExecutionTime = sendWriteRequestsToFrontend(firstHalfOfTheYearTaxiTrips);
    var finish1 = System.currentTimeMillis() - start1;

    sleepFor3minutes();

    AtomicInteger counter = new AtomicInteger();
    sendRandomRequestsToGetDayTotal(counter);
    sendRandomRequestsToGetMonthTotal(counter);

    var start2 = System.currentTimeMillis();
    var secondExecutionTime = sendWriteRequestsToFrontend(secondHalfOfTheYearTaxiTripts);
    var finish2 = System.currentTimeMillis() - start2;

    log.info("Percentiles for write requests.");
    List<Long> executionTimes =
        Stream.concat(firstExecutionTime.stream(), secondExecutionTime.stream()).toList();
    logPercentiles(executionTimes, executionTimes.size() / ((finish1 + finish2) / 1000));

    log.info(
        "In total send: {} requests in: {} seconds",
        executionTimes.size(),
        (finish1 + finish2) / 1000);
    System.exit(0);
  }

  private static void sleepFor3minutes() {
    try {
      Thread.sleep(Duration.ofMinutes(3));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void sendRandomRequestsToGetMonthTotal(AtomicInteger counter) {
    var s2 = System.currentTimeMillis();
    List<Long> executionTimesForMonthTotals =
        Flux.range(1, 100)
            .flatMap(
                i -> {
                  var s = System.currentTimeMillis();
                  LocalDate randomDate = generateRandomDate(2018, 2018, Month.JANUARY, Month.JUNE);
                  return webClient
                      .get()
                      .uri(
                          uriBuilder ->
                              uriBuilder
                                  .path("/api/v1/total")
                                  .queryParam("year", randomDate.getYear())
                                  .queryParam("month", randomDate.getMonthValue())
                                  .build())
                      .accept(MediaType.APPLICATION_JSON)
                      .retrieve()
                      .bodyToMono(String.class)
                      .map(
                          e -> {
                            int count = counter.incrementAndGet();
                            log.info("Request {} : {}", count, e);
                            return (System.currentTimeMillis() - s);
                          });
                })
            .collectList()
            .block();

    log.info("Percentiles for get totals randomly 100 times for month data.");
    logPercentiles(executionTimesForMonthTotals, System.currentTimeMillis() - s2);
  }

  private void sendRandomRequestsToGetDayTotal(AtomicInteger counter) {
    var s1 = System.currentTimeMillis();
    var executionTimesForGetTotalPerDay =
        Flux.range(1, 1000)
            .flatMap(
                i -> {
                  var s = System.currentTimeMillis();
                  LocalDate randomDate = generateRandomDate(2018, 2018, Month.JANUARY, Month.JUNE);
                  return webClient
                      .get()
                      .uri(
                          uriBuilder ->
                              uriBuilder
                                  .path("/api/v1/total")
                                  .queryParam("year", randomDate.getYear())
                                  .queryParam("month", randomDate.getMonthValue())
                                  .queryParam("day", randomDate.getDayOfMonth())
                                  .build())
                      .accept(MediaType.APPLICATION_JSON)
                      .retrieve()
                      .bodyToMono(String.class)
                      .map(
                          e -> {
                            int count = counter.incrementAndGet();
                            log.info("Request {} : {}", count, e);
                            return (System.currentTimeMillis() - s);
                          });
                })
            .collectList()
            .block();
    log.info("Percentiles for get totals randomly 1000 times for totals per day data.");
    logPercentiles(executionTimesForGetTotalPerDay, System.currentTimeMillis() - s1);
  }

  private static void logPercentiles(List<Long> executionTime, long throughput) {
    List<Long> sortedExecutionTime = executionTime.stream().sorted().toList();
    log.info("99 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.99)));
    log.info("95 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.95)));
    log.info("90 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.9)));
    log.info("50 percentile: {}", sortedExecutionTime.get((int) (executionTime.size() * 0.5)));
    log.info("throughput: {}", throughput);
  }

  public LocalDate generateRandomDate(
      int startYear, int endYear, Month startMonth, Month endMonth) {
    Random random = new Random();
    int minDay = (int) LocalDate.of(startYear, startMonth, 1).toEpochDay();
    int maxDay = (int) LocalDate.of(endYear, endMonth, endMonth.length(true)).toEpochDay();
    long randomDay = minDay + random.nextInt(maxDay - minDay);
    return LocalDate.ofEpochDay(randomDay);
  }

  private List<Long> sendWriteRequestsToFrontend(List<TaxiTrip> taxiTrips) {
    log.info("Start sending taxi trips to frontend, taxiTrips.size: {}", taxiTrips.size());

    List<Long> executionTime =
        Flux.fromIterable(taxiTrips)
            //            .buffer(2000)
            //            .delayElements(Duration.ofSeconds(1))
            //            .flatMapIterable(e -> e)
            .flatMap(this::sendRequest)
            .collectList()
            .block();
    return executionTime;
  }

  private void downloadOnlyNewFilesToSampleDataFolderFromS3() {
    Path sampleDataPath = Paths.get(this.sampleDataPath);
    Set<String> existingFiles = getExistingFiles(sampleDataPath);

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
