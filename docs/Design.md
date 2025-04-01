# **LogFetch Design Discussion**

### LogFetch - design/development considerations, tradeoffs, assumptions, and future work

---

## Language and framework considerations:
1. Python with Flask
2. Node.js
3. Java (Spring Boot)

While Python could be the fastest way to develop a solution, it would not necessarily be the best for a production
environment. For Node, I'm not super familiar with the framework, so it would be difficult to complete the project in a
week. On the third hand, Java is something I'm familiar with and is a robust solution frequently used for production
software, although, it is more heavyweight. As I plan to do development with Intellij, the setup is familiar and largely
automated. I will start with Java and see how it goes.


## Deployment options:
1. Docker Container
2. Systemd Service

A docker container would provide the best portability, but could limit our access to the /var/log files on the host
system. It would depend on restrictions (or lack thereof) on the host system. The Systemd service is a slightly more
complicated deployment and would require some thought for the rpm or deb package. As a Systemd service, access to
/var/log will not be an issue. Systemd seems to be required.

## Assumptions
* Assumption is logs are UTF-8? Should there be support for internationalization?

---

## Future work
* Fix error response for Spring Boot validation. The return code is correct, but no error message is ugly.
* Add security (API keys or JWT).
* Add logging.
* Discuss need for https.
* Add help endpoint, improve response for /logs endpoint when no parameters are provided.
* Discuss need for error checking the filter input.
* Support wildcards for filtering, consider regex support.
* Handle single log line greater than LogReader.MAX_READ_SIZE.
* Also need to think about the return size of a large number of large logs.
* Determine optimal MAX_READ_SIZE.
* Add data compression to response.