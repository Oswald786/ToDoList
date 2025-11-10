package com.todolist;

import io.micronaut.core.optim.StaticOptimizations;
import io.micronaut.core.util.EnvironmentProperties;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentPropertiesOptimizationLoader implements StaticOptimizations.Loader<EnvironmentProperties> {
  private void load0(Map<String, List<String>> env) {
    env.put("USERDOMAIN_ROAMINGPROFILE", Arrays.asList("userdomain.roamingprofile", "userdomain-roamingprofile"));
    env.put("PATH", Arrays.asList("path"));
    env.put("SYSTEMDRIVE", Arrays.asList("systemdrive"));
    env.put("PROCESSOR_LEVEL", Arrays.asList("processor.level", "processor-level"));
    env.put("SESSIONNAME", Arrays.asList("sessionname"));
    env.put("ALLUSERSPROFILE", Arrays.asList("allusersprofile"));
    env.put("PROCESSOR_ARCHITECTURE", Arrays.asList("processor.architecture", "processor-architecture"));
    env.put("AMDRMSDKPATH", Arrays.asList("amdrmsdkpath"));
    env.put("PSModulePath", Arrays.asList("psmodulepath"));
    env.put("PROGRAMFILES", Arrays.asList("programfiles"));
    env.put("USERNAME", Arrays.asList("username"));
    env.put("PWD", Arrays.asList("pwd"));
    env.put("ProgramFiles(x86)", Arrays.asList("programfiles(x86)"));
    env.put("PATHEXT", Arrays.asList("pathext"));
    env.put("DriverData", Arrays.asList("driverdata"));
    env.put("OneDriveConsumer", Arrays.asList("onedriveconsumer"));
    env.put("OLDPWD", Arrays.asList("oldpwd"));
    env.put("WINDIR", Arrays.asList("windir"));
    env.put("ProgramData", Arrays.asList("programdata"));
    env.put("ProgramW6432", Arrays.asList("programw6432"));
    env.put("__PSLockDownPolicy", Arrays.asList("..pslockdownpolicy", ".-pslockdownpolicy", "-.pslockdownpolicy", "--pslockdownpolicy"));
    env.put("HOMEPATH", Arrays.asList("homepath"));
    env.put("PROCESSOR_IDENTIFIER", Arrays.asList("processor.identifier", "processor-identifier"));
    env.put("PUBLIC", Arrays.asList("public"));
    env.put("EXEPATH", Arrays.asList("exepath"));
    env.put("=::", Arrays.asList("=::"));
    env.put("SHLVL", Arrays.asList("shlvl"));
    env.put("LOCALAPPDATA", Arrays.asList("localappdata"));
    env.put("USERDOMAIN", Arrays.asList("userdomain"));
    env.put("LOGONSERVER", Arrays.asList("logonserver"));
    env.put("PLINK_PROTOCOL", Arrays.asList("plink.protocol", "plink-protocol"));
    env.put("JAVA_HOME", Arrays.asList("java.home", "java-home"));
    env.put("TERM", Arrays.asList("term"));
    env.put("OneDrive", Arrays.asList("onedrive"));
    env.put("MSYSTEM", Arrays.asList("msystem"));
    env.put("APPDATA", Arrays.asList("appdata"));
    env.put("SYSTEMROOT", Arrays.asList("systemroot"));
    env.put("3DVPATH", Arrays.asList("3dvpath"));
    env.put("OS", Arrays.asList("os"));
    env.put("EFC_8524_1592913036", Arrays.asList("efc.8524.1592913036", "efc.8524-1592913036", "efc-8524.1592913036", "efc-8524-1592913036"));
    env.put("COMPUTERNAME", Arrays.asList("computername"));
    env.put("COMSPEC", Arrays.asList("comspec"));
    env.put("COMMONPROGRAMFILES", Arrays.asList("commonprogramfiles"));
    env.put("PROCESSOR_REVISION", Arrays.asList("processor.revision", "processor-revision"));
    env.put("CommonProgramW6432", Arrays.asList("commonprogramw6432"));
    env.put("TERMINAL_EMULATOR", Arrays.asList("terminal.emulator", "terminal-emulator"));
    env.put("TEMP", Arrays.asList("temp"));
    env.put("USERPROFILE", Arrays.asList("userprofile"));
    env.put("HOMEDRIVE", Arrays.asList("homedrive"));
    env.put("TMP", Arrays.asList("tmp"));
    env.put("CommonProgramFiles(x86)", Arrays.asList("commonprogramfiles(x86)"));
    env.put("NUMBER_OF_PROCESSORS", Arrays.asList("number.of.processors", "number.of-processors", "number-of.processors", "number-of-processors"));
    env.put("HOME", Arrays.asList("home"));
  }

  @Override
  public EnvironmentProperties load() {
    Map<String, List<String>> env = new HashMap<String, List<String>>();
    load0(env);
    return EnvironmentProperties.of(env);
  }
}
