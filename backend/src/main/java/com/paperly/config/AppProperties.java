package com.paperly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private SeedAdmin seedAdmin = new SeedAdmin();
    private SeedFaculty seedFaculty = new SeedFaculty();

    public SeedAdmin getSeedAdmin() {
        return seedAdmin;
    }

    public void setSeedAdmin(SeedAdmin seedAdmin) {
        this.seedAdmin = seedAdmin;
    }

    public SeedFaculty getSeedFaculty() {
        return seedFaculty;
    }

    public void setSeedFaculty(SeedFaculty seedFaculty) {
        this.seedFaculty = seedFaculty;
    }

    public static class SeedAdmin {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class SeedFaculty {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
