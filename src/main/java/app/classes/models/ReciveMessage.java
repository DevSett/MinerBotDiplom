package app.classes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReciveMessage {

    private String id;

    private String key;


    //в некоторых случаях служит ключом риг(при командах от веба)
    private String nameRig;

    private String information;

    private boolean getMoney;
    private boolean money;
    private boolean getConfig;
    private boolean config;
    private boolean infoRig;
    private boolean addUser;
    private boolean rebootRig;
    private boolean offRig;
}
