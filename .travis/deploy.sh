#!/bin/bash
cd `dirname $0`/..

if [[ "$TRAVIS_TAG" ]] || [[ "${TRAVIS_BRANCH}" = "master" ]]
then
    if [ -z "$SONATYPE_USERNAME" ]
    then
        echo "Error: Please set SONATYPE_USERNAME and SONATYPE_PASSWORD environment variable"
        exit 1
    fi

    if [ -z "$SONATYPE_PASSWORD" ]
    then
        echo "Error: Please set SONATYPE_PASSWORD environment variable"
        exit 1
    fi

    if [ ! -z "$TRAVIS_TAG" ]
    then
        echo "On a tag -> Set pom.xml <version> to $TRAVIS_TAG"
        mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG 1>/dev/null 2>/dev/null
    else
        echo "Not on a tag -> Keep snapshot version in pom.xml"
    fi

    mvn clean deploy --settings .travis/settings.xml -DskipTests=true -B -U
else
    echo "Not deploying dependant builds for ${TRAVIS_BRANCH}"
fi