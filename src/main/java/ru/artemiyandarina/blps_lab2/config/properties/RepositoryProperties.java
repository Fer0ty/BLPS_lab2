package ru.artemiyandarina.blps_lab2.config.properties;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.nio.file.Path;
import java.nio.file.Paths;

@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "application.repositories")
public class RepositoryProperties {
    private String baseDir = System.getProperty("user.dir") + "/application-repositories";
    private String filesDir = "files";
    private String xmlDir = "xml";
    private String userXmlFilename = "users.xml";

    public Path getBaseDir() {
        return Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public Path getFilesDir() {
        return getBaseDir().resolve(filesDir).normalize();
    }

    public Path getXmlDir() {
        return getBaseDir().resolve(xmlDir).normalize();
    }

    public Path getUserXmlFilename() {
        return getXmlDir().resolve(userXmlFilename).normalize();
    }
}
