# Releasing

## Preparing the release environment

### Set up your Sonatype OSSRH account

* Create a [Sonatype OSSRH JIRA account](https://issues.sonatype.org/secure/Signup!default.jspa).
* Create a ticket to request access to the `com.squareup.logcat` project. Here's an example: [OSSRH-54959](https://issues.sonatype.org/browse/OSSRH-54959).
* Then ask someone with deployer role from the team to confirm access.

### Set up your signing key

```bash
# Create a new key
gpg --gen-key
# List local keys. Key id is last 8 characters
gpg -K
cd ~/.gnupg
# Export key locally
gpg --export-secret-keys -o secring.gpg
# Upload key to Ubuntu servers
gpg --send-keys --keyserver keyserver.ubuntu.com <KEY ID>
# Confirm the key can now be found
gpg --recv-keys --keyserver keyserver.ubuntu.com <KEY ID>
```

### Set up your home gradle.properties

Add this to your `~/.gradle/gradle.properties`:

```
signing.keyId=<KEY ID>
signing.password=<KEY PASSWORD>
signing.secretKeyRingFile=/Users/YOUR_USERNAME_/.gnupg/secring.gpg
SONATYPE_NEXUS_USERNAME=<SONATYPE_USERNAME>
SONATYPE_NEXUS_PASSWORD=<SONATYPE_PASSWORD>
```

### Set up GitHub CLI

Install GitHub CLI

```bash
brew install gh
```

Install jq, a CLI Json processor

```bash
brew install jq
```

Set up aliases for milestone management:

```bash
gh alias set listOpenMilestones "api graphql -F owner=':owner' -F name=':repo' -f query='
    query ListOpenMilestones(\$name: String\!, \$owner: String\!) {
        repository(owner: \$owner, name: \$name) {
            milestones(first: 100, states: OPEN) {
                nodes {
                    title
                    number
                    description
                    dueOn
                    url
                    state
                    closed
                    closedAt
                    updatedAt
                }
            }
        }
    }
'"

gh alias set --shell createMilestone "gh api --method POST repos/:owner/:repo/milestones --input - | jq '{ html_url: .html_url, state: .state, created_at: .created_at }'"

gh alias set --shell closeMilestone "echo '{\"state\": \"closed\"}' | gh api --method PATCH repos/:owner/:repo/milestones/\$1 --input - | jq '{ html_url: .html_url, state: .state, closed_at: .closed_at }'"
```

## Releasing

* Create a local release branch from `main`
```bash
git checkout main
git pull
git checkout -b release_{NEW_VERSION}
```

* Update `VERSION_NAME` in `gradle.properties` (remove `-SNAPSHOT`)
```gradle
sed -i '' 's/VERSION_NAME={NEW_VERSION}-SNAPSHOT/VERSION_NAME={NEW_VERSION}/' gradle.properties
```

* Update the changelog
```
mate CHANGELOG.md
```	

* Commit all local changes
```bash
git commit -am "Prepare {NEW_VERSION} release"
```

* Perform a clean build
```bash
./gradlew clean
./gradlew build
```

* Create a tag and push it
```bash
git tag v{NEW_VERSION}
git push origin v{NEW_VERSION}
```

* Upload the artifacts to Sonatype OSS Nexus
```bash
./gradlew uploadArchives --no-daemon --no-parallel
```

* Release to Maven Central
    * Login to Sonatype OSS Nexus: https://oss.sonatype.org/
    * Click on **Staging Repositories**
    * Scroll to the bottom, you should see an entry named `comsquareup-XXXX`
    * Check the box next to the `comsquareup-XXXX` entry, click **Close** then **Confirm**
    * Wait a bit, hit **Refresh**, until the *Status* for that column changes to *Closed*.
    * Check the box next to the `comsquareup-XXXX` entry, click **Release** then **Confirm**
* Merge the release branch to main
```bash
git checkout main
git pull
git merge --no-ff release_{NEW_VERSION}
```
* Update `VERSION_NAME` in `gradle.properties` (increase version and add `-SNAPSHOT`)
```gradle
sed -i '' 's/VERSION_NAME={NEW_VERSION}/VERSION_NAME={NEXT_VERSION}-SNAPSHOT/' gradle.properties
```

* Commit your changes
```bash
git commit -am "Prepare for next development iteration"
```

* Push your changes
```bash
git push
```

* Create a new release
```bash
gh release create v{NEW_VERSION} --title v{NEW_VERSION} --notes 'See [Change Log](https://github.com/square/logcat/blob/main/CHANGELOG.md)'
```

* Wait for the release to be available [on Maven Central](https://repo1.maven.org/maven2/com/squareup/logcat/logcat/).
* Tell your friends, update all of your apps, and tweet the new release. As a nice extra touch, mention external contributions.
