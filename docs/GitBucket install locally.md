# 🚀 GitBucket Local Installation Guide

This guide explains how to install and run **GitBucket** locally on a Windows machine.

---

## 🧰 Step 1: Install Java (Prerequisite)

GitBucket requires Java (JDK 8 or higher).

### 🔹 Download JDK:

Visit: https://adoptium.net/

### 🔹 Verify Installation:

Open Command Prompt and run:

```bash
java -version
```

You should see the installed Java version.

---

## 📥 Step 2: Download GitBucket

### 🔹 Download:

Go to:
https://github.com/gitbucket/gitbucket/releases

Download the latest file:

```
gitbucket.war
```

### 🔹 Save Location:

Example:

```
C:\gitbucket\
```

---

## ▶️ Step 3: Run GitBucket

Open Command Prompt:

```bash
cd C:\gitbucket
java -jar gitbucket.war
```

Wait for the server to start.

---

## 🌐 Step 4: Access GitBucket

Open your browser and go to:

```
http://localhost:8080
```

---

## 🔐 Step 5: Login

Default credentials:

* **Username:** root
* **Password:** root

⚠️ Change the password after first login.

---

## 📁 Step 6: Create a Repository

1. Click **Create Repository**
2. Enter repository name (e.g., `my-project`)
3. Click **Create**

---

## 🔗 Step 7: Clone Repository Locally

Example:

```bash
git clone http://localhost:8080/root/my-project.git
```

---

## 🏢 Step 8: Deploy GitBucket on Tomcat / JBoss (Alternative to Standalone)

`gitbucket.war` is a standard Java WAR file, so instead of running it standalone with `java -jar`, you can deploy it into a servlet container like **Apache Tomcat** or **JBoss/WildFly**.

### 🔹 Option A: Deploy on Apache Tomcat

1. **Download and extract Tomcat** (version 9.x recommended, since GitBucket needs Servlet API 3.1+).

   * Go to https://tomcat.apache.org/
   * Under **Tomcat 9**, download the **Windows zip (32-bit/64-bit)** archive — no installer is required.
   * Extract it to a simple path, e.g. `C:\tomcat9`. This folder is what the rest of the steps refer to as `<TOMCAT_HOME>`.

2. **Tell Tomcat where GitBucket should store its data** (repositories, database, config).

   By default GitBucket stores everything under the logged-in user's home directory (`%HOME%\.gitbucket`), which is often not what you want on a server. You override this with the `gitbucket.home` Java system property, passed to Tomcat at startup.

   Tomcat doesn't have a config file for JVM options out of the box — it looks for an optional script called `setenv.bat` in its `bin` folder and runs it automatically every time it starts. So:

   * Go to `<TOMCAT_HOME>\bin\`
   * If `setenv.bat` doesn't exist, create a new text file there with that exact name.
   * Add this line to it:

     ```bat
     set CATALINA_OPTS=%CATALINA_OPTS% -Dgitbucket.home=C:\gitbucket\data
     ```

   * Make sure the folder `C:\gitbucket\data` exists (create it if needed) — Tomcat's service account must be able to read/write it.

   This single line means: "whatever other startup options Tomcat already has, keep them, and additionally pass `-Dgitbucket.home=...` to the JVM." Tomcat picks this up the next time it starts.

3. **Copy the WAR file into Tomcat's `webapps` folder** — this is the actual deployment step.

   Tomcat watches its `webapps` folder and automatically deploys (unpacks and starts) any `.war` file it finds there, using the filename as the URL path. So dropping `gitbucket.war` in means Tomcat will serve it at `/gitbucket` without any extra configuration.

   ```
   copy gitbucket.war <TOMCAT_HOME>\webapps\
   ```

   * To serve GitBucket at the root path (`/` instead of `/gitbucket`), rename the file to `ROOT.war` before copying.

4. **Start Tomcat**:

   ```bat
   <TOMCAT_HOME>\bin\startup.bat
   ```

5. **Access GitBucket**:

   ```
   http://localhost:8080/gitbucket
   ```

   (or `http://localhost:8080/` if deployed as `ROOT.war`)

6. Login with the default `root` / `root` credentials as before.

⚠️ **Notes:**

