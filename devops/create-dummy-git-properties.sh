#!/bin/bash
# create a dummy git.properties file for development purposes

cat > ../src/main/resources/git.properties << 'EOF'
# Git Properties
git.branch=main
git.commit.id=0000000000000000000000000000000000000000
git.commit.id.abbrev=0000000
git.commit.id.describe=1.0-SNAPSHOT-0-g0000000
git.build.version=1.0-SNAPSHOT
git.commit.time=2024-01-01T00:00:00Z
git.commit.user.name=Developer
git.commit.user.email=developer@example.com
git.commit.message.full=Initial commit
git.commit.message.short=Initial commit
git.dirty=false
git.remote.origin.url=https://github.com/example/repo.git
git.closest.tag.name=
git.closest.tag.commit.count=0
git.tags=
git.build.time=2024-01-01T00:00:00Z
git.build.user.name=Developer
git.build.user.email=developer@example.com
git.total.commit.count=1
git.local.branch.ahead=0
git.local.branch.behind=0
EOF