Go to Oracle's [Java download page](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and download the Java Cryptography Extension (JCE) unlimited strength jurisdiction policy files 6 zipfile to this directory.

    cd /etc/puppet/modules/cloudera/files
    curl -f --retry 10 http://download.oracle.com/otn-pub/java/jce_policy/6/jce_policy-6.zip -o jce_policy-6.zip
    unzip jce_policy-6.zip

You should have the following structure:

    $ tree files/
    files/
    |-- java.sh
    |-- jce
    |   |-- COPYRIGHT.html
    |   |-- local_policy.jar
    |   |-- README.txt
    |   `-- US_export_policy.jar
    |-- jce_policy-6.zip
    `-- README
    
    1 directory, 7 files


