package app.classes.configurations;

import app.MainApp;
import app.classes.models.Autorithation;
import app.classes.models.Property;
import app.classes.server.Server;
import app.classes.services.MinerBot;
import app.classes.services.ServiceGpu;
import app.classes.services.ServiceMoney;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * Created by Сергей on 05.07.2017.
 */
@Configuration
public class MainConfig {

    @Autowired
    Property property;

    @Bean
    public Property property() {
        return new Property();
    }

    @Bean
    @Scope("prototype")
    public MinerBot minerBot() {



        String host = "197.243.34.228";
        int port = 		3128;

//        for (int i = 0; i < property.getPorts().size() && host.isEmpty(); i++) {
//            if (ServiceGpu.testConnection(property.getHosts().get(i), property.getPorts().get(i))) {
//                host = property.getHosts().get(i);
//                port = property.getPorts().get(i);
//            }
//        }

        if (port==0 || host.isEmpty()){
            return new MinerBot();
        }
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials("", ""));

        HttpHost httpHost = new HttpHost(host, port);

        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(true).build();
        botOptions.setRequestConfig(requestConfig);
        botOptions.setCredentialsProvider(credsProvider);
        botOptions.setHttpProxy(httpHost);
        return new MinerBot(botOptions);
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
    public ServiceMoney serviceMoney() {
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
