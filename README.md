# JavaCrawler

## How to run this project on Docker:
1) Download only the project's DockerFile.

2) Open a terminal / command promt.

3) Execute the following commands from the directory where the downloaded Dockerfile is:

- Create a docker image.
```
  docker build -t java_crawler_image .
```
- Create a container based on the created image.
```
  docker container create -i -t --name java_crawler_container java_crawler_image
```
- Launch the container.
```
  docker start java_crawler_container
```
4) Run the following commands inside the launched container:
- Download the project from GitHub.
```
  git clone https://github.com/lokeneza/JavaCrawler
```
- Get into the directory where the maven/gradle files are.
```
  cd JavaCrawler/JavaCrawler
```
- Run the project.
```
  gradle run
```
