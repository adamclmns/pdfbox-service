# Run this project

### On Windows
open a command prompt in the project root (same directory as pom.xml)
```bat
./mvnw spring-boot:run
```
Navigate to "http://localhost:8000/pdf" to request a new PDF.

Navigate to "http://localhost/version" for a basic health-check 



# What even is this?
This project is a demo for generating PDF's in memory and returning them to the browser as a download. 

This project uses ApachePDFBox and Boxable