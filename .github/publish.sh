fail() {
    echo "$@" 1>&2
    exit 1
}

checkout() {
    echo Checking out newtag = "$NEW_TAG", release type = "$RELEASE_TYPE"
    git fetch --all

    case $RELEASE_TYPE in
      Full)
          git checkout -b "$BRANCH_NAME" || fail "Unable to checkout $BRANCH_NAME";;
      Patch)
          git checkout "$BRANCH_NAME" || fail "Unable to checkout $BRANCH_NAME";;
      Update)
          git checkout -b "$BRANCH_NAME" "$PREV_TAG" || fail "Unable to checkout $BRANCH_NAME";;
    esac
}

set_version() {
    echo Setting version of "$REPO_NAME" to "$NEW_VERSION"

    # Changing the version in version.gradle file
    if [ "$MODULE_NAME" = "netkit" ]; then
       perl -pi -e "s/^ext.netkitVersion.*$/ext.netkitVersion = '$NEW_VERSION'/" $NETKIT_SERVICES_VERSION_FILE
    fi

    perl -pi -e "s/^ext.netkitVersion.*$/ext.netkitVersion = '$NEW_VERSION'/" $VERSION_FILE
    perl -pi -e "s/^ext.dtglibVersion.*$/ext.dtglibVersion = '$NEW_VERSION'/" $VERSION_FILE
    perl -pi -e "s/^ext.playkitVersion.*$/ext.playkitVersion = '$NEW_VERSION'/" $VERSION_FILE
    perl -pi -e "s/^ext.libVersion.*$/ext.libVersion = '$NEW_VERSION'/" $VERSION_FILE

    # Changing the version in build.gradle file
    if [[ "$RELEASE_TYPE" = "Patch" || "$RELEASE_TYPE" = "Update" ]]; then
       echo "RELEASE_TYPE = '$RELEASE_TYPE'"
       perl -pi -e "s/playkit:playkit:$PLAYKIT_PREV_VERSION/playkit:playkit:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/playkit:playkitproviders:$PLAYKIT_PREV_VERSION/playkit:playkitproviders:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/playkit:kavaplugin:$PLAYKIT_PREV_VERSION/playkit:kavaplugin:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/playkit:broadpeakplugin:$PLAYKIT_PREV_VERSION/playkit:broadpeakplugin:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/playkit:smartswitchplugin:$PLAYKIT_PREV_VERSION/playkit:smartswitchplugin:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/player:tvplayer:$PLAYKIT_PREV_VERSION/player:tvplayer:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/dtg:dtglib:$DTG_PREV_VERSION/dtg:dtglib:$DTG_DEP_VERSION/" $BUILD_GRADLE
    fi

    if [ "$RELEASE_TYPE" = "Full" ]; then
       echo "RELEASE_TYPE = '$RELEASE_TYPE'"
       perl -pi -e "s/:playkit-android:dev-SNAPSHOT/.playkit:playkit:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/:playkit-android-providers:develop-SNAPSHOT/.playkit:playkitproviders:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/:playkit-android-kava:develop-SNAPSHOT/.playkit:kavaplugin:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/:kaltura-player-android:develop-SNAPSHOT/.player:tvplayer:$NEW_VERSION/" $BUILD_GRADLE
       perl -pi -e "s/:playkit-dtg-android:current-SNAPSHOT/.dtg:dtglib:$DTG_DEP_VERSION/" $BUILD_GRADLE
    fi
}

build() {
    chmod +x gradlew
    ./gradlew netkit:publishToSonatype #closeAndReleaseSonatypeStagingRepository
}

