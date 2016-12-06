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
# Demonstrates splitting an incoming flowfile into multiple outputs
#
# Variables provided in scope by script engine:
#
#    session - ProcessSession
#    context - ProcessContext
#    log - ComponentLog
#    REL_SUCCESS - Relationship
#    REL_FAILURE - Relationship
###############################################################################

import json
import sys
import traceback
from java.nio.charset import StandardCharsets
from org.apache.commons.io import IOUtils
from org.apache.nifi.processor.io import InputStreamCallback, OutputStreamCallback
from org.python.core.util import StringUtil


class WriteCallback(OutputStreamCallback):
    def __init__(self):
        self.content = None
        self.charset = StandardCharsets.UTF_8

    def process(self, outputStream):
        bytes = bytearray(self.content.encode('utf-8'))
        outputStream.write(bytes)


class SplitCallback(InputStreamCallback):
    def __init__(self):
        self.parentFlowFile = None

    def process(self, inputStream):
        try:
            # Read input FlowFile content
            input_text = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            input_list = json.loads(input_text)

            # Create FlowFiles for array items
            splits = []
            fragment_index = 0
            for item in input_list:
                color = item['color']
                splitFlowFile = session.create(self.parentFlowFile)
                writeCallback = WriteCallback()
                writeCallback.content = json.dumps(item)
                splitFlowFile = session.write(splitFlowFile, writeCallback)
                splitFlowFile = session.putAllAttributes(splitFlowFile, {
                    'fragment.index': fragment_index,
                    'color': color
                })
                splits.append(splitFlowFile)
                log.info(color)
                fragment_index += 1

            for splitFlowFile in splits:
                session.transfer(splitFlowFile, REL_SUCCESS)
        except:
            traceback.print_exc(file=sys.stdout)
            raise


parentFlowFile = session.get()
if parentFlowFile != None:
    splitCallback = SplitCallback()
    splitCallback.parentFlowFile = parentFlowFile
    session.read(parentFlowFile, splitCallback)
    session.remove(parentFlowFile)
