all:

	mkdir -p bin
	javac -Xlint:deprecation -source 1.6 -target 1.6 -d bin/ -sourcepath src/ -classpath bin/ src/Graphwar/*.java src/GraphServer/*.java src/GlobalServer/*.java src/RoomServer/*.java
	cp -r bin/Graphwar Graphwar
	cp -r bin/GraphServer GraphServer
	cp -r bin/GlobalServer GlobalServer
	cp -r bin/RoomServer RoomServer
	jar cfe graphwar.jar Graphwar.Graphwar GraphServer Graphwar rsc
	jar cfe roomServer.jar RoomServer.RoomServer GraphServer RoomServer rsc
	jar cfe globalServer.jar GlobalServer.GlobalServer GraphServer GlobalServer rsc
	rm -rf Graphwar
	rm -rf GraphServer
	rm -rf GlobalServer
	rm -rf RoomServer

clean:

	rm -r bin
	rm graphwar.jar
	rm roomServer.jar
	rm globalServer.jar
