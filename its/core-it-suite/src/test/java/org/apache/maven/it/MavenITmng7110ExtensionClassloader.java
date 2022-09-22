package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.maven.shared.verifier.util.ResourceExtractor;
import org.apache.maven.shared.verifier.Verifier;
import org.apache.maven.shared.verifier.VerificationException;

public class MavenITmng7110ExtensionClassloader
        extends AbstractMavenIntegrationTestCase
{
    public MavenITmng7110ExtensionClassloader()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    public void testVerifyResourceOfExtensionAndDependency() throws IOException, VerificationException
    {
        final File projectDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-7110-extensionclassloader" );
        
        final Verifier extensionVerifier = newVerifier( new File( projectDir, "extension" ).getAbsolutePath() );
        extensionVerifier.executeGoal( "install" );
        extensionVerifier.verifyErrorFreeLog();

        final Verifier libVerifier = newVerifier( new File( projectDir, "lib" ).getAbsolutePath() );
        libVerifier.executeGoal( "install" );
        libVerifier.verifyErrorFreeLog();

        final Verifier bomVerifier = newVerifier( new File( projectDir, "bom" ).getAbsolutePath() );
        bomVerifier.executeGoal( "install" );
        bomVerifier.verifyErrorFreeLog();
        
        final Verifier projectVerifier = newVerifier( new File( projectDir, "module" ).getAbsolutePath() );
        projectVerifier.executeGoal( "verify" );
        projectVerifier.verifyErrorFreeLog();
        
        Properties properties = new Properties();
        Reader fileReader = new FileReader( new File( projectDir, "module/out.txt" ) );
        properties.load( fileReader );
        
        assertEquals( "1", properties.getProperty( "extension.txt.count", "-1" ) );
        assertEquals( "1", properties.getProperty( "lib.txt.count", "-1" ) );
    }

}
