package develop.p2p.chatchan.Interface;

import java.io.*;
import java.util.Properties;

public class ConfigInterface
{
    File file;
    Properties settings = new Properties();

    public ConfigInterface(String fileName)
    {
        this.file = new File(fileName);
    }

    public boolean isExists()
    {
        return file.exists();
    }

    public boolean saveDefaultConfig() throws IOException
    {
        if (this.isExists())
            return true;
        this.file.createNewFile();
        try (FileOutputStream out = new FileOutputStream(file))
        {
            settings.storeToXML(out, "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getString(String key, String defaults) throws IOException
    {
        settings.loadFromXML(new FileInputStream(file));
        return settings.getProperty(key, defaults);
    }

    public void setProperty(String key, String value)
    {
        settings.setProperty(key, value);
    }

    public boolean isKeyExists(String key)
    {
        return settings.containsKey(key);
    }
}