* Tomcat auto-deploys based on the WAR filename, so `gitbucket.war` becomes context path `/gitbucket`. Rename the file if you need a different path.
* GitBucket requires Servlet API 3.1+, so use Tomcat 8.5+ or 9.x.

#### 🔄 If port 8080 is already in use

Unlike the standalone `--port` flag, Tomcat's port isn't set via a command-line option — it's fixed in a config file, because Tomcat itself (not GitBucket) owns the HTTP connector.

1. Open `<TOMCAT_HOME>\conf\server.xml`.
2. Find the `Connector` element that looks like this:

   ```xml
   <Connector port="8080" protocol="HTTP/1.1"
              connectionTimeout="20000"
              redirectPort="8443" />
   ```

3. Change `port="8080"` to any free port, e.g. `port="9090"`.
4. Save the file and restart Tomcat:

   ```bat
   <TOMCAT_HOME>\bin\shutdown.bat
   <TOMCAT_HOME>\bin\startup.bat
   ```

5. Access GitBucket using the new port, e.g. `http://localhost:9090/gitbucket`.

   Tip: to check whether a port is already occupied before changing it, run `netstat -ano | findstr :8080` in Command Prompt — if a process ID is returned, that port is in use.

---

### 🔹 Option B: Deploy on JBoss / WildFly

1. **Download WildFly / JBoss EAP** from: https://www.wildfly.org/

2. **Set the GitBucket home directory** via JVM options.

   Edit `<JBOSS_HOME>\bin\standalone.conf.bat`, add:

   ```bat
   set "JAVA_OPTS=%JAVA_OPTS% -Dgitbucket.home=C:\gitbucket\data"
   ```

3. **Copy the WAR file** into the deployments folder:

   ```
   copy gitbucket.war <JBOSS_HOME>\standalone\deployments\
   ```

4. **Start the server**:

   ```bat
   <JBOSS_HOME>\bin\standalone.bat -b 0.0.0.0
   ```

5. Wait until you see a `gitbucket.war.deployed` marker file appear in the `deployments` folder — this confirms a successful deployment.

6. **Access GitBucket**:

   ```
   http://localhost:8080/gitbucket
   ```

7. Login with the default `root` / `root` credentials as before.

⚠️ **Notes:**

* If deployment fails, check `<JBOSS_HOME>\standalone\log\server.log` for errors — a common cause is the `gitbucket.home` directory not being writable.
* Rename the WAR to `ROOT.war` to deploy it at the root context (`/`).

#### 🔄 If port 8080 is already in use

JBoss/WildFly builds its port from a **base port + offset** defined in `standalone.xml`, so you have two options:

**Option 1 — quick override at startup (no file editing):**

```bat
<JBOSS_HOME>\bin\standalone.bat -b 0.0.0.0 -Djboss.socket.binding.port-offset=100
```

This shifts every default port up by 100 — e.g. HTTP becomes `8180` instead of `8080`. Access GitBucket at `http://localhost:8180/gitbucket`.

**Option 2 — permanent change in config:**

1. Open `<JBOSS_HOME>\standalone\configuration\standalone.xml`.
2. Find the `socket-binding-group` section and locate:

   ```xml
   <socket-binding name="http" port="${jboss.http.port:8080}"/>
   ```

3. Change the default, e.g.:

   ```xml
   <socket-binding name="http" port="${jboss.http.port:9090}"/>
   ```

4. Save the file and restart the server.

   Tip: run `netstat -ano | findstr :8080` in Command Prompt first to confirm whether the port is actually occupied and by which process.

---

## ⚙️ Optional Configurations

### 🔹 Change Port:

```bash
java -jar gitbucket.war --port=9090
```

---

### 🔹 Set Data Directory:

```bash
java -jar gitbucket.war --gitbucket.home=C:\gitbucket\data
```

---

## ⚠️ Troubleshooting

### ❌ Port Already in Use

Use a different port:

```bash
--port=9090
```

---

### ❌ Java Not Recognized

* Ensure Java is installed
* Set `JAVA_HOME` environment variable

---

## ✅ Summary

GitBucket provides:

* GitHub-like UI
* Local Git hosting
* Repository management
* Pull requests and issues

---

## 💡 Recommendation

If you need a more modern and faster alternative, consider:

* **Gitea**

---

## 🎉 You're Ready!

You now have your own local Git hosting platform running 🚀
