package com.scaleup.backend.stock;

import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import com.scaleup.backend.stock.DTO.ChartData;
import com.scaleup.backend.stock.DTO.StockHistory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;
import static org.apache.spark.sql.functions.*;

@Repository
public class Test {

    SparkConf conf = new SparkConf()
            .setAppName("ScaleUp")
            .setMaster("local[2]")

            // .set("spark.files", "C:/Users/Maximilian/Desktop/dsbulk-1.8.0/bin/secure-connect-scaleup.zip")
            .set("spark.files", "C:\\Users\\Maximilian\\Desktop\\ScaleUp\\src\\main\\resources\\secure-connect-scale-up.zip")

            .set("spark.cassandra.connection.config.cloud.path", "secure-connect-scale-up.zip")

            // .set("spark.cassandra.auth.username", "DxoGprmPXqJppiSUXTAdDyzS")
            .set("spark.cassandra.auth.username", "lcvBkCHWMbuczSdpEEmSjnYd")

            // .set("spark.cassandra.auth.password", "wzTpZWbHcps4k5zAF1qTJSvQwW0fE0rEHNPhUR0xRGvLv_108KA46vYkBrjug96+LIbvkY2qPY5n4crt+Zjx9HpIPUSPdfZaHLHJvYX5pZ-so-RHZoZ5.jWcnNYtL.bT")
            .set("spark.cassandra.auth.password", "_K4g+3+Sdx0Shib_IRwL190+irmCZ5DOh6h-424XJ5i+7mfQNFIXKPv-.,bM3JelrjY918ebL6ttC6ujGE2FKXs4RM7oMiNHzS+5kdm0zc4os5sxhIv0UWc6SZ0JjKK2")

            .set("spark.dse.continuousPagingEnabled", "false")
            .set("spark.testing.memory", "2147480000");
    JavaSparkContext sc = new JavaSparkContext(conf);

    SparkSession spark = SparkSession
            .builder()
            .config(conf)
            .getOrCreate();


    public List<ChartData> getUserId() {
//        JavaRDD<Stock> stockRDD = javaFunctions(sc)
//                // .select("symbol")
//                .cassandraTable("testing", "stocks", mapRowTo(Stock.class));
//        Dataset<Row> df = spark.createDataFrame(stockRDD, Stock.class);

        LocalDate date = LocalDate.now().minusYears(2);
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");
        String dateString = dtf.format(date);
        String symbol = "AAPL";

        List<ChartData> hist = new ArrayList<>();
        Encoder<StockHistory> stockHistoryEncoder = Encoders.bean(StockHistory.class);


        Dataset<Row> df = spark.read()
                .format("org.apache.spark.sql.cassandra")
                .options(ImmutableMap.of("table", "stock_by_month", "keyspace", "test"))
                .load()
                .filter(col("symbol").like(symbol))
                .filter(col("year_and_month").like(dateString))
                .withColumn("minute", minute(col("date")))
                .withColumn("hour", hour(col("date")));

        Dataset<Row> df_daily = df
                // .withColumn("mod", expr("minute % 30"))
                .filter(col("date")
                        .lt(lit(Date.valueOf(date.plusDays(1)))))
                .filter(col("date")
                        .gt(lit(Date.valueOf(date.minusDays(1)))))
                .select("close", "date");


        Dataset<StockHistory> dailyHistoryDF = df_daily.as(stockHistoryEncoder);
        hist.add(new ChartData("daily", dailyHistoryDF.collectAsList()));

        Dataset<Row> df_weekly = df
                .filter(col("date")
                        .lt(lit(Date.valueOf(date.plusDays(1)))))
                .filter(col("date")
                        .gt(lit(Date.valueOf(date.minusWeeks(1)))))
                .filter("minute % 30 == 0")
                .select("close", "date");

        Dataset<StockHistory> weeklyHistoryDF = df_weekly.as(stockHistoryEncoder);
        hist.add(new ChartData("weekly", weeklyHistoryDF.collectAsList()));

        Dataset<Row> df_monthly = df
                .filter(col("date")
                        .lt(lit(Date.valueOf(date.plusDays(1)))))
                .filter(col("date")
                        .gt(lit(Date.valueOf(date.minusMonths(1)))))
                .filter("minute == 0")
                .select("close", "date");

        df_monthly.show(10);

        Dataset<StockHistory> monthlyHistoryDF = df_monthly.as(stockHistoryEncoder);
        hist.add(new ChartData("monthly", monthlyHistoryDF.collectAsList()));

        return hist;
    }
}
