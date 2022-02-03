package com.scaleup.backend.stockHistory;

import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;
import com.scaleup.backend.stockHistory.DTO.StockHistoryDTO;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

@Repository
public class StockHistoryQuarterRepository {

    SparkConf conf = new SparkConf()
            .setAppName("ScaleUp")
            .setMaster("local[2]")
            .set("spark.files", "src/main/resources/secure-connect-scaleup.zip")
            .set("spark.cassandra.connection.config.cloud.path", "secure-connect-scaleup.zip")
            .set("spark.cassandra.auth.username", "tZYEZcaGLcmRQKHIZIjMuxrG")
            .set("spark.cassandra.auth.password", "YFLCWpIZuUgERH4JQZQQk183ajAt1Sx0cU+JpDBuYWC85RNbQfbhpd5WZWPmEBXXfT9fvui6O9wc-yQymOPU2-dot83pxsBwK4CBHlJn.-KnxP-2IDzR-KpmHjX.,WEZ")
            .set("spark.dse.continuousPagingEnabled", "false")
            .set("spark.testing.memory", "2147480000");

    SparkSession spark = SparkSession
            .builder()
            .config(conf)
            .getOrCreate();

    public Dataset<StockHistoryDTO> getStockHistoryByQuarter(String symbol, String interval) {

        LocalDate date = LocalDate.now().minusYears(1);
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-Q");
        String dateString = dtf.format(date);

        String filterExpression;
        LocalDate pastDate;
        switch (interval) {
            case "month":
                filterExpression = "minute == 0";
                pastDate = date.minusMonths(1);
                break;
            case "week":
                filterExpression = "minute % 30 == 0";
                pastDate = date.minusWeeks(1);
                break;
            case "day":
                filterExpression = "minute % 15 == 0";
                pastDate = date.minusDays(1);
                break;
            default:
                filterExpression = "minute % 15 == 0";
                pastDate = date.minusDays(2);
                break;
        }

        Dataset<Row> df = spark.read()
                .format("org.apache.spark.sql.cassandra")
                .options(ImmutableMap.of("table", "stock_history_by_quarter", "keyspace", "testing"))
                .load()
                .filter(col("symbol").like(symbol))
                .filter(col("year_and_quarter").like(dateString))
                .withColumn("minute", minute(col("date")))
                .withColumn("hour", hour(col("date")))
                .filter(col("date")
                        .lt(lit(Date.valueOf(date.plusDays(1)))))
                .filter(col("date")
                        .gt(lit(Date.valueOf(pastDate))))
                .filter(filterExpression)
                .select("close", "date");

        Encoder<StockHistoryDTO> stockHistoryEncoder = Encoders.bean(StockHistoryDTO.class);
        return df.as(stockHistoryEncoder);
    }
}
