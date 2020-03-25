package com.converage.utils;

import com.converage.architecture.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 运行环境
 */
@Component
public class EnvironmentUtils {

    @Autowired
    private Environment environment;

    public boolean isPro() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return "pro".equals(activeProfiles[0]);
        }
        return false;
    }

    public boolean isTest() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return "test".equals(activeProfiles[0]);
        }
        return false;
    }

    public boolean isDev() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return "dev".equals(activeProfiles[0]);
        }
        return false;
    }

    public void checkIfPro() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (!"pro".equals(activeProfiles[0])) {
            throw new BusinessException("not in pro environment");
        }
    }


}
