# Copyright 2016 BatchIQ
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

###############################################################################
# Setting flowfile attributes in ExecuteScript
#
# Variables provided in scope by script engine:
#
#    session - ProcessSession
#    context - ProcessContext
#    log - ComponentLog
#    REL_SUCCESS - Relationship
#    REL_FAILURE - Relationship
###############################################################################

flowFile = session.get()
if flowFile != None:
    # Get attributes
    greeting = flowFile.getAttribute("greeting")
    message = greeting + ", Script!"

    # Set single attribute
    flowFile = session.putAttribute(flowFile, "message", message)

    # Set multiple attributes
    flowFile = session.putAllAttributes(flowFile, {
        "attribute.one": "true",
        "attribute.two": "2"
    })

    session.transfer(flowFile, REL_SUCCESS)