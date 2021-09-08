package net.adoptopenjdk.test.ssl;

import junit.framework.TestCase;
import org.junit.Assert;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TestEnumSupportedProtocals assert current vm supports:
 * TLSv1.3
 * TLSv1.2
 * TLSv1.1
 * TLSv1
 * SSLv3
 * SSLv2Hello
 */
public class TestEnumSupportedProtocals extends TestCase {
    private static final String[] PROTOCOLS = new String[]{
            "TLSv1.3",
            "TLSv1.2",
            "TLSv1.1",
            "TLSv1",
            "SSLv3",
            "SSLv2Hello"
    };

    public void runTests() throws Exception {
        SSLContext ctx = SSLContext.getDefault();
        SSLParameters params = ctx.getSupportedSSLParameters();
        Set<String> supportedProtocols = Arrays.stream(params.getProtocols()).collect(Collectors.toSet());
        for (String protocol : PROTOCOLS) {
            if (!supportedProtocols.contains(protocol)) {
                assertEquals(protocol, "TLSv1.3");

                String javaVersion = System.getProperty("java.version");
                /**
                 * TLS: version 1.0, 1.1, 1.2, and 1.3 (since JDK 8u261)
                 */
                if (!javaVersion.startsWith("1.8.0") || Integer.parseInt(javaVersion.split("_")[1]) >= 261) {
                    Assert.fail(protocol + " is not supported in " + javaVersion);
                }
            }
        }
    }
}
