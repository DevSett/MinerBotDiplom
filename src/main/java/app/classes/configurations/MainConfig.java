package app.classes.configurations;

import app.MainApp;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import app.classes.services.MinerBot;
import app.classes.services.ServiceGpu;
import app.classes.services.ServiceMoney;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Сергей on 05.07.2017.
 */
@Configuration
public class MainConfig {

    @Bean
    public Property property() {
        return new Property();
    }

    @Bean
    @Scope("prototype")
    public MinerBot minerBot() {
        return new MinerBot();
    }

    @Bean
    public MainApp mainApp() {
        return new MainApp();
    }

    @Bean
    @Scope("prototype")
    public Autorithation autorithation() {
        return new Autorithation();
    }

    @Bean
    public ServiceGpu serviceGpu() {
        return new ServiceGpu();
    }

    @Bean
    public ServiceMoney serviceMoney(){
        return new ServiceMoney();
    }

//    @Bean
//    @Scope("prototype")
//    public MainWindow mainWindow(){return new MainWindow();}
//
//    @Bean
//    @Scope("prototype")
//    public MenuHbox menuHbox(){return new MenuHbox();}

}
