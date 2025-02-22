# Fudge Project

The Fudge Project is a utility designed to simplify the process of generating and managing documentation for your feature tests. It specifically supports
building feature documentation from JGiven JSON outputs while creating Markdown files that can be directly used in tools like **Docusaurus**. Additionally, the
project provides a custom **Maven plugin** to integrate this functionality seamlessly into your build process.

## Main Features

1. **Generate Feature Documentation from JGiven JSON Output**:
   The project can parse test results (in JSON format) produced by JGiven and transform them into human-readable and developer-friendly Markdown documentation.
2. **Create Markdown Files for Docusaurus**:
   Feature documentation is output as Markdown files, structured and formatted to integrate directly with **Docusaurus**, making it easier to maintain and
   deploy documentation to your static site.
3. **Maven Plugin**:
   Includes a custom **Fudge Maven Plugin** that allows the feature processing and Markdown generation to be easily configured and executed through your
   project's build process.

## How to Use

### 1. Add the Fudge Maven Plugin to Your Project

To use the Fudge Maven Plugin in your project, add the following plugin configuration to your `pom.xml` file:

``` xml
<build>
    <plugins>
        <plugin>
            <groupId>com.lolplane.fudge</groupId>
            <artifactId>fudge-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-docs</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <jgivenOutputDirectory>${project.basedir}/target/jgiven-reports/json</jgivenOutputDirectory>
                <fudgeOutputDirectory>${project.basedir}/docs</fudgeOutputDirectory>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. Plugin Configuration

Here are the parameters supported by the **fudge-maven-plugin**, along with their descriptions:

| Parameter               | Description                                                      | Default Value                                   |
|-------------------------|------------------------------------------------------------------|-------------------------------------------------|
| `jgivenOutputDirectory` | Path to the directory containing the JGiven JSON output.         | `${project.build.directory/jgiven-reports/json` |
| `fudgeOutputDirectory`  | Path where the generated Markdown documentation will be written. | `${project.build.directory}/site/fudge`         |
| `verboseMode`           | If enabled, more fine-grained logs will be written.              | `false`                                         |
| `dryMode`               | If enabled, no data is written to the file system.               | `false`                                         |

### 3. Generate Documentation

To generate feature documentation, run the Maven `generate-docs` goal:

``` bash
mvn com.lolplane.fudge:fudge-maven-plugin:generate-docs
```

This process will take your JGiven JSON outputs, parse them, and generate Markdown files in the specified `docsOutputDir`.

### 4. View Markdown Output

The generated Markdown files will follow a structured format that is compatible with **Docusaurus**. You can directly integrate these files as pages or content
into a Docusaurus project.

## Example Workflow with JGiven and Docusaurus

1. **Set Up JGiven**:
   Ensure your tests are written using JGiven and generate JSON output reports as part of your test workflow (e.g., using Maven).
2. **Integrate Fudge Maven Plugin**:
   Add the plugin to your `pom.xml` as described above, ensuring the `jgivenOutputDirectory` points to the directory containing your JGiven JSON reports.
3. **Generate Markdown Files**:
   Run the Maven plugin to convert your test results into Markdown files.
4. **Deploy with Docusaurus**:
    - Copy the generated Markdown files from the `fudgeOutputDirectory` to your Docusaurus `docs` folder or specify `fudgeOutputDirectory` to be the `docs`
      folder in your Docusaurus project.
    - Add references to the Markdown files in your Docusaurus site configuration. (optional)

## Example Directory Structure

After generating Markdown documentation, your project directory might look like this:

``` 
project-root/
│
├── target/
│   ├── jgiven-reports/
│       ├── json/
│           ├── feature1.json
│           ├── feature2.json
│
├── docs/
│   ├── Feature1.md
│   ├── Feature2.md
│
├── pom.xml
```

Here:

- `target/jgiven-reports/json`: Directory containing the raw JGiven JSON files.
- `docs`: Directory containing the generated Markdown files.

## Building and Testing Fudge

### Requirements:

- **JDK 17** or higher
- **Apache Maven**
- **Docusaurus** (optional for testing integration)

### Steps to Build and Test:

1. **Building the Project**:

``` bash
   mvn clean install
```

2. **Run Unit Tests**:

``` bash
   mvn test
```

3. **Use Locally Built Plugin**:
   If testing locally, reference the plugin with its version in the project's `pom.xml` (use the `SNAPSHOT` version if applicable).

## Contributing

Contributions are welcome! Whether it’s reporting issues, suggesting improvements, or submitting pull requests, your input is valuable to the project.

### Development Guidelines:

- Follow standard Maven project structure.
- Write unit tests for new functionality.
- Ensure code is well-documented and adheres to Java language standards.

## License

This project is licensed under the **Apache 2.0 License**. See the [LICENSE](LICENSE) file for details.
