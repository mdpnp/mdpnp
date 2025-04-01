@ECHO OFF
SET CPP_JAR=anarres-cpp.jar
SET CPP_ROOT=%~dp0
SET CPP_LIB=%CPP_ROOT%\lib
SET CPP_CLASSPATH=%CPP_LIB%\anarres-cpp.jar;%CPP_LIB%\gnu.getopt.jar
SET CPP_MAINCLASS=org.anarres.cpp.Main
SET CPP_JFLAGS=-Xmx128M

java %CPP_JFLAGS% -cp "%CPP_CLASSPATH%" %CPP_MAINCLASS% %* 
