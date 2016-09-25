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
 * Reading from nifi.properties in ExecuteScript
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

flowFile = session.get();
if (flowFile != null) {

    var NiFiPropertiesLoader = Java.type("org.apache.nifi.properties.NiFiPropertiesLoader");

    // Get property to read
    var propertyName = flowFile.getAttribute("property-name");

    // Get NiFi properties
    var nifiPropertiesLoader = new NiFiPropertiesLoader();
    var nifiProperties = nifiPropertiesLoader.get();

    // Read property value
    var propertyValue = nifiProperties.getProperty(propertyName);
    flowFile = session.putAttribute(flowFile, "property-value", propertyValue);

    session.transfer(flowFile, REL_SUCCESS)
}
