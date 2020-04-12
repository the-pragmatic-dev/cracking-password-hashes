# A Beginners Guide on Cracking Password Hashes

Given a reasonable assumption on how people create passwords, we demonstrate cracking a set of SHA-256 hashed passwords through a combination of dictionary and brute force attacks.

A full write up of this application can be found over on Medium: https://medium.com/@steve_99356/a-beginners-guide-on-cracking-password-hashes-c7212e199eb2.

Commands to build and run the application are as follow:

```bash
./mvnw clean package

./mvnw exec:java -Dexec.args="-i data/hashes.txt -o data/output.txt -d data/dictionary"
```
