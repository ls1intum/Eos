# How to release Eos?


We use [JReleaser](https://jreleaser.org/) to upload Eos to Maven Central. In order to so, you have to set up a `~/.jreleaser/config.toeml` file according to [the documentation](https://jreleaser.org/guide/latest/examples/maven/maven-central.html#_gradle).

1. Update the version in `build.gradle`, `README.md` and in the `build.gradle` of the example project / project for building the docker container.
2. Run `./gradlew clean publish` to build the project and publish it to the local Maven staging repository.
3. Run `mkdir ./build/jreleaser && ./gradlew jreleaserDeploy` to upload the project to Maven Central.
4. Commit and push the changes to the repository.
5. Create a new release on GitHub and upload the artifacts from the `build/staging-deploy/de/tum/cit/ase/eos/x.x.x` directory.
6. (This will trigger the CI pipeline to build the Docker container and upload it to Docker Hub.)
