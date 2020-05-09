/*
 *  (C) Copyright 2017 TheOtherP (theotherp@posteo.net)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.nzbhydra.github.mavenreleaseplugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
@Mojo(name = "generate-wrapper-hashes",
        inheritByDefault = false,
        aggregator = true //Only call for parent POM
)
public class WrapperHashesGeneratorMojo extends AbstractMojo {

    @Parameter(property = "wrapperFile1", required = true)
    protected File wrapperFile1;
    @Parameter(property = "wrapperFile2", required = true)
    protected File wrapperFile2;
    @Parameter(property = "wrapperFile3", required = true)
    protected File wrapperFile3;
    @Parameter(property = "wrapperFile4", required = true)
    protected File wrapperFile4;
    @Parameter(property = "wrapperFile5", required = true)
    protected File wrapperFile5;
    @Parameter(property = "wrapperHashesJsonFile", required = true)
    protected File wrapperHashesJsonFile;


    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute() throws MojoExecutionException {

        checkWrapperFilesExist(wrapperFile1);
        checkWrapperFilesExist(wrapperFile2);
        checkWrapperFilesExist(wrapperFile3);
        checkWrapperFilesExist(wrapperFile4);
        checkWrapperFilesExist(wrapperFile5);
        getLog().info("Will write hashes to " + wrapperHashesJsonFile.getAbsolutePath());

        Map<String, String> filenamesToHashCodes = new HashMap<>();
        try {
            hashFile(filenamesToHashCodes, wrapperFile1);
            hashFile(filenamesToHashCodes, wrapperFile2);
            hashFile(filenamesToHashCodes, wrapperFile3);
            hashFile(filenamesToHashCodes, wrapperFile4);
            hashFile(filenamesToHashCodes, wrapperFile5);
            Files.write(objectMapper.writeValueAsBytes(filenamesToHashCodes), wrapperHashesJsonFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while hashing wrapper file", e);
        }

    }

    private void hashFile(Map<String, String> filenamesToHashCodes, File file) throws IOException {
        HashCode hashCode = Files.asByteSource(file).hash(Hashing.crc32());
        filenamesToHashCodes.put(file.getName(), hashCode.toString());
    }

    private void checkWrapperFilesExist(File wrapperFile1) throws MojoExecutionException {
        if (!wrapperFile1.exists()) {
            throw new MojoExecutionException("Wrapper file does not exist: " + wrapperFile1.getAbsolutePath());
        }
    }


}
