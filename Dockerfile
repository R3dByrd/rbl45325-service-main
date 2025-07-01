FROM ubuntu:latest
RUN apt-get update && apt-get install -y figlet
ENTRYPOINT ["figlet", "-f", "slant"]
CMD ["Cloud Native Computing!"]
