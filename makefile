
all:
	docker run --rm \
		-v ${PWD}:/compile \
		graphwar/build

docker-build:
	## build docker image
	docker build -t graphwar/build .

run-client:
	docker run --rm \
		-v ${PWD}:/compile \
		-v /tmp/.X11-unix:/tmp/.X11-unix \
		-e DISPLAY=${DISPLAY} \
		graphwar/build \
		java -jar graphwar.jar

clean:

	rm -r bin
	rm graphwar.jar
	rm roomServer.jar
	rm globalServer.jar
