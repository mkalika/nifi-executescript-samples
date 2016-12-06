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
 * Split FlowFile Script (for Java 8)
 * Adapted from http://funnifi.blogspot.com/2016/03/executescript-json-to-json-revisited.html
 *
 * Variables provided in scope by script engine:
 *
 *     session - ProcessSession
 *     context - ProcessContext
 *     log - ComponentLog
 *     REL_SUCCESS - Relationship
 *     REL_FAILURE - Relationship
 */

var parentFlowFile = session.get();

if (parentFlowFile !== null) {

    var InputStreamCallback = Java.type("org.apache.nifi.processor.io.InputStreamCallback");
    var OutputStreamCallback = Java.type("org.apache.nifi.processor.io.OutputStreamCallback");
    var IOUtils = Java.type("org.apache.commons.io.IOUtils");
    var StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

    session.read(parentFlowFile, new InputStreamCallback(function(inputStream) {
        // Read input FlowFile content
        var inputText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        var inputArray = JSON.parse(inputText);

        // Create FlowFiles for array items
        var splits = [];
        for (var i = 0; i < inputArray.length; i++) {
            var item = inputArray[i];
            log.info(item.color);
            var splitFlowFile = session.create(parentFlowFile);
            splitFlowFile = session.write(splitFlowFile, new OutputStreamCallback(function(outputStream) {
                outputStream.write(JSON.stringify(item, null, "\t").getBytes(StandardCharsets.UTF_8));
            }));
            splitFlowFile = session.putAllAttributes(splitFlowFile, {
                "fragment.index": i.toString(),
                "color": item.color
            });
            splits = splits.concat(splitFlowFile);
        }
        splits.forEach(function (splitFlowFile) {
            session.transfer(splitFlowFile, REL_SUCCESS);
        });
    }));

    session.remove(parentFlowFile);
}