release_and_tag() {
    git config user.name "Github Actions Bot KLTR"
    git config user.email "<>"

    echo Releasing version $NEW_VERSION of $REPO_NAME to GitHub
    set +e
    git add $VERSION_FILE || fail "Version file not found $VERSION_FILE"
    git add $BUILD_GRADLE || fail "Build file not found $BUILD_GRADLE"
    git commit -m "Update version to $NEW_TAG"
    set -e
    git push origin HEAD:$BRANCH_NAME || fail "Unable to push $BRANCH_NAME"

    if [[ "$RELEASE_TYPE" = "Patch" || "$RELEASE_TYPE" = "Full" ]]; then

cat << EOF > ./post.json
{
      "name": "$NEW_TAG",
      "body": "## Changes from [$PREV_TAG](https://github.com/GouravSna/$REPO_NAME/releases/tag/$PREV_TAG)\n\nTBD",
      "tag_name": "$NEW_TAG",
      "target_commitish": "$BRANCH_NAME"
}
EOF
    fi

    if [ "$RELEASE_TYPE" = "Update" ]; then
                  JSON_BODY="### Plugin Playkit Support\n\n"
                  JSON_BODY="$JSON_BODY$NEW_TAG\n\n"
      JSON_BODY="$JSON_BODY * upgrade to $NEW_TAG\n\n"
      JSON_BODY="$JSON_BODY #### Gradle\n\n"
                  JSON_BODY="$JSON_BODY * implementation 'com.kaltura.playkit:"
      JSON_BODY="$JSON_BODY$MODULE_NAME:$NEW_VERSION"
      JSON_BODY="$JSON_BODY'"


cat << EOF > ./post.json
{
      "name": "$NEW_TAG",
      "body": "## Changes from [$PREV_TAG](https://github.com/GouravSna/$REPO_NAME/releases/tag/$PREV_TAG)\n\n$JSON_BODY",
      "tag_name": "$NEW_TAG",
      "target_commitish": "$BRANCH_NAME"
}
EOF
    fi

    cat post.json

    curl --request POST \
         --url https://api.github.com/repos/GouravSna/GH-TEST/releases \
         --header "authorization: Bearer $TOKEN" \
         --header 'content-type: application/json' \
         -d@post.json

    rm ./post.json

    # delete temp branch
    #git push origin --delete $BRANCH_NAME
}

notify_teams() {

color=0072C6
  curl "$TEAMS_WEBHOOK" -d @- << EOF
  {
      "@context": "https://schema.org/extensions",
      "@type": "MessageCard",
      "themeColor": "$color",
      "title": "$REPO_NAME $NEW_VERSION",
      "text": "🎉 Release Ready",
      "sections": [
          {
              "facts": [
                  {
                      "name": "Tag",
                      "value": "$NEW_TAG"
                  },
                  {
                      "name": "Version",
                      "value": "$NEW_VERSION"
                  },
                  {
                      "name": "Gradle line",
                      "value": "TBD"
                  }
              ]
          }
      ],
      "potentialAction": [
          {
              "@type": "OpenUri",
              "name": "GitHub Release Page",
              "targets": [
                  {
                      "os": "default",
                      "uri": "$RELEASE_URL"
                  }
              ]
          }
      ]
  }
EOF

}

  RELEASE_TYPE=$RELEASE_TYPE

  REPO_NAME=$REPO_NAME
  MODULE_NAME=$MODULE_NAME
  VERSION_FILE=$MODULE_NAME/version.gradle
  BUILD_GRADLE=$MODULE_NAME/build.gradle
  NETKIT_SERVICES_VERSION_FILE="services/version.gradle" #Special variable only for netkit to change the version file version

  REPO_URL=https://github.com/GouravSna/$REPO_NAME
  NEW_VERSION=$NEW_VERSION
  PLAYKIT_PREV_VERSION=$PLAYKIT_PREV_VERSION
  DTG_DEP_VERSION=$DTG_DEP_VERSION
  TOKEN=$TOKEN
  TEAMS_WEBHOOK=$TEAMS_WEBHOOK

  NEW_TAG=v$NEW_VERSION
  PREV_TAG=v$PLAYKIT_PREV_VERSION
  RELEASE_URL=$REPO_URL/releases/tag/$NEW_TAG

  if [[ "$RELEASE_TYPE" = "Full" || "$RELEASE_TYPE" = "Update" ]]; then
  BRANCH_NAME="release/$NEW_TAG"
  fi

  if [ "$RELEASE_TYPE" = "Patch" ]; then
  BRANCH_NAME="patch/$NEW_TAG"
  fi

#  echo "BRANCH_NAME = '$BRANCH_NAME'"
#  echo "RELEASE_TYPE = '$RELEASE_TYPE'"
#  echo "REPO_NAME = '$REPO_NAME'"
#  echo "MODULE_NAME = '$MODULE_NAME'"
#  echo "VERSION_FILE = '$VERSION_FILE'"
#  echo "BUILD_GRADLE = '$BUILD_GRADLE'"
#  echo "REPO_URL = '$REPO_URL'"
#  echo "NEW_VERSION = '$NEW_VERSION'"
#  echo "PLAYKIT_PREV_VERSION = '$PLAYKIT_PREV_VERSION'"
#  echo "DTG_DEP_VERSION = '$DTG_DEP_VERSION'"
#  echo "NEW_TAG = '$NEW_TAG'"
#  echo "PREV_TAG = '$PREV_TAG'"
#  echo "RELEASE_URL = '$RELEASE_URL'"
#  echo "TOKEN = '$TOKEN'"
#  echo "TEAMS_WEBHOOK = '$TEAMS_WEBHOOK'"

  checkout
  set_version
  build
  release_and_tag
  notify_teams
