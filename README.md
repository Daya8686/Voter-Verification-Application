# 🗳️ Voter Verification Application 🎉

Welcome to the **Voter Verification Application**! This robust Spring Boot project ensures secure voter authentication using **biometric verification** with a **SecuGen Fingerprint Scanner**. It prevents vote rigging, manages voter data efficiently, and provides real-time streaming via a microservice. 🚀

## ✨ Features

- **🔒 Biometric Authentication**: Uses SecuGen Fingerprint Scanner with FDxSDKPro for secure voter verification.
- **🛡️ Vote Rigging Prevention**: Detects duplicate votes and displays details like name, father's name, and address. 🚫
- **📊 Data Management**: Imports voter info from Excel and stores it locally or in AWS using **Spring Batch**. ☁️
- **📈 Excel Export**: Generates voter data reports in Excel format with Spring Batch. 📄
- **🔐 Spring Security**: Implements authentication and authorization for voter in-charge personnel. 🛡️
- **🔄 Retry Mechanism**: Handles fingerprint issues (tight press, light press, dusty fingers) with retries. 🖐️
- **🌐 Microservice Streaming**: A separate microservice broadcasts voter data in real-time. 📡

## 🛠️ Prerequisites

To run this application, ensure:

- **☕ Java JDK 17+** (`java -version` to check).
- **🧰 Maven** for dependency management and builds.
- **🖐️ SecuGen Fingerprint Scanner** with drivers installed.
- **📚 FDxSDKPro.jar** included in the project.

## 🚀 Setup Instructions

### 1. Clone the Repository 📥

```bash
git clone https://github.com/your-username/voter-verification-application.git
cd voter-verification-application
```

### 2. Configure Dependencies ⚙️

Add the following to `pom.xml` to include `FDxSDKPro.jar` and other dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>com.secugen</groupId>
        <artifactId>FDxSDKPro</artifactId>
        <scope>system</scope>
        <version>1.0</version>
        <systemPath>${project.basedir}/src/main/resources/lib/FDxSDKPro.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.batch</groupId>
        <artifactId>spring-batch-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <includeSystemScope>true</includeSystemScope>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Place `FDxSDKPro.jar` in `src/main/resources/lib/`.

### 3. Rebuild the Application 🛠️

Rebuild the JAR file after code changes:

1. **Open Command Prompt as Administrator** 🖥️:
   ```bash
   cd D:\Spring Boot Practice\VoterVerificationApplication\VoterVerificationApplication
   ```

2. **Delete Old Build Files** 🗑️:
   ```bash
   rd /s /q target
   ```

3. **Rebuild the JAR** 📦:
   ```bash
   mvn clean package
   ```
   Check `target/voter-verification-app-0.0.1-SNAPSHOT.jar`.

4. **Verify FDxSDKPro.jar** ✅:
   ```bash
   jar tf target/voter-verification-app-0.0.1-SNAPSHOT.jar | findstr FDxSDKPro
   ```
   Expected: `BOOT-INF/lib/FDxSDKPro-1.0.jar`.

5. **Run the Application** 🚀:
   Use `run_application.bat` or:
   ```bash
   java -jar target/voter-verification-app-0.0.1-SNAPSHOT.jar
   ```

### 4. Create a `.bat` File for Easy Execution 📜

Create `run_application.bat` in the project root:

```bat
@echo off
cd /d "D:\Spring Boot Practice\VoterVerificationApplication\VoterVerificationApplication\target"
java -jar voter-verification-app-0.0.1-SNAPSHOT.jar
pause
```

- Right-click and **Run as Administrator** to start the app. 🎯

## 🌍 Running on Another Machine

To deploy on a different machine:

1. Ensure:
   - **Java JDK 17+** installed (`java -version`).
   - **SecuGen drivers** installed.
   - `FDxSDKPro.jar` included in the JAR.

2. Copy `target/voter-verification-app-0.0.1-SNAPSHOT.jar` to the new machine.
3. Run:
   ```bash
   java -jar voter-verification-app-0.0.1-SNAPSHOT.jar
   ```
4. If it fails, verify `java.exe` is in the system PATH.

## 🐛 Common Issues & Fixes

### 1. **ClassNotFoundException for FDxSDKPro.jar** ❌
   - **Fix**: Check inclusion:
     ```bash
     jar tf target/voter-verification-app-0.0.1-SNAPSHOT.jar | findstr FDxSDKPro
     ```
     If missing, update `pom.xml` and rebuild.

### 2. **Fingerprint Scanner Not Working** 🖐️
   - **Fix**:
     - Run as Administrator.
     - Ensure drivers are installed.
     - Test with SecuGen’s SDK app.

### 3. **Error 6 (SGFDX_ERROR_DEVICE_NOT_FOUND)** ⚠️
   - **Fix**:
     - Run as Administrator.
     - Check scanner connection.

## 📝 Summary & Best Practices

- **After Code Changes** 🔄:
  1. Delete `target/` (`rd /s /q target`).
  2. Rebuild (`mvn clean package`).
  3. Verify `FDxSDKPro.jar` in `BOOT-INF/lib/`.
  4. Run with `run_application.bat`.

- **Deploying Elsewhere** 🌐:
  - Install Java JDK 17+ and scanner drivers.
  - Copy and run the JAR.

## 🤝 Contributing

Contributions are welcome! Fork the repo, make changes, and submit a pull request. 🌟

## 📜 License

This project is licensed under the MIT License.

---

🎉 **Secure Voting, Made Simple!** 🗳️  
🚀 **Happy Coding!** 😊
