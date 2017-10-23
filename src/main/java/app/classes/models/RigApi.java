package app.classes.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class RigApi {
    @Getter
    private String worker;

    @Getter
    private Long time;

    @Getter
    private Long lastSeen;

    @Getter
    private BigDecimal reportedHashrate;

    @Getter
    private BigDecimal currentHashrate;

    @Getter
    private Long validShares;

    @Getter
    private Long invalidShares;

    @Getter
    private Long staleShares;

    @Getter
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
        setWorker(worker);
        setTime(time);
       setLastSeen(lastSeen);
        setReportedHashrate(reportedHashrate);
        setCurrentHashrate(currentHashrate);
        setValidShares(validShares);
        setInvalidShares(invalidShares);
        setStaleShares(staleShares);
        setAverageHashrate(averageHashrate);
    }

    public void setWorker(String worker) {
        this.worker = worker==null?"":worker;
    }

    public void setTime(Long time) {
        this.time = time==null?0l:time;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen==null?0l:lastSeen;
    }

    public void setReportedHashrate(BigDecimal reportedHashrate) {
        this.reportedHashrate = reportedHashrate==null?BigDecimal.ZERO:reportedHashrate;
    }

    public void setCurrentHashrate(BigDecimal currentHashrate) {
        this.currentHashrate  = currentHashrate==null?BigDecimal.ZERO:currentHashrate;
    }

    public void setValidShares(Long validShares) {
        this.validShares = validShares ==null?0l:validShares;
    }

    public void setInvalidShares(Long invalidShares) {
        this.invalidShares = invalidShares ==null?0l:invalidShares;
    }

    public void setStaleShares(Long staleShares) {
        this.staleShares = staleShares ==null?0l:staleShares;
    }

    public void setAverageHashrate(BigDecimal averageHashrate) {
        this.averageHashrate = averageHashrate==null?BigDecimal.ZERO:averageHashrate;
    }
}
