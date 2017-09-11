package app.classes.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class RigApi {
    @Getter
    @Setter
    private String worker;

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


    public RigApi(
            String worker,
            Long time,
            Long lastSeen,
            BigDecimal reportedHashrate,
            BigDecimal currentHashrate,
            Long validShares,
            Long invalidShares,
            Long staleShares,
            BigDecimal averageHashrate
    ) {
        this.worker = worker;
        this.time = time;
        this.lastSeen = lastSeen;
        this.reportedHashrate = reportedHashrate;
        this.currentHashrate = currentHashrate;
        this.validShares = validShares;
        this.invalidShares = invalidShares;
        this.staleShares = staleShares;
        this.averageHashrate = averageHashrate;
    }
}
