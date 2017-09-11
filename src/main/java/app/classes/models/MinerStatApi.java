package app.classes.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

public class MinerStatApi {

    @Getter
    @Setter
    private Long time;

    @Getter
    @Setter
    private Long lastSeen;

    @Getter
    @Setter
    private BigDecimal reportedHashrate;

    @Getter
    @Setter
    private BigDecimal currentHashrate;

    @Getter
    @Setter
    private Long validShares;

    @Getter
    @Setter
    private Long invalidShares;

    @Getter
    @Setter
    private Long staleShares;

    @Getter
    @Setter
    private BigDecimal averageHashrate;

    @Getter
    @Setter
    private Long activeWorkers;

    @Getter
    @Setter
    private Long unpaid;

    @Getter
    @Setter
    private Object unconfirmed;

    @Getter
    @Setter
    private BigDecimal coinsPerMin;

    @Getter
    @Setter
    private BigDecimal usdPerMin;

    @Getter
    @Setter
    private BigDecimal btcPerMin;

    public MinerStatApi(Long time, Long lastSeen, BigDecimal reportedHashrate, BigDecimal currentHashrate, Long validShares, Long invalidShares, Long staleShares, BigDecimal averageHashrate, Long activeWorkers, Long unpaid, Object unconfirmed, BigDecimal coinsPerMin, BigDecimal usdPerMin, BigDecimal btcPerMin) {
        this.time = time;
        this.lastSeen = lastSeen;
        this.reportedHashrate = reportedHashrate;
        this.currentHashrate = currentHashrate;
        this.validShares = validShares;
        this.invalidShares = invalidShares;
        this.staleShares = staleShares;
        this.averageHashrate = averageHashrate;
        this.activeWorkers = activeWorkers;
        this.unpaid = unpaid;
        this.unconfirmed = unconfirmed;
        this.coinsPerMin = coinsPerMin;
        this.usdPerMin = usdPerMin;
        this.btcPerMin = btcPerMin;
    }
}
