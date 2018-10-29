package app.classes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RigsGson {
//             "key": "12345",
//             "link": "https://api.ethermine.org",
//             "account":"0x1a35478Ec84E49bf4F5A78A470a2740ee7fe042F",
//             "keyUser":"",
//             "type": 0

    private String key;
    private String link;
    private String account;
    private int type;

}
