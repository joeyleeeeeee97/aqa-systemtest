package net.adoptopenjdk.test.auth;

import net.adoptopenjdk.stf.environment.FileRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

public class OAuthTest implements StfPluginInterface {
    FileRef keyStoreFile;


    @Override
    public void help(HelpTextGenerator help) throws Exception {
        help.outputSection("OAuthTest");
        help.outputText("This test demonstrates running a Authorization Code Grant using java security");
    }

    @Override
    public void pluginInit(StfCoreExtension svt) throws Exception {
    }

    @Override
    public void setUp(StfCoreExtension svt) throws Exception {
        keyStoreFile = svt.locateResourceFile("testkeys");
    }

    public void execute(StfCoreExtension test) throws Exception {
        StfProcess oauthCenter = test.doRunBackgroundProcess("Run AuthCenter", "AUTH", ECHO_ON, ExpectedOutcome.neverCompletes(),
                test.createJavaProcessDefinition()
                        .addProjectToClasspath("openjdk.test.security")
                        .runClass(AuthCenter.class));

        StfProcess server = test.doRunBackgroundProcess("Run Server", "SER", ECHO_ON, ExpectedOutcome.neverCompletes(),
                test.createJavaProcessDefinition()
                        .addProjectToClasspath("openjdk.test.security")
                        .runClass(Server.class)
                        .addArg(keyStoreFile.getSpec()));

        StfProcess client = test.doRunBackgroundProcess("Run Client", "CLI", ECHO_ON, ExpectedOutcome.cleanRun().within("10s"),
                test.createJavaProcessDefinition()
                        .addProjectToClasspath("openjdk.test.security")
                        .runClass(Client.class)
                        .addArg(keyStoreFile.getSpec()));

        test.doMonitorProcesses("Wait for clients to complete", client);

        test.doKillProcesses("Stop servers", oauthCenter, server);

    }

    @Override
    public void tearDown(StfCoreExtension svt) throws Exception {

    }
}
