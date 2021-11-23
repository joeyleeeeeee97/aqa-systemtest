package net.adoptopenjdk.test;

import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.environment.FileRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

public class AppCDSTest implements StfPluginInterface {
    FileRef springPetclinicJar;

    @Override
    public void help(HelpTextGenerator help) throws Exception {
        help.outputSection("AppCDSTest");
        help.outputText("Test spring petclinic using AppCDS");
    }

    @Override
    public void pluginInit(StfCoreExtension svt) throws Exception {
    }

    @Override
    public void setUp(StfCoreExtension svt) throws Exception {
        springPetclinicJar = svt.locateResourceFile("spring-petclinic-2.4.0.BUILD-SNAPSHOT.jar");
    }

    public void execute(StfCoreExtension test) throws Exception {
        DirectoryRef tmpDir =  test.env().getTmpDir();

//        StfProcess defaultRunner = test.doRunBackgroundProcess("Default Run", "r1", ECHO_ON, ExpectedOutcome.neverCompletes(),
//                test.createJavaProcessDefinition()
//                        .addProjectToClasspath("openjdk.test.appcds")
//                        .addJarToClasspath(springPetclinicJar)
//                        .runClass("org.springframework.boot.loader.JarLauncher")
//                        .addArg("org.springframework.samples.petclinic.PetClinicApplication"));
//
//        test.doSleep("sleep", "10");
//        test.doFindFileMatches("Assert Started in 10 seconds", defaultRunner.getStdoutFileRef(), "Started PetClinicApplication");
//        test.doKillProcesses("stop r1", defaultRunner);

        StfProcess dumper = test.doRunBackgroundProcess("Dump Run", "r2", ECHO_ON, ExpectedOutcome.neverCompletes(),
                test.createJavaProcessDefinition()
                        .addJvmOption("-Xlog:class+path+cds=debug -XX:ArchiveClassesAtExit=" + tmpDir.getPath() + "/app-cds.jsa")
                        .addJarToClasspath(springPetclinicJar)
                        .runClass("org.springframework.boot.loader.JarLauncher")
                        .addArg("org.springframework.samples.petclinic.PetClinicApplication"));

        test.doSleep("sleep", "10");
        test.doFindFileMatches("Assert Started in 10 seconds", dumper.getStdoutFileRef(), "Started PetClinicApplication");
        test.doKillProcesses("stop r2", dumper);


        StfProcess AppCDSRunner = test.doRunBackgroundProcess("AppCDS Run", "r3", ECHO_ON, ExpectedOutcome.neverCompletes(),
                test.createJavaProcessDefinition()
                        .addJvmOption("-Xlog:cds=debug -Xshare:on -XX:SharedArchiveFile=" + tmpDir.getPath() + "/app-cds.jsa")
                        .addJarToClasspath(springPetclinicJar)
                        .runClass("org.springframework.boot.loader.JarLauncher")
                        .addArg("org.springframework.samples.petclinic.PetClinicApplication"));

        test.doSleep("sleep", "10");
        test.doFindFileMatches("Assert Started in 10 seconds", AppCDSRunner.getStdoutFileRef(), "Started PetClinicApplication");
        test.doKillProcesses("stop r3", AppCDSRunner);
    }

    @Override
    public void tearDown(StfCoreExtension test) throws Exception {
    }
}
