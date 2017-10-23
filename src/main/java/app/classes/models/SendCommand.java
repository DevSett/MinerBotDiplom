package app.classes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendCommand {

    private String id;


    private boolean status;


    private boolean status_debug;


    private boolean service_reboot_rig;


    private boolean service_shutdown_rig;

    private boolean screenshot;

    private boolean getName;

}
