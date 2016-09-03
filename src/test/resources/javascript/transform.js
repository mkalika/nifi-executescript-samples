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

/******************************************************************************
 * Transform FlowFile Script (for Java 8)
 * Adapted from http://funnifi.blogspot.com/2016/03/executescript-json-to-json-revisited.html
 *
 * Variables provided in scope by script engine:
 *
 *     session - ProcessSession
 *     context - ProcessContext
 *     log - ComponentLog
 *     REL_SUCCESS - Relationship
 *     REL_FAILURE - Relationship
 *
 */
var flowFile = session.get();

if (flowFile !== null) {

    var StreamCallback = Java.type("org.apache.nifi.processor.io.StreamCallback");
    var IOUtils = Java.type("org.apache.commons.io.IOUtils");
    var StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

    flowFile = session.write(flowFile, new StreamCallback(function(inputStream, outputStream) {
        // Read input FlowFile content
        var inputText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        var inputObj = JSON.parse(inputText);

        // Transform content
        var outputObj = inputObj;
        outputObj.value = outputObj.value * outputObj.value;
        outputObj.message = "Hello";

        // Write output content
        outputStream.write(JSON.stringify(outputObj, null, "\t").getBytes(StandardCharsets.UTF_8));
    }));

    // Finish by transferring the FlowFile to an output relationship
    session.transfer(flowFile, REL_SUCCESS);
}
