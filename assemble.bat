@ECHO OFF

mvn compile assembly:single
echo apache-maven-3.2.3\bin\assemble done
copy .\target\forums_db-1.0-jar-with-dependencies.jar .\
echo copy done
pause