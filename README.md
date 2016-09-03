[![Build Status](https://travis-ci.org/BatchIQ/nifi-executescript-samples.svg)](https://travis-ci.org/BatchIQ/nifi-executescript-samples)

# NiFi ExecuteScript Samples
This repo contains samples scripts for use with Apache NiFi's ExecuteScript processor.
Additionally, the repo may be cloned and modified to unit test custom scripts using NiFi's mock framework.

## Sample Scripts
Scripts are designed to demonstrate basic techniques in various languages:

| Topic | Javascript | Python |
| :--- | :--- | :--- |
| Reading and writing flowfile attributes | [attributes.js](tree/master/src/test/resources/javascript/attributes.js) | [attributes.py](tree/master/src/test/resources/python/attributes.py) |
| Logging | [log.js](tree/master/src/test/resources/javascript/log.js) | [log.py](tree/master/src/test/resources/python/log.py) |
| Transforming an input flowfile to a single output | [transform.js](tree/master/src/test/resources/javascript/transform.js) | [transform.py](tree/master/src/test/resources/python/transform.py) |
| Splitting an input flowfile to multiple outputs | [split.js](tree/master/src/test/resources/javascript/split.js) | [split.py](tree/master/src/test/resources/python/split.py) |

## Contributing
Please help.  These sample scripts are very likely to be buggy, unnecessarily complicated, misguided, downright stupid, or all of the above.
Please open an issue for bugs and new contributions.

## License
Apache License 2.0
