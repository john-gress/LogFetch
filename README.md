# **LogFetch**

LogFetch is a lightweight Spring Boot service that provides an on-demand REST API for monitoring Unix-based server logs. It enables efficient retrieval of log entries with filtering and paging.

---

## **Features**
- ✅ Retrieve logs from `/var/log` in reverse order (newest first).
- ✅ Supports pagination using file offsets for efficient reads. Use `offset` query parameter.
- ✅ Keyword-based filtering via a `filter` query parameter.
- ✅ Handles large log files (>1GB) efficiently using the `FileChannel` java library.
- ✅ Deployable as a **systemd** service for production use.

---

## **API Usage**
### **Get Logs**  
Retrieves logs from a specified file.  

#### **Request**  
```http
GET /logs?logFile=system.log&limit=10&offset=0&filter=Error
```

| Parameter  | Type   | Required | Description |
|------------|--------|----------|-------------|
| `logFile`  | String | ✅ Yes   | Log filename (e.g., `system.log`). Must be in `/var/log`. |
| `limit`    | Int    | ❌ No    | Number of log entries to fetch (default: `10`, max: `10000`). |
| `offset`   | Long   | ❌ No    | File offset for pagination (default: `0`). |
| `filter`   | String | ❌ No    | Filters logs containing this keyword. If empty, all logs are returned. |

#### **Response**
```json
{
  "logs": [
    "Error: Disk space low",
    "Error: Connection lost"
  ],
  "nextOffset": 123456
}
```

> #### NOTE: `nextOffset = -1` when file has been completely processed (beginning of file reached).

---

## **Installation & Deployment**

### **1. Clone the Repository**
```sh
git clone https://github.com/john-gress/LogFetch.git
cd LogFetch
```

### **2. Build the Application**
```sh
mvn clean package
```

### **3. Run the Application
```sh
java -jar target/logfetch-0.0.1-SNAPSHOT.jar
```

### **4. Manual Testing
```sh
curl "http://localhost:8080/logs?logFile=system.log"
```

---

## **Deploy as a Systemd Service**
### **1. Move JAR to /opt/logfetch/**
```sh
sudo mkdir -p /opt/logfetch
sudo cp target/logfetch-0.0.1-SNAPSHOT.jar /opt/logfetch/logfetch.jar
```

### **2. Set File Permissions**
```sh
sudo chown -R nobody:nogroup /opt/logfetch
sudo chmod 755 /opt/logfetch/logfetch.jar
```

### **3. Create a logfetch.service file**
#### *Create a systemd servie file `/etc/systemd/system/logfetch.service`
```ini
[Unit]
Description=LogFetch Service
After=network.target

[Service]
ExecStart=/usr/bin/java -jar /opt/logfetch/logfetch.jar
WorkingDirectory=/opt/logfetch
Restart=always
User=nobody
Group=nogroup

[Install]
WantedBy=multi-user.target
```

### **4. Enable and Start Service**
```sh
sudo systemctl daemon-reload
sudo systemctl enable logfetch
sudo systemctl start logfetch
```
