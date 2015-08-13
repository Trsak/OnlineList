package cz.trsak.onlinelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

public class VersionChecker {
    String URLGit = "https://raw.githubusercontent.com/Trsak/OnlineList/master/src/plugin.yml";
    String Version = "";
    String GitVersion = "";

    public VersionChecker(String Version) {
        this.Version = Version;
        try {
            URL oracle = new URL(URLGit);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("version")) {
                    inputLine = inputLine.replace("version:", "");
                    GitVersion = inputLine.trim();
                    break;
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean Check() {
        if (Version != GitVersion) {
            return false;
        }
        return true;
    }
}
