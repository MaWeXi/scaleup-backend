package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.*;
import com.scaleup.backend.stockHistory.DTO.StockHistoryDTO;
import com.scaleup.backend.stockHistory.StockHistoryMax;
import com.scaleup.backend.stockHistory.StockHistoryMaxRepository;
import com.scaleup.backend.stockHistory.StockHistoryQuarterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    final StockRepository stockRepository;
    final StockHistoryQuarterRepository stockHistoryQuarterRepository;
    final StockHistoryMaxRepository stockHistoryMaxRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public StockService(
            StockRepository stockRepository,
            StockHistoryQuarterRepository stockHistoryQuarterRepository,
            StockHistoryMaxRepository stockHistoryMaxRepository
    ) {
        this.stockRepository = stockRepository;
        this.stockHistoryQuarterRepository = stockHistoryQuarterRepository;
        this.stockHistoryMaxRepository = stockHistoryMaxRepository;
    }

    public ResponseEntity<?> updateAllStocks(List<Stock> stocks) {
        try {
            stockRepository.saveAll(stocks);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<StockDTO> getStockBySymbol(String symbol) {
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);

        try {
            if (stockOptional.isPresent()) {

                /*
                Map Stock to StockDTO
                 */
                StockDTO stockDTO = modelMapper.map(stockOptional.get(), StockDTO.class);

                return new ResponseEntity<>(stockDTO, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<StockDTO> getStockWithHistory(String symbol, String interval) {
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);

        try {
            if (stockOptional.isPresent()) {

                List<StockHistoryDTO> stockHistories;

                switch (interval) {
                    case "day":
                    case "week":
                    case "month":
                        stockHistories = stockHistoryQuarterRepository
                                .getStockHistoryByQuarter(symbol, interval)
                                .collectAsList();
                        break;
                    case "year":
                        LocalDate dateNow = LocalDate.now();
                        List<StockHistoryMax> stockHistoryMax = stockHistoryMaxRepository
                                .findBySymbolAndDayBetween(symbol, dateNow.minusYears(1), dateNow);

                        List<StockHistoryDTO> stockHistoriesTemp = new ArrayList<>();
                        for (StockHistoryMax stockHistory : stockHistoryMax) {
                            Timestamp timestamp = Timestamp.valueOf(stockHistory.getDay().atStartOfDay());
                            stockHistoriesTemp.add(new StockHistoryDTO(timestamp, stockHistory.getClose()));
                        }
                        stockHistories = stockHistoriesTemp;

                        break;
                    default:
                        throw new CustomErrorException(HttpStatus.NOT_FOUND, "Interval not valid");
                }

                /*
                Map Stock to StockDTO
                 */
                StockDTO stockDTO = modelMapper.map(stockOptional.get(), StockDTO.class);
                stockDTO.setStockHistoryDTO(stockHistories);

                return new ResponseEntity<>(stockDTO, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<BidUpdate> getBidPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                BidUpdate bidUpdate = new BidUpdate();
                BigDecimal bidPrice = stock.get().getBidPrice();
                bidUpdate.setBidPrice(bidPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                bidUpdate.setBidPriceDevelopment((dayOpen.subtract(bidPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                return new ResponseEntity<>(bidUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<AskUpdate> getAskPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                AskUpdate askUpdate = new AskUpdate();
                BigDecimal askPrice = stock.get().getAskPrice();
                askUpdate.setAskPrice(askPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                askUpdate.setAskPriceDevelopment((dayOpen.subtract(askPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                return new ResponseEntity<>(askUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<CurrentPriceUpdate> getCurrentPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                CurrentPriceUpdate currentPriceUpdate = new CurrentPriceUpdate();
                BigDecimal currentPrice = stock.get().getCurrentPrice();
                currentPriceUpdate.setCurrentPrice(currentPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                currentPriceUpdate.setCurrentPriceDevelopment((dayOpen.subtract(currentPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                return new ResponseEntity<>(currentPriceUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
