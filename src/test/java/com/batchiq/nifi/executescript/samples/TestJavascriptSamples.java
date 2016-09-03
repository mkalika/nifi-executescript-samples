/*
 * Copyright 2016 BatchIQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.batchiq.nifi.executescript.samples;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.nifi.processors.script.ExecuteScript;
import org.apache.nifi.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestJavascriptSamples extends BaseScriptTest {

    @Before
    public void setup() throws Exception {
        super.setupExecuteScript();
    }

    /**
     * Demonstrates logging from within scripts
     * @throws Exception
     */
    @Test
    public void testAttributes() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ExecuteScript());
        runner.setValidateExpressionUsage(false);
        runner.setProperty(ExecuteScript.SCRIPT_ENGINE, "ECMAScript");
        runner.setProperty(ExecuteScript.SCRIPT_FILE, "target/test/resources/javascript/attributes.js");
        runner.setProperty(ExecuteScript.MODULES, "target/test/resources/javascript");
        runner.assertValid();

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("greeting", "Hello");
        runner.enqueue("nothing".getBytes(StandardCharsets.UTF_8), attributes);
        runner.run();

        runner.assertAllFlowFilesTransferred("success", 1);
        final List<MockFlowFile> successFlowFiles = runner.getFlowFilesForRelationship("success");
        MockFlowFile result = successFlowFiles.get(0);
        result.assertAttributeEquals("message", "Hello, Script!");
        result.assertAttributeEquals("attribute.one", "true");
        result.assertAttributeEquals("attribute.two", "2");
    }

    /**
     * Demonstrates logging from within scripts
     * @throws Exception
     */
    @Test
    public void testLog() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ExecuteScript());
        runner.setValidateExpressionUsage(false);
        runner.setProperty(ExecuteScript.SCRIPT_ENGINE, "ECMAScript");
        runner.setProperty(ExecuteScript.SCRIPT_FILE, "target/test/resources/javascript/log.js");
        runner.setProperty(ExecuteScript.MODULES, "target/test/resources/javascript");
        runner.assertValid();

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("greeting", "Hello");
        runner.enqueue("sample text".getBytes(StandardCharsets.UTF_8), attributes);
        runner.run();

        runner.assertAllFlowFilesTransferred("success", 1);
        final List<MockFlowFile> successFlowFiles = runner.getFlowFilesForRelationship("success");
        MockFlowFile result = successFlowFiles.get(0);

        MockComponentLog log = runner.getLogger();
        List<LogMessage> infoMessages = log.getInfoMessages();
        Assert.assertEquals(1, infoMessages.size());
        Assert.assertTrue(infoMessages.get(0).getMsg().contains("Hello, Info"));

        List<LogMessage> debugMessages = log.getDebugMessages();
        Assert.assertEquals(1, debugMessages.size());
        Assert.assertTrue(debugMessages.get(0).getMsg().contains("Hello, Debug"));
    }


    private static class InputObject {
        public int value;
    }

    private static class OutputObject {
        public int value;
        public String message;
    }

    /**
     * Demonstrates transforming the JSON object in an incoming FlowFile to output
     * @throws Exception
     */
    @Test
    public void testTransform() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ExecuteScript());
        runner.setValidateExpressionUsage(false);
        runner.setProperty(ExecuteScript.SCRIPT_ENGINE, "ECMAScript");
        runner.setProperty(ExecuteScript.SCRIPT_FILE, "target/test/resources/javascript/transform.js");
        runner.setProperty(ExecuteScript.MODULES, "target/test/resources/javascript");
        runner.assertValid();

        InputObject inputJsonObject = new InputObject();
        inputJsonObject.value = 3;
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonBytes = mapper.writeValueAsBytes(inputJsonObject);

        runner.enqueue(jsonBytes);
        runner.run();

        runner.assertAllFlowFilesTransferred("success", 1);
        final List<MockFlowFile> successFlowFiles = runner.getFlowFilesForRelationship("success");
        MockFlowFile result = successFlowFiles.get(0);
        byte[] flowFileBytes = result.toByteArray();

        OutputObject outputJsonObject = mapper.readValue(flowFileBytes, OutputObject.class);
        Assert.assertEquals(9, outputJsonObject.value);
        Assert.assertEquals("Hello", outputJsonObject.message);
    }


    /**
     * Demonstrates splitting an array in a single incoming FlowFile into multiple output FlowFiles
     * @throws Exception
     */
    @Test
    public void testSplit() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ExecuteScript());
        runner.setValidateExpressionUsage(false);
        runner.setProperty(ExecuteScript.SCRIPT_ENGINE, "ECMAScript");
        runner.setProperty(ExecuteScript.SCRIPT_FILE, "target/test/resources/javascript/split.js");
        runner.setProperty(ExecuteScript.MODULES, "target/test/resources/javascript");
        runner.assertValid();

        String inputContent = "[";
        inputContent += "{ \"color\": \"blue\" },";
        inputContent += "{ \"color\": \"green\" },";
        inputContent += "{ \"color\": \"red\" }";
        inputContent += "]";
        runner.enqueue(inputContent.getBytes(StandardCharsets.UTF_8));
        runner.run();

        MockComponentLog log = runner.getLogger();
        List<LogMessage> infoMessages = log.getInfoMessages();

        runner.assertAllFlowFilesTransferred("success", 3);
        final List<MockFlowFile> successFlowFiles = runner.getFlowFilesForRelationship("success");
        MockFlowFile blueFlowFile = successFlowFiles.get(0);
        blueFlowFile.assertAttributeEquals("color", "blue");
        MockFlowFile greenFlowFile = successFlowFiles.get(1);
        greenFlowFile.assertAttributeEquals("color", "green");
        MockFlowFile redFlowFile = successFlowFiles.get(2);
        redFlowFile.assertAttributeEquals("color", "red");
    }

}
