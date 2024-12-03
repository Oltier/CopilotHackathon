To Run app in docker:

Build the container image,

```bash
   docker build -t myapp .
```

Run the container,

```bash
docker run -p 8080:8080 myapp
```

Test the container.

```bash
curl http://localhost:8080/hello?key=world
```

```bash
curl http://localhost:8080/chuck-norris-joke
```