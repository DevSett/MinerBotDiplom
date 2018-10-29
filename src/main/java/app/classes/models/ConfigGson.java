package app.classes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor

public class ConfigGson {

//    Ip=localhost
//            Port=6457
//    PathToLog=log.txt
//            RigName=daddy1
//    Charset=UTF-8
//    TemperatureArg=Temp:
//    TotalArg=Total speed:
//    MhsArg=GPU0:
//    TYPE=1
//    Key=6BFHWC035QXWSMDB4R4EXYWGFG1YS30T9XOYG9GV

    private String ip;
    private String port;
    private String pathToLog;
    private String rigName;
    private String charset;
    private String temperatureArg;
    private String totalArg;
    private String mhsArg;
    private String type;
    private String key;

}
