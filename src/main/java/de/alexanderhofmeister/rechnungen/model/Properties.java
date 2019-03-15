package de.alexanderhofmeister.rechnungen.model;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;

public class Properties {

    private static Properties INSTANCE;

    private PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();

    private Properties() {
        try {
            this.propertiesConfiguration = new PropertiesConfiguration("./config.properties");
        } catch (ConfigurationException e) {
            // TODO ERROR HANDLING
            e.printStackTrace();
        }
    }

    public static Properties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Properties();

        return INSTANCE;
    }

    public List<String> getKeys() {
        return IteratorUtils.toList(propertiesConfiguration.getKeys());
    }

    public void setProperty(String key, Object value) {
        this.propertiesConfiguration.setProperty(key, value);
    }

    public void save() throws ConfigurationException {
        this.propertiesConfiguration.save();
    }

    public String getString(String key) {
        return this.propertiesConfiguration.getString(key);
    }

    public int getInt(String key) {
        return this.propertiesConfiguration.getInt(key);
    }
}
