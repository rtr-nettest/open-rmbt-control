package at.rtr.rmbt;

// Source: https://github.com/git-commit-id/git-commit-id-maven-plugin/blob/master/docs/access-version-info-at-runtime.md

public interface Version {
    String TAGS = "${git.tags}";
    String BRANCH = "${git.branch}";
    String DIRTY = "${git.dirty}";
    String REMOTE_ORIGIN_URL = "${git.remote.origin.url}";

    String COMMIT_ID = "${git.commit.id.full}";
    String COMMIT_ID_ABBREV = "${git.commit.id.abbrev}";
    String DESCRIBE = "${git.commit.id.describe}";
    String DESCRIBE_SHORT = "${git.commit.id.describe-short}";
    String COMMIT_USER_NAME = "${git.commit.user.name}";
    String COMMIT_USER_EMAIL = "${git.commit.user.email}";

    String COMMIT_MESSAGE_SHORT = "${git.commit.message.short}";
    String COMMIT_TIME = "${git.commit.time}";
    String CLOSEST_TAG_NAME = "${git.closest.tag.name}";
    String CLOSEST_TAG_COMMIT_COUNT = "${git.closest.tag.commit.count}";

    String BUILD_USER_NAME = "${git.build.user.name}";
    String BUILD_USER_EMAIL = "${git.build.user.email}";
    String BUILD_TIME = "${git.build.time}";
    String BUILD_HOST = "${git.build.host}";
    String BUILD_VERSION = "${git.build.version}";
    String BUILD_NUMBER = "${git.build.number}";
    String BUILD_NUMBER_UNIQUE = "${git.build.number.unique}";
}