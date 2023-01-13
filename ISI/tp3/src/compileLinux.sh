javac -cp ".:./lib/postgresql-42.5.1.jar" Model.java App.java Main.java
java -cp ".:./lib/postgresql-42.5.1.jar" Main
rm *.class