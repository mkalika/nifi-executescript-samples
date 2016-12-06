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
# Working with State values in ExecuteScript
#
# Variables provided in scope by script engine:
#
#    session - ProcessSession
#    context - ProcessContext
#    log - ComponentLog
#    REL_SUCCESS - Relationship
#    REL_FAILURE - Relationship
###############################################################################

from org.apache.nifi.components.state import Scope
from java.util import HashMap

flowFile = session.get()
if flowFile != None:

    # Get current state
    oldState = context.getStateManager().getState(Scope.CLUSTER)
    stateMessage = oldState.get('some-state')

    # Set new state
    newState = HashMap()
    newState.put('some-state', stateMessage + 'bar')
    context.getStateManager().setState(newState, Scope.CLUSTER)

    session.transfer(flowFile, REL_SUCCESS)
