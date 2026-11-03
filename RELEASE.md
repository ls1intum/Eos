# How to release Eos?

Eos is published to Maven Central under the namespace `de.tum.cit.aet` via the [Central Publisher Portal](https://central.sonatype.com).
The upload and the signing are done by [JReleaser](https://jreleaser.org/guide/latest/examples/maven/maven-central.html) (Gradle plugin `org.jreleaser`).

## One-time setup

1. **Namespace:** Your Portal account needs access to the `de.tum.cit.aet` namespace (see [Namespaces](https://central.sonatype.com/publishing/namespaces)).
2. **User token:** Generate a user token in the Portal (Account → *Generate User Token*). The normal login password does not work.
3. **GPG key:** Create a key pair (`gpg --full-generate-key`) and publish the public key to a keyserver, e.g. `gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>`. Maven Central verifies the signatures against it.
4. **Credentials:** Store them in `~/.jreleaser/config.toml` (never commit this file):

```toml
JRELEASER_MAVENCENTRAL_USERNAME = "<token username>"
JRELEASER_MAVENCENTRAL_PASSWORD = "<token password>"
JRELEASER_GPG_PASSPHRASE = "<passphrase>"
JRELEASER_GPG_PUBLIC_KEY = """-----BEGIN PGP PUBLIC KEY BLOCK-----
...
-----END PGP PUBLIC KEY BLOCK-----"""
JRELEASER_GPG_SECRET_KEY = """-----BEGIN PGP PRIVATE KEY BLOCK-----
...
-----END PGP PRIVATE KEY BLOCK-----"""
```

The armored keys can be exported with `gpg --armor --export <KEY_ID>` and `gpg --armor --export-secret-keys <KEY_ID>`.
The same keys can also be passed as environment variables with the same names.

## Release

All commands use Java 25 (e.g. `export JAVA_HOME=$(/usr/libexec/java_home -v 25)` on macOS).

1. **Bump the version** in:
   - `build.gradle` (`version`)
   - `Readme.md` (dependency and Docker image tag)
   - `CITATION.cff` (`version`)
   - `example/tests/build.gradle` and `docker/artemis-java-template/build.gradle` (`eosVersion`)

   When updating Ares, keep `ares_version` in `gradle.properties` and `aresVersion` in the example and Docker `build.gradle` files in sync.
2. **Dry run** (signs and validates the artifacts locally, uploads nothing):
   ```bash
   ./gradlew clean publish jreleaserDeploy --dryrun
   ```
   `publish` stages the artifacts in `build/staging-deploy`; JReleaser signs them and checks the Maven Central requirements.
3. **Deploy to Maven Central:**
   ```bash
   ./gradlew clean publish jreleaserDeploy
   ```
   JReleaser uploads the bundle and polls the Portal until the deployment has been processed (this can take a few minutes).
   Check the deployment under [Deployments](https://central.sonatype.com/publishing/deployments); if it is *Validated* but not yet published, click *Publish*.
   It can take a while until the version is visible on [Maven Central](https://repo1.maven.org/maven2/de/tum/cit/aet/eos/).
4. **Commit and push** the version bump.
5. **Create a GitHub release** with the tag of the version (e.g. `2.0.0`) and attach the artifacts from `build/staging-deploy/de/tum/cit/aet/eos/<version>`.
   The release triggers the CI pipeline, which builds the Docker image `ghcr.io/ls1intum/eos:<version>` and tests the example with it.
   The Docker build downloads Eos from Maven Central, so the deployment has to be published first.
6. Remove a locally published version from `~/.m2/repository/de/tum/cit/aet/eos/<version>` (if any), so local builds use the released artifact.

## Troubleshooting

- **`NoClassDefFoundError: org/eclipse/jgit/lib/GpgObjectSigner`:** another Gradle plugin pulled in JGit 7. `build.gradle` pins JGit 5.x for the build classpath; keep that block when updating plugins.
- **401/403 on upload:** the user token in `config.toml` is invalid or expired, or the account has no access to the namespace.
- **Signature rejected:** the public key has not been published to a keyserver yet.
