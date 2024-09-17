# keen-clinic-desktop
The Desktop Version of Keen Clinic

`jdeps Keen_Clinic.jar`

`jlink --add-modules java.base,java.net.http,java.scripting,java.sql,java.logging,java.desktop,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom --output jdk-21-mini-jre --strip-debug  --no-man-pages --no-header-files --compress=2`